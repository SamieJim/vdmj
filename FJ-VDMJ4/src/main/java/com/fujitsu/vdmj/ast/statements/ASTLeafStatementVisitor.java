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

package com.fujitsu.vdmj.ast.statements;

import java.util.Collection;

import com.fujitsu.vdmj.ast.definitions.ASTDefinition;
import com.fujitsu.vdmj.ast.definitions.ASTLeafDefinitionVisitor;
import com.fujitsu.vdmj.ast.expressions.ASTExpression;
import com.fujitsu.vdmj.ast.expressions.ASTLeafExpressionVisitor;
import com.fujitsu.vdmj.ast.patterns.ASTBind;
import com.fujitsu.vdmj.ast.patterns.ASTMultipleBind;
import com.fujitsu.vdmj.ast.patterns.ASTMultipleSeqBind;
import com.fujitsu.vdmj.ast.patterns.ASTMultipleSetBind;
import com.fujitsu.vdmj.ast.patterns.ASTSeqBind;
import com.fujitsu.vdmj.ast.patterns.ASTSetBind;

/**
 * This ASTStatement visitor visits all of the leaves of an statement tree and calls
 * the basic processing methods for the simple statements.
 */
abstract public class ASTLeafStatementVisitor<E, C extends Collection<E>, S> extends ASTStatementVisitor<C, S>
{
	@Override
	public C caseAlwaysStatement(ASTAlwaysStatement node, S arg)
	{
		C all = newCollection();
		all.addAll(node.always.apply(this, arg));
		all.addAll(node.body.apply(this, arg));
		return all;
	}

 	@Override
	public C caseAssignmentStatement(ASTAssignmentStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		return (expVisitor != null ? node.exp.apply(expVisitor, arg) : newCollection());
	}

	@Override
	public C caseAtomicStatement(ASTAtomicStatement node, S arg)
	{
		C all = newCollection();
		
		for (ASTAssignmentStatement assignment: node.assignments)
		{
			all.addAll(assignment.apply(this, arg));
		}
		
		return all;
	}

 	@Override
	public C caseBlockStatement(ASTBlockStatement node, S arg)
	{
		ASTLeafDefinitionVisitor<E, C, S> defVisitor = getDefinitionVisitor();
		C all = newCollection();
		
		if (defVisitor != null)
		{
			for (ASTDefinition def: node.assignmentDefs)
			{
				all.addAll(def.apply(defVisitor, arg));
			}
		}
		
		for (ASTStatement statement: node.statements)
		{
			all.addAll(statement.apply(this, arg));
		}
		
		return all;
	}

	@Override
	public C caseCallObjectStatement(ASTCallObjectStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = newCollection();
		
		if (expVisitor != null)
		{
			for (ASTExpression a: node.args)
			{
				all.addAll(a.apply(expVisitor, arg));
			}
		}
		
		return all;
	}

 	@Override
	public C caseCallStatement(ASTCallStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = newCollection();
		
		if (expVisitor != null)
		{
			for (ASTExpression a: node.args)
			{
				all.addAll(a.apply(expVisitor, arg));
			}
		}
		
		return all;
	}

 	@Override
	public C caseCasesStatement(ASTCasesStatement node, S arg)
	{
		C all = newCollection();
		
		for (ASTCaseStmtAlternative alternative: node.cases)
		{
			all.addAll(alternative.statement.apply(this, arg));
		}
		
		return all;
	}

 	@Override
	public C caseCyclesStatement(ASTCyclesStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = newCollection();
		
		if (expVisitor != null)
		{
			all.addAll(node.cycles.apply(expVisitor, arg));
		}
		
		all.addAll(node.statement.apply(this, arg));
		return all;
	}

 	@Override
	public C caseDurationStatement(ASTDurationStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = newCollection();
		
		if (expVisitor != null)
		{
			all.addAll(node.duration.apply(expVisitor, arg));
		}
		
		all.addAll(node.statement.apply(this, arg));
		return all;
	}

