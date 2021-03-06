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

package dap.handlers;

import java.io.File;
import java.io.IOException;

import dap.DAPHandler;
import dap.DAPMessageList;
import dap.DAPRequest;
import dap.DAPServerState;
import json.JSONArray;
import json.JSONObject;

public class SetBreakpointsHandler extends DAPHandler
{
	public SetBreakpointsHandler(DAPServerState state)
	{
		super(state);
	}
	
	@Override
	public DAPMessageList run(DAPRequest request) throws IOException
	{
		try
		{
			JSONObject arguments = request.get("arguments");
			JSONObject source = arguments.get("source");
			File file = new File((String)source.get("path")).getCanonicalFile();
			JSONArray lines = arguments.get("lines");
			
			return dapServerState.getManager().setBreakpoints(request, file, lines);
		}
		catch (Exception e)
		{
			return new DAPMessageList(request, e);
		}
	}
}
