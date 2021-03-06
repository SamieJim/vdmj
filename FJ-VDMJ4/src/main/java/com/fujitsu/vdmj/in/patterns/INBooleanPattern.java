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

package com.fujitsu.vdmj.in.patterns;

import java.util.List;
import java.util.Vector;

import com.fujitsu.vdmj.ast.lex.LexBooleanToken;
import com.fujitsu.vdmj.runtime.Context;
import com.fujitsu.vdmj.runtime.PatternMatchException;
import com.fujitsu.vdmj.runtime.ValueException;
import com.fujitsu.vdmj.tc.types.TCBooleanType;
import com.fujitsu.vdmj.tc.types.TCType;
import com.fujitsu.vdmj.values.NameValuePairList;
import com.fujitsu.vdmj.values.Value;

public class INBooleanPattern extends INPattern
{
	private static final long serialVersionUID = 1L;
	public final LexBooleanToken value;

	public INBooleanPattern(LexBooleanToken token)
	{
		super(token.location);
		this.value = token;
	}

	@Override
	public String toString()
	{
		return value.toString();
	}

	@Override
	public List<NameValuePairList> getAllNamedValues(Value expval, Context ctxt)
		throws PatternMatchException
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();

		try
		{
			if (expval.boolValue(ctxt) != value.value)
			{
				patternFail(4106, "Boolean pattern match failed");
			}
		}
		catch (ValueException e)
		{
			patternFail(e);
		}

		result.add(new NameValuePairList());
		return result;
	}

	@Override
	public TCType getPossibleType()
	{
		return new TCBooleanType(location);
	}

	@Override
	public <R, S> R apply(INPatternVisitor<R, S> visitor, S arg)
	{
		return visitor.caseBooleanPattern(this, arg);
	}
}
