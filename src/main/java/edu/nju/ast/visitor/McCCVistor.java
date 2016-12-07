package edu.nju.ast.visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.WhileStatement;

import lombok.Getter;

/**
 * 用于计算方法的圈复杂度，计算方法参考：
 * http://stackoverflow.com/questions/29039524/implementing-a-metric-suite-using
 * -astparser-in-java
 * 
 * @author SuZiquan
 *
 */
public class McCCVistor extends ASTVisitor {
	
	@Getter
	private int mcCC = 0;

	@Override
	public boolean visit(IfStatement node) {
		mcCC++;
		return super.visit(node);
	}

	@Override
	public boolean visit(ForStatement node) {
		mcCC++;
		return super.visit(node);
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		mcCC++;
		return super.visit(node);
	}

	@Override
	public boolean visit(WhileStatement node) {
		mcCC++;
		return super.visit(node);
	}

	@Override
	public boolean visit(DoStatement node) {
		mcCC++;
		return super.visit(node);
	}

	@Override
	public boolean visit(SwitchCase node) {
		mcCC++;
		return super.visit(node);
	}

	@Override
	public boolean visit(CatchClause node) {
		mcCC++;
		return super.visit(node);
	}

	@Override
	public boolean visit(ConditionalExpression node) {
		mcCC++;
		return super.visit(node);
	}

	@Override
	public boolean visit(InfixExpression node) {
		if (node.getOperator() == InfixExpression.Operator.CONDITIONAL_AND
				|| node.getOperator() == InfixExpression.Operator.CONDITIONAL_OR)
			mcCC++;
		return super.visit(node);
	}

}