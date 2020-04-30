/*******************************************************************************
 *
 *	Copyright (c) 2018 Nick Battle.
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

package com.fujitsu.vdmj.in.annotations;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.fujitsu.vdmj.in.expressions.INExpression;
import com.fujitsu.vdmj.in.expressions.INExpressionList;
import com.fujitsu.vdmj.in.modules.INModule;
import com.fujitsu.vdmj.in.statements.INStatement;
import com.fujitsu.vdmj.runtime.Context;
import com.fujitsu.vdmj.tc.lex.TCIdentifierToken;
import com.fujitsu.vdmj.values.Value;

public abstract class INAnnotation
{
	public final TCIdentifierToken name;
	
	public final INExpressionList args;
	
	private static final Set<Class<? extends INAnnotation>> declared = new HashSet<Class<? extends INAnnotation>>(); 
	private static final List<INAnnotation> instances = new Vector<INAnnotation>(); 

	public INAnnotation(TCIdentifierToken name, INExpressionList args)
	{
		this.name = name;
		this.args = args;

		declared.add(this.getClass());
		instances.add(this);
	}
	
	public static void reset()
	{
		declared.clear();
		instances.clear();
	}

	public static void init(Context ctxt)
	{
		for (Class<?> clazz: declared)
		{
			try
			{
				Method doInit = clazz.getMethod("doInit", (Class<?>[])null);
				doInit.invoke(null, (Object[])null);
			}
			catch (Throwable e)
			{
				throw new RuntimeException(clazz.getSimpleName() + ":" + e);
			}
		}
		
		for (INAnnotation annotation: instances)
		{
			annotation.doInit(ctxt);
		}
	}
	
	public static void doInit()
	{
		// Nothing by default
	}

	protected void doInit(Context ctxt)
	{
		// Nothing by default
	}

	@Override
	public String toString()
	{
		return "@" + name + (args.isEmpty() ? "" : "(" + args + ")");
	}

	public void inBefore(INStatement stmt, Context ctxt)
	{
		// Do nothing
	}

	public void inBefore(INModule stmt, Context ctxt)
	{
		// Do nothing
	}
	
	public void inBefore(INExpression exp, Context ctxt)
	{
		// Do nothing
	}

	public void inAfter(INStatement stmt, Value rv, Context ctxt)
	{
		// Do nothing
	}
	
	public void inAfter(INExpression exp, Value rv, Context ctxt)
	{
		// Do nothing
	}
}
