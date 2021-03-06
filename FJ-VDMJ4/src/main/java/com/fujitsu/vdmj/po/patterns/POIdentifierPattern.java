/*******************************************************************************
 *
 *	Copyright (c) 2016 Fujitsu Services Ltd.
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

package com.fujitsu.vdmj.po.patterns;

import com.fujitsu.vdmj.po.definitions.PODefinitionList;
import com.fujitsu.vdmj.po.definitions.POLocalDefinition;
import com.fujitsu.vdmj.po.expressions.POExpression;
import com.fujitsu.vdmj.po.expressions.POVariableExpression;
import com.fujitsu.vdmj.tc.lex.TCNameList;
import com.fujitsu.vdmj.tc.lex.TCNameToken;
import com.fujitsu.vdmj.tc.types.TCType;
import com.fujitsu.vdmj.tc.types.TCUnknownType;

public class POIdentifierPattern extends POPattern
{
	private static final long serialVersionUID = 1L;
	public final TCNameToken name;

	public POIdentifierPattern(TCNameToken token)
	{
		super(token.getLocation());
		this.name = token;
	}

	@Override
	public int getLength()
	{
		return ANY;	// Special value meaning "any length"
	}

	@Override
	public String toString()
	{
		return name.toString();
	}

	@Override
	public PODefinitionList getAllDefinitions(TCType ptype)
	{
		PODefinitionList defs = new PODefinitionList();
		defs.add(new POLocalDefinition(location, name, ptype));
		return defs;
	}

	@Override
	public TCNameList getAllVariableNames()
	{
		TCNameList list = new TCNameList();
		list.add(name);
		return list;
	}

	@Override
	public TCType getPossibleType()
	{
		return new TCUnknownType(location);
	}

	@Override
	public POExpression getMatchingExpression()
	{
		return new POVariableExpression(name, null);
	}

	@Override
	public boolean isSimple()
	{
		return false;
	}

	@Override
	public boolean alwaysMatches()
	{
		return true;
	}

	@Override
	public <R, S> R apply(POPatternVisitor<R, S> visitor, S arg)
	{
		return visitor.caseIdentifierPattern(this, arg);
	}
}
