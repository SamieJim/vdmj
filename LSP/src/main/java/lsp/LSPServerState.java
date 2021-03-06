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

package lsp;

import workspace.WorkspaceManager;

public class LSPServerState
{
	private boolean running = false;
	private boolean initialized = false;
	private WorkspaceManager manager = null;
	
	public boolean isInitialized()
	{
		return initialized;
	}
	
	public void setInitialized(boolean set)
	{
		initialized = set;
	}

	public WorkspaceManager getManager()
	{
		return manager;
	}

	public void setManager(WorkspaceManager manager)
	{
		this.manager = manager;
	}

	public void setRunning(boolean running)
	{
		this.running = running;
	}

	public boolean isRunning()
	{
		return running;
	}
}
