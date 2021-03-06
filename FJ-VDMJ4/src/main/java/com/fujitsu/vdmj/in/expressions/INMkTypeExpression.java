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

package com.fujitsu.vdmj.in.expressions;

import com.fujitsu.vdmj.lex.LexLocation;
import com.fujitsu.vdmj.runtime.Context;
import com.fujitsu.vdmj.runtime.ValueException;
import com.fujitsu.vdmj.tc.lex.TCNameList;
import com.fujitsu.vdmj.tc.types.TCRecordType;
import com.fujitsu.vdmj.util.Utils;
import com.fujitsu.vdmj.values.RecordValue;
import com.fujitsu.vdmj.values.Value;
import com.fujitsu.vdmj.values.ValueList;

public class INMkTypeExpression extends INExpression
{
	private static final long serialVersionUID = 1L;
	public final TCRecordType recordType;
	public final INExpressionList args;

	public INMkTypeExpression(LexLocation location, TCRecordType recordType, INExpressionList args)
	{
		super(location);
		this.recordType = recordType;
		this.args = args;
	}

	@Override
	public String toString()
	{
		return "mk_" + recordType.name + "(" + Utils.listToString(args) + ")";
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		ValueList argvals = new ValueList();

		for (INExpression e: args)
		{
			argvals.add(e.eval(ctxt));
		}

		try
		{
    		return new RecordValue(recordType, argvals, ctxt);
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public INExpression findExpression(int lineno)
	{
		INExpression found = super.findExpression(lineno);
		if (found != null) return found;

		return args.findExpression(lineno);
	}

	@Override
	public ValueList getValues(Context ctxt)
	{
		return args.getValues(ctxt);
	}

	@Override
	public TCNameList getOldNames()
	{
		return args.getOldNames();
	}

	@Override
	public <R, S> R apply(INExpressionVisitor<R, S> visitor, S arg)
	{
		return visitor.caseMkTypeExpression(this, arg);
	}
}
