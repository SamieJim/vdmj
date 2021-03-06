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

package com.fujitsu.vdmj.tc.patterns;

import com.fujitsu.vdmj.tc.types.TCTypeSet;
import com.fujitsu.vdmj.typechecker.Environment;

public class TCBindExitChecker extends TCBindVisitor<TCTypeSet, Environment>
{
	private com.fujitsu.vdmj.tc.expressions.TCExitChecker expVisitor =
			new com.fujitsu.vdmj.tc.expressions.TCExitChecker();

	@Override
	public TCTypeSet caseBind(TCBind node, Environment arg)
	{
		return new TCTypeSet();
	}
	
	@Override
	public TCTypeSet caseSeqBind(TCSeqBind node, Environment base)
	{
		return node.sequence.apply(expVisitor, base);
	}

	@Override
	public TCTypeSet caseSetBind(TCSetBind node, Environment base)
	{
		return node.set.apply(expVisitor, base);
	}
	
	@Override
	public TCTypeSet caseTypeBind(TCTypeBind node, Environment base)
	{
		return new TCTypeSet();
	}
}