 	@Override
	public C caseElseIfStatement(ASTElseIfStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = newCollection();
		
		if (expVisitor != null)
		{
			all.addAll(node.elseIfExp.apply(expVisitor, arg));
		}
		
		all.addAll(node.thenStmt.apply(this, arg));
		return all;
	}

 	@Override
	public C caseErrorStatement(ASTErrorStatement node, S arg)
	{
		return newCollection();
	}

 	@Override
	public C caseExitStatement(ASTExitStatement node, S arg)
	{
 		if (node.expression != null)
 		{
			ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
			return (expVisitor != null ? node.expression.apply(expVisitor, arg) : newCollection());
 		}
 		else
 		{
 			return newCollection();
 		}
	}

 	@Override
	public C caseForAllStatement(ASTForAllStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = newCollection();
		
		if (expVisitor != null)
		{
			all.addAll(node.set.apply(expVisitor, arg));
		}
		
		all.addAll(node.statement.apply(this, arg));
		return all;
	}

 	@Override
	public C caseForIndexStatement(ASTForIndexStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = newCollection();
		
		if (expVisitor != null)
		{
			all.addAll(node.from.apply(expVisitor, arg));
			all.addAll(node.to.apply(expVisitor, arg));
			
			if (node.by != null)
			{
				all.addAll(node.by.apply(expVisitor, arg));
			}
		}
		
		all.addAll(node.statement.apply(this, arg));
		return all;
	}

 	@Override
	public C caseForPatternBindStatement(ASTForPatternBindStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = caseBind(node.patternBind.bind, arg);
		
		if (expVisitor != null)
		{
			all.addAll(node.exp.apply(expVisitor, arg));
		}
		
		all.addAll(node.statement.apply(this, arg));
		return all;
	}

 	@Override
	public C caseIfStatement(ASTIfStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = newCollection();
		
		if (expVisitor != null)
		{
			all.addAll(node.ifExp.apply(expVisitor, arg));
		}
		
		all.addAll(node.thenStmt.apply(this, arg));
		
		if (node.elseList != null)
		{
			for (ASTElseIfStatement elseStmt: node.elseList)
			{
				all.addAll(elseStmt.apply(this, arg));
			}
		}
		
		if (node.elseStmt != null)
		{
			all.addAll(node.elseStmt.apply(this, arg));
		}
		
		return all;
	}

 	@Override
	public C caseLetBeStStatement(ASTLetBeStStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = caseMultipleBind(node.bind, arg);
		
		if (expVisitor != null && node.suchThat != null)
		{
			all.addAll(node.suchThat.apply(expVisitor, arg));
		}
		
		all.addAll(node.statement.apply(this, arg));
		return all;
	}

 	@Override
	public C caseLetDefStatement(ASTLetDefStatement node, S arg)
	{
		ASTLeafDefinitionVisitor<E, C, S> defVisitor = getDefinitionVisitor();
		C all = newCollection();
		
		if (defVisitor != null)
		{
			for (ASTDefinition def: node.localDefs)
			{
				all.addAll(def.apply(defVisitor, arg));
			}
		}
		
		all.addAll(node.statement.apply(this, arg));
		return all;
	}

 	@Override
	public C casePeriodicStatement(ASTPeriodicStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = newCollection();
		
		if (expVisitor != null)
		{
			for (ASTExpression a: node.args)
			{
				all.addAll(a.apply(expVisitor, arg));
			}
		}
		
		return all;
	}

 	@Override
	public C caseReturnStatement(ASTReturnStatement node, S arg)
	{
 		if (node.expression != null)
 		{
			ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
			return (expVisitor != null ? node.expression.apply(expVisitor, arg) : newCollection());
 		}
 		else
 		{
 			return newCollection();
 		}
	}

