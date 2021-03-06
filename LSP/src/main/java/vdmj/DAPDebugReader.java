/*******************************************************************************
 *
 *	Copyright (c) 2020 Nick Battle.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package vdmj;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;

import com.fujitsu.vdmj.debug.DebugCommand;
import com.fujitsu.vdmj.debug.DebugLink;
import com.fujitsu.vdmj.debug.DebugType;
import com.fujitsu.vdmj.debug.TraceCallback;
import com.fujitsu.vdmj.runtime.Context;
import com.fujitsu.vdmj.runtime.Tracepoint;
import com.fujitsu.vdmj.scheduler.SchedulableThread;

import dap.DAPMessageList;
import dap.DAPRequest;
import dap.DAPResponse;
import dap.DAPServer;
import json.JSONArray;
import json.JSONObject;
import workspace.Log;

/**
 * A class to listen for and interact with multiple threads that are being debugged.
 */
public class DAPDebugReader extends Thread implements TraceCallback
{
	private static final int TIMEOUT = 200;		// Before we suspect trouble
	private final DAPServer server;
	private final DAPDebugLink link;

	private SchedulableThread debuggedThread = null;
	
	public DAPDebugReader() throws Exception
	{
		setName("DAPDebugReader");
		DAPDebugExecutor.init();
		server = DAPServer.getInstance();
		link = (DAPDebugLink)DebugLink.getInstance();
	}
	
	@Override
	public void run()
	{
		link.setTraceCallback(this);
		
		while (link.waitForStop())
		{
			try
			{
				debuggedThread = link.getDebugThread();
				Log.printf("----------------- DEBUG STOP in %s", debuggedThread.getName());
				prompt();
				
				if (doCommand(true))	// timeout first command
				{
					while (doCommand(false));
				}
				
				Log.printf("----------------- RESUME");
			}
			catch (Exception e)
			{
				Log.error(e);
				break;
			}
		}
	}
	
	private boolean doCommand(boolean timed) throws Exception
	{
		try
		{
			JSONObject dapMessage = null;
			int retries = 2;
			
			while (--retries > 0)
			{
				try
				{
					dapMessage = server.readMessage(timed ? TIMEOUT : 0);
				}
				catch (SocketTimeoutException e)
				{
					Log.error(e);
					Log.error("Expecting request from client?");
					
					// Trying kicking the client with an event to say all threads stopped
					List<SchedulableThread> threads = link.getThreads();
					
					server.writeMessage(new DAPResponse("stopped",
							new JSONObject(
									"reason", "step",
									"threadId", threads.get(0).getId(),
									"allThreadsStopped", "true")));
				}
			}

			if (dapMessage == null)	// EOF - closed socket?
			{
				Log.printf("End of stream detected");
				link.killThreads();
				return false;
			}

			DAPRequest dapRequest = new DAPRequest(dapMessage);

			switch ((String)dapRequest.get("command"))
			{
				case "threads":
					server.writeMessage(doThreads(dapRequest));
					return true;

				case "setBreakpoints":
					JSONObject arguments = dapRequest.get("arguments");
					JSONObject source = arguments.get("source");
					File file = new File((String)source.get("path")).getCanonicalFile();
					JSONArray lines = arguments.get("lines");
					DAPMessageList responses = server.getState().getManager().setBreakpoints(dapRequest, file, lines);

					for (JSONObject response: responses)
					{
						server.writeMessage(response);
					}

					return true;

				default:
					DebugCommand command = parse(dapRequest);
					SchedulableThread targetThread = threadFor(dapRequest);

					if (command.getType() == null)	// Ignore - payload is DAP response
					{
						server.writeMessage((JSONObject) command.getPayload());
						return true;
					}

					DebugCommand response = link.sendCommand(targetThread, command);
					DAPResponse dapResponse = new DAPResponse(dapRequest, true, null, response.getPayload());

					switch (response.getType())
					{
						case RESUME:
							link.resumeThreads();
							server.writeMessage(dapResponse);
							return false;

						case STOP:
						case QUIT:
						case TERMINATE:
							link.killThreads();
							server.writeMessage(dapResponse);
							return false;

						case PRINT:
							server.writeMessage(dapResponse);
							prompt();
							return true;

						default:
							server.writeMessage(dapResponse);
							return true;
					}
			}
		}
		catch (Exception e)
		{
			Log.error(e);
			throw e;
		}
	}

	private SchedulableThread threadFor(DAPRequest request)
	{
		JSONObject arguments = request.get("arguments");
		Long th = arguments.get("threadId");
		
		if (th != null)		// Command has a threadId target
		{
			for (SchedulableThread thread: link.getThreads())
			{
				if (thread.getId() == th.longValue())
				{
					return thread;
				}
			}

			Log.error("Cannot find threadId %s", th);
		}

		return debuggedThread;
	}

	private DebugCommand parse(DAPRequest request) throws IOException
	{
		String command = request.get("command");
		
		switch (command)
		{
			case "terminate":
				return DebugCommand.TERMINATE;
				
			case "evaluate":
				return new DebugCommand(DebugType.PRINT, request.get("arguments"));
				
			case "continue":
				return DebugCommand.CONTINUE;
				
			case "stepIn":
				return DebugCommand.STEP;
				
			case "stepOut":
				return DebugCommand.OUT;
				
			case "next":
				return DebugCommand.NEXT;
				
			case "stackTrace":
				return new DebugCommand(DebugType.STACK, request.get("arguments"));
			
			case "scopes":
				return new DebugCommand(DebugType.SCOPES, request.get("arguments"));
				
			case "variables":
				return new DebugCommand(DebugType.VARIABLES, request.get("arguments"));
				
			default:
				return new DebugCommand(null, new DAPResponse(request, false, "Unsupported command: " + command, null));
		}
	}

	private DAPResponse doThreads(DAPRequest request)
	{
		List<SchedulableThread> threads = link.getThreads();
		Collections.sort(threads);
		JSONArray list = new JSONArray();
		
		for (SchedulableThread thread: threads)
		{
			list.add(new JSONObject(
				"id",	thread.getId(),
				"name", thread.getName()));
		}
		
		return new DAPResponse(request, true, null, new JSONObject("threads", list));
	}
	
	@Override
	public String toString()
	{
		return getName();
	}
	
	@Override
	public void tracepoint(Context ctxt, Tracepoint tp)
	{
		try
		{
			if (tp.condition == null)
			{
				String s = "Reached trace point [" + tp.number + "]";
				text(Thread.currentThread().getName() + ": " + s);
			}
			else
			{
				String result = null;
				
				try
				{
					result = tp.condition.eval(ctxt).toString();
				}
				catch (Exception e)
				{
					result = e.getMessage();
				}
				
				String s = tp.trace + " = " + result + " at trace point [" + tp.number + "]";
				text(Thread.currentThread().getName() + ": " + s);
			}
		}
		catch (IOException e)
		{
			Log.error(e);
		}
	}
	
	private void prompt() throws IOException
	{
		text("[debug]> ");
	}

	private void text(String message) throws IOException
	{
		if (System.getProperty("lsp.prompts") != null)
		{
			server.writeMessage(new DAPResponse("output", new JSONObject("output", message)));
		}
	}
}
