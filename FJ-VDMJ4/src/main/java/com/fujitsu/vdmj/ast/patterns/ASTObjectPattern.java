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

package com.fujitsu.vdmj.ast.patterns;

import com.fujitsu.vdmj.ast.lex.LexNameToken;
import com.fujitsu.vdmj.ast.types.ASTType;
import com.fujitsu.vdmj.ast.types.ASTUnresolvedType;
import com.fujitsu.vdmj.lex.LexLocation;
import com.fujitsu.vdmj.util.Utils;

public class ASTObjectPattern extends ASTPattern
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken classname;
	public final ASTNamePatternPairList fieldlist;
	public final ASTType type;

	public ASTObjectPattern(LexLocation location, LexNameToken classname, ASTNamePatternPairList fieldlist)
	{
		super(location);
		this.classname = classname;
		this.fieldlist = fieldlist;
		this.type = new ASTUnresolvedType(classname);
	}

	@Override
	public String toString()
	{
		return "obj_" + type + "(" + Utils.listToString(fieldlist) + ")";
	}

	@Override
	public <R, S> R apply(ASTPatternVisitor<R, S> visitor, S arg)
	{
		return visitor.caseObjectPattern(this, arg);
	}
}
