package com.cflint.tools;

import java.util.ArrayList;
import java.util.List;

import cfml.parsing.cfscript.CFAssignmentExpression;
import cfml.parsing.cfscript.CFBinaryExpression;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFFunctionExpression;
import cfml.parsing.cfscript.CFIdentifier;
import cfml.parsing.cfscript.CFUnaryExpression;
import cfml.parsing.cfscript.CFVarDeclExpression;
import cfml.parsing.cfscript.script.CFCompDeclStatement;
import cfml.parsing.cfscript.script.CFCompoundStatement;
import cfml.parsing.cfscript.script.CFExpressionStatement;
import cfml.parsing.cfscript.script.CFForInStatement;
import cfml.parsing.cfscript.script.CFForStatement;
import cfml.parsing.cfscript.script.CFFuncDeclStatement;
import cfml.parsing.cfscript.script.CFIfStatement;
import cfml.parsing.cfscript.script.CFScriptStatement;

public class CFScriptTool {
	private static List<CFScriptStatement> statements = new ArrayList<CFScriptStatement>();
	private static List<CFExpression> expressions = new ArrayList<CFExpression>();

	public static List<CFScriptStatement>getAllCFStatements(CFScriptStatement expression) {

		if (expression instanceof CFCompoundStatement) {
			for (final CFScriptStatement statement : ((CFCompoundStatement) expression).getStatements()) {
				statements.add((CFCompoundStatement)expression);
				getAllCFStatements(statement);
			}
		} else if (expression instanceof CFExpressionStatement) {
			statements.add((CFExpressionStatement)expression);
			getAllCFStatements(((CFExpressionStatement)expression).getExpression());
		} else if (expression instanceof CFCompDeclStatement) {
			statements.add((CFCompDeclStatement)expression);
			getAllCFStatements(((CFCompDeclStatement)expression).getBody());
		} else if (expression instanceof CFForStatement) {
			statements.add((CFForStatement)expression);
			getAllCFStatements(((CFForStatement)expression).getInit());
			getAllCFStatements(((CFForStatement)expression).getCond());
			getAllCFStatements(((CFForStatement)expression).getNext());
			getAllCFStatements(((CFForStatement)expression).getBody());
		} else if (expression instanceof CFForInStatement) {
			statements.add((CFForInStatement)expression);
			getAllCFStatements(((CFForInStatement)expression).getVariable());
			getAllCFStatements(((CFForInStatement)expression).getStructure());
			getAllCFStatements(((CFForInStatement)expression).getBody());
		} else if (expression instanceof CFIfStatement) {
			statements.add((CFIfStatement)expression);
			final CFIfStatement cfif = (CFIfStatement) expression;
			getAllCFStatements(cfif.getCond());
			getAllCFStatements(cfif.getThenStatement());
			if (cfif.getElseStatement() != null) {
				getAllCFStatements(((CFIfStatement)expression).getElseStatement());
			}
		} else if (expression instanceof CFFuncDeclStatement) {
			statements.add((CFFuncDeclStatement)expression);
			getAllCFStatements(((CFFuncDeclStatement)expression).getBody());
		} else {
			statements.add(expression);
		}
		return statements;
	}
	
	public static List<CFExpression>getAllCFStatements(CFExpression expression) {
		if (expression instanceof CFUnaryExpression) {
			getAllCFStatements(((CFUnaryExpression) expression).getSub());
		} else if (expression instanceof CFAssignmentExpression) {
			getAllCFStatements(((CFAssignmentExpression) expression).getLeft());
			getAllCFStatements(((CFAssignmentExpression) expression).getRight());
		} else if (expression instanceof CFBinaryExpression) {
			getAllCFStatements(((CFBinaryExpression) expression).getLeft());
			getAllCFStatements(((CFBinaryExpression) expression).getRight());
		} else if (expression instanceof CFFunctionExpression) {
			final CFFunctionExpression cfFunctionExpr = (CFFunctionExpression) expression;
			for (final CFExpression expr : cfFunctionExpr.getArgs()) {
				getAllCFStatements(expr);
			}
		} else if (expression instanceof CFIdentifier) {
			final String name = ((CFIdentifier) expression).getName();
		} else if (expression instanceof CFVarDeclExpression) {
			getAllCFStatements(((CFVarDeclExpression) expression).getInit());
		} else {
			expressions.add(expression);
		}
		return expressions;
	}
	
	
}
