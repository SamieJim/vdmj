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
import com.fujitsu.vdmj.runtime.ContextException;
import com.fujitsu.vdmj.runtime.ExceptionHandler;
import com.fujitsu.vdmj.runtime.ValueException;
import com.fujitsu.vdmj.values.NaturalValue;
import com.fujitsu.vdmj.values.Value;

public class INCardinalityExpression extends INUnaryExpression
{
	private static final long serialVersionUID = 1L;

	public INCardinalityExpression(LexLocation location, INExpression exp)
	{
		super(location, exp);
	}

	@Override
	public String toString()
	{
		return "(card " + exp + ")";
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
			return new NaturalValue(exp.eval(ctxt).setValue(ctxt).size());
		}
		catch (ValueException e)
		{
			return abort(e);
		}
        catch (ContextException e)
        {
        	ExceptionHandler.handle(e);		// To avoid case below
        	return null;
        }
		catch (Exception e)
		{
			return abort(4065, e.getMessage(), ctxt);
		}
	}

	@Override
	public <R, S> R apply(INExpressionVisitor<R, S> visitor, S arg)
	{
		return visitor.caseCardinalityExpression(this, arg);
	}
}
