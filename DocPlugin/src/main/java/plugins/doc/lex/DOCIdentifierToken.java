/*******************************************************************************
 *
 *	Copyright (c) 2019 Paul Chisholm
 *
 *	Author: Paul Chisholm
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
 
package plugins.doc.lex;

import plugins.doc.lex.DOCToken;

import com.fujitsu.vdmj.lex.LexLocation;
import com.fujitsu.vdmj.tc.lex.TCIdentifierToken;

public class DOCIdentifierToken extends DOCToken
{
	private static final long serialVersionUID = 1L;
	public final LexLocation location;
	protected final TCIdentifierToken token;

	public DOCIdentifierToken(TCIdentifierToken token)
	{
		this.location = token.getLocation();
		this.token = token;
	}

	@Override
	public void extent(int maxWidth)
	{
		return;
	}
	
	@Override
	public String toHTML(int indent)
	{
		return null;
	}
}
