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

package vdmj;

import java.io.File;
import java.util.Set;

import com.fujitsu.vdmj.ast.lex.LexNameToken;
import com.fujitsu.vdmj.lex.LexLocation;
import com.fujitsu.vdmj.tc.TCNode;
import com.fujitsu.vdmj.tc.definitions.TCClassDefinition;
import com.fujitsu.vdmj.tc.definitions.TCClassList;
import com.fujitsu.vdmj.tc.definitions.TCDefinition;
import com.fujitsu.vdmj.tc.expressions.TCMkTypeExpression;
import com.fujitsu.vdmj.tc.expressions.TCVariableExpression;
import com.fujitsu.vdmj.tc.modules.TCModule;
import com.fujitsu.vdmj.tc.modules.TCModuleList;
import com.fujitsu.vdmj.tc.statements.TCCallObjectStatement;
import com.fujitsu.vdmj.tc.statements.TCCallStatement;
import com.fujitsu.vdmj.tc.statements.TCIdentifierDesignator;
import com.fujitsu.vdmj.tc.types.TCUnresolvedType;
import com.fujitsu.vdmj.typechecker.ModuleEnvironment;
import com.fujitsu.vdmj.typechecker.NameScope;
import com.fujitsu.vdmj.typechecker.PrivateClassEnvironment;
import com.fujitsu.vdmj.typechecker.PublicClassEnvironment;

import workspace.Log;

public class LSPDefinitionFinder
{
	public TCDefinition find(TCModuleList modules, File file, int line, int col)
	{
		return find(modules, new LexLocation(file, "?", line, col, line, col));
	}
	
	public TCDefinition find(TCModuleList modules, LexLocation position)
	{
		LSPDefinitionLocationFinder finder = new LSPDefinitionLocationFinder();
		
		for (TCModule module: modules)
		{
			// Only explicit modules have a span, called (say) "M`M"
			LexNameToken sname = new LexNameToken(module.name.getName(), module.name.getLex());
			LexLocation span = LexLocation.getSpan(sname);
			
			if (span == null || position.within(span))
			{
				ModuleEnvironment env = new ModuleEnvironment(module);
				
				for (TCDefinition def: module.defs)
				{
					Set<TCNode> nodes = def.apply(finder, position);
					
					if (nodes != null && !nodes.isEmpty())	// found it!
					{
						TCNode node = nodes.iterator().next();
						
						if (node instanceof TCVariableExpression)
						{
							TCVariableExpression vexp = (TCVariableExpression)node;
							return vexp.getDefinition();
						}
						else if (node instanceof TCCallStatement)
						{
							TCCallStatement stmt = (TCCallStatement)node;
							return stmt.getDefinition();
						}
						else if (node instanceof TCCallObjectStatement)
						{
							TCCallObjectStatement stmt = (TCCallObjectStatement)node;
							return stmt.getDefinition();
						}
						else if (node instanceof TCUnresolvedType)
						{
							TCUnresolvedType unresolved = (TCUnresolvedType)node;
							return env.findType(unresolved.typename, module.name.getName());
						}
						else if (node instanceof TCMkTypeExpression)
						{
							TCMkTypeExpression mk = (TCMkTypeExpression)node;
							return env.findType(mk.typename, module.name.getName());
						}
						
						Log.error("TCNode located, but unable to find definition %s", position);
						return null;
					}
				}
			}
		}

		Log.error("Unable to locate symbol %s", position);
		return null;
	}
	
	public TCDefinition find(TCClassList classes, File file, int line, int col)
	{
		return find(classes, new LexLocation(file, "?", line, col, line, col));
	}
	
	public TCDefinition find(TCClassList classes, LexLocation position)
	{
		LSPDefinitionLocationFinder finder = new LSPDefinitionLocationFinder();
		PublicClassEnvironment globals = new PublicClassEnvironment(classes); 
		
		for (TCClassDefinition cdef: classes)
		{
			LexLocation span = LexLocation.getSpan(cdef.name.getLex());
			
			if (span == null || position.within(span))
			{
				PrivateClassEnvironment env = new PrivateClassEnvironment(cdef, globals);
				
				for (TCDefinition def: cdef.definitions)
				{
					Set<TCNode> nodes = def.apply(finder, position);
					
					if (nodes != null && !nodes.isEmpty())	// found it!
					{
						TCNode node = nodes.iterator().next();
						
						if (node instanceof TCVariableExpression)
						{
							TCVariableExpression vexp = (TCVariableExpression)node;
							return vexp.getDefinition();
						}
						else if (node instanceof TCCallStatement)
						{
							TCCallStatement stmt = (TCCallStatement)node;
							return stmt.getDefinition();
						}
						else if (node instanceof TCCallObjectStatement)
						{
							TCCallObjectStatement stmt = (TCCallObjectStatement)node;
							return stmt.getDefinition();
						}
						else if (node instanceof TCIdentifierDesignator)
						{
							TCIdentifierDesignator id = (TCIdentifierDesignator)node;
							return env.findName(id.name, NameScope.NAMESANDSTATE);
						}
						else if (node instanceof TCUnresolvedType)
						{
							TCUnresolvedType unresolved = (TCUnresolvedType)node;
							return env.findType(unresolved.typename, cdef.name.getName());
						}
						else if (node instanceof TCMkTypeExpression)
						{
							TCMkTypeExpression mk = (TCMkTypeExpression)node;
							return env.findType(mk.typename, cdef.name.getName());
						}
						
						Log.error("TCNode located, but unable to find definition %s", position);
						return null;
					}
				}
			}
		}

		Log.error("Unable to locate symbol %s", position);
		return null;
	}
}