 	@Override
	public C caseSimpleBlockStatement(ASTSimpleBlockStatement node, S arg)
	{
		C all = newCollection();
		
		for (ASTStatement assignment: node.statements)
		{
			all.addAll(assignment.apply(this, arg));
		}
		
		return all;
	}

 	@Override
	public C caseSkipStatement(ASTSkipStatement node, S arg)
	{
		return newCollection();
	}

 	@Override
	public C caseSpecificationStatement(ASTSpecificationStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = newCollection();
		
		if (expVisitor != null)
		{
			if (node.precondition != null)
			{
				all.addAll(node.precondition.apply(expVisitor, arg));
			}
			
			if (node.postcondition != null)
			{
				all.addAll(node.postcondition.apply(expVisitor, arg));
			}
		}
		
		return all;
	}

 	@Override
	public C caseSporadicStatement(ASTSporadicStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = newCollection();
		
		if (expVisitor != null)
		{
			for (ASTExpression a: node.args)
			{
				all.addAll(a.apply(expVisitor, arg));
			}
		}
		
		return all;
	}

 	@Override
	public C caseStartStatement(ASTStartStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		return (expVisitor != null ? node.objects.apply(expVisitor, arg) : newCollection());
	}

 	@Override
	public C caseStopStatement(ASTStopStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		return (expVisitor != null ? node.objects.apply(expVisitor, arg) : newCollection());
	}

 	@Override
	public C caseSubclassResponsibilityStatement(ASTSubclassResponsibilityStatement node, S arg)
	{
		return newCollection();
	}

 	@Override
	public C caseTixeStatement(ASTTixeStatement node, S arg)
	{
		C all = newCollection();
		
		for (ASTTixeStmtAlternative alternative: node.traps)
		{
			all.addAll(alternative.statement.apply(this, arg));
		}
		
		all.addAll(node.body.apply(this, arg));
		return all;
	}

 	@Override
	public C caseTrapStatement(ASTTrapStatement node, S arg)
	{
		C all = caseBind(node.patternBind.bind, arg);
		all.addAll(node.with.apply(this, arg));
		all.addAll(node.body.apply(this, arg));
		return all;
	}

 	@Override
	public C caseWhileStatement(ASTWhileStatement node, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = newCollection();
		
		if (expVisitor != null)
		{
			all.addAll(node.exp.apply(expVisitor, arg));
		}
		
		all.addAll(node.statement.apply(this, arg));
		return all;
	}

	private C caseBind(ASTBind bind, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = newCollection();
		
		if (expVisitor != null)
		{
			if (bind instanceof ASTSetBind)
			{
				ASTSetBind sbind = (ASTSetBind)bind;
				all.addAll(sbind.set.apply(expVisitor, arg));
			}
			else if (bind instanceof ASTSeqBind)
			{
				ASTSeqBind sbind = (ASTSeqBind)bind;
				all.addAll(sbind.sequence.apply(expVisitor, arg));
			}
		}
		
		return all;
	}

 	private C caseMultipleBind(ASTMultipleBind bind, S arg)
	{
		ASTLeafExpressionVisitor<E, C, S> expVisitor = getExpressionVisitor();
		C all = newCollection();
		
		if (expVisitor != null)
		{
			if (bind instanceof ASTMultipleSetBind)
			{
				ASTMultipleSetBind sbind = (ASTMultipleSetBind)bind;
				all.addAll(sbind.set.apply(expVisitor, arg));
			}
			else if (bind instanceof ASTMultipleSeqBind)
			{
				ASTMultipleSeqBind sbind = (ASTMultipleSeqBind)bind;
				all.addAll(sbind.sequence.apply(expVisitor, arg));
			}
		}
		
		return all;
	}
	
	abstract protected C newCollection();

 	abstract protected ASTLeafExpressionVisitor<E, C, S> getExpressionVisitor();

 	abstract protected ASTLeafDefinitionVisitor<E, C, S> getDefinitionVisitor();
}
