package com.cflint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.RecognitionException;

import com.cflint.listeners.ProgressMonitorListener;
import com.cflint.listeners.ScanProgressListener;
import com.cflint.plugins.*;
import com.cflint.plugins.core.*;
import com.cflint.tools.CFLintFilter;
import com.cflint.tools.CFSeverity;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import cfml.parsing.CFMLParser;
import cfml.parsing.CFMLSource;
import cfml.parsing.cfscript.CFAssignmentExpression;
import cfml.parsing.cfscript.CFBinaryExpression;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.CFFunctionExpression;
import cfml.parsing.cfscript.CFIdentifier;
import cfml.parsing.cfscript.CFLiteral;
import cfml.parsing.cfscript.CFUnaryExpression;
import cfml.parsing.cfscript.CFVarDeclExpression;
import cfml.parsing.cfscript.IErrorReporter;
import cfml.parsing.cfscript.ParseException;
import cfml.parsing.cfscript.script.CFCompDeclStatement;
import cfml.parsing.cfscript.script.CFCompoundStatement;
import cfml.parsing.cfscript.script.CFExpressionStatement;
import cfml.parsing.cfscript.script.CFForInStatement;
import cfml.parsing.cfscript.script.CFForStatement;
import cfml.parsing.cfscript.script.CFFuncDeclStatement;
import cfml.parsing.cfscript.script.CFFunctionParameter;
import cfml.parsing.cfscript.script.CFIfStatement;
import cfml.parsing.cfscript.script.CFScriptStatement;

public class CFLint implements IErrorReporter {
	
	static final int LINES_THRESHOLD = 600; 
	static final int CHAR_PER_LINE_THRESHOLD = 100; 
	StackHandler handler = new StackHandler();
	boolean inFunction = false;
	boolean inAssignment = false;
	boolean inComponent = false;
	BugList bugs;
	List<CFLintScanner> extensions = new ArrayList<CFLintScanner>();
	List<String> allowedExtensions = new ArrayList<String>();
	boolean verbose = false;
	boolean quiet = false;
	boolean showProgress = false;
	boolean progressUsesThread = true;

	private String currentFile;
	List<ScanProgressListener> scanProgressListeners = new ArrayList<ScanProgressListener>();

	public CFLint() {
		this(	//new NestedCFOutput(), 
				//new TypedQueryNew(), 
				//new VarScoper(), 
				//new ArgVarChecker(), 
				//new ArgDefChecker(),
				//new GlobalVarChecker(), 
				//new QueryParamChecker(), 
				//new CommentChecker(), 
				//new CfqueryChecker(), 
				//new CFScriptTagCountChecker(), 
				//new CFDumpChecker(), 
				//new CFNestedControlStatementChecker(), 
				//new CFEmptyCodeBlockChecker(),
				//new HintChecker(), 
				//new CFSwitchDefaultChecker(), 
				//new FunctionLengthChecker(), 
				//new FunctionOutputChecker(), 
				//new CFKeywordLowercaseChecker(),
				//new CfmcTagChecker(),
				//new TagCaseChecker(),
				//new CFComponentChecker(),
				new ScriptTagChecker(),
				new SelectStarChecker(),
				new CFModuleTagChecker(),
				new CFInsertTagChecker(),
				new CFUpdateTagChecker());
	}

	public CFLint(final CFLintScanner... bugsScanners) {
		super();
		
//		DictionaryPreferences dprefs = new DictionaryPreferences();
//		dprefs.setDictionaryDir("C:\\projects\\cfml.dictionary-master\\dictionary");
//		DictionaryManager.initDictionaries(dprefs);
		
		for (final CFLintScanner scanner : bugsScanners) {
			extensions.add(scanner);
		}
		final CFLintFilter filter = CFLintFilter.createFilter();
		bugs = new BugList(filter);
		try {
			allowedExtensions.addAll(Arrays.asList(ResourceBundle.getBundle("com.cflint.cflint")
					.getString("allowedextensions").split(",")));
		} catch (final Exception e) {
			allowedExtensions.add(".cfc");
			allowedExtensions.add(".cfm");
		}
	}

	public void scan(final String folder) {
		final File f = new File(folder);
		scan(f);
	}

	public void scan(final File folderOrFile) {
		if (folderOrFile.isDirectory()) {
			for (final File file : folderOrFile.listFiles()) {
				scan(file);
			}
		} else if (!folderOrFile.isHidden() && checkExtension(folderOrFile)) {
			//check number of lines in a file
			//rule: number of lines per file should be under linesThreshold
			/*int lines = 0;
			try {
				Scanner scanner = new Scanner(folderOrFile);
				while (scanner.hasNext()) {
					lines++;
					String line = scanner.nextLine();
					//rule: number of character per line should be under CHAR_PER_LINE_THRESHOLD
					if (line.length() > CHAR_PER_LINE_THRESHOLD) {
						bugs.add(new BugInfo.BugInfoBuilder().setMessageCode("EXCESSIVE_LINE_LENGTH")
								.setFilename(folderOrFile.getAbsolutePath())
								.setMessage("Line number " + lines + " is " + line.length() + " characters long. Over the " 
										+ CHAR_PER_LINE_THRESHOLD + " character threshold")
								.setSeverity(CFSeverity.WARNING.getValue()).build());
					}
				}
				scanner.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			
			if (lines > LINES_THRESHOLD) { 
				bugs.add(new BugInfo.BugInfoBuilder().setMessageCode("EXCESSIVE_FILE_LENGTH")
						.setFilename(folderOrFile.getAbsolutePath())
						.setMessage(folderOrFile.getName() + " is " + lines + " lines long. Over the " + LINES_THRESHOLD + " line threshold")
						.setSeverity(CFSeverity.WARNING.getValue()).build());
			}*/
			
			final String src = load(folderOrFile);
			//System.out.println("processing " + file);
			try {
				process(src, folderOrFile.getAbsolutePath());
			} catch (final Exception e) {
				//rj- commenting this out as these errors are antlr errors from cfscript parsing (mainly
				//primaryExpression dealing with semicolons) due to railo/blue dragon differences.
				/*
				e.printStackTrace();
				bugs.add(new BugInfo.BugInfoBuilder().setMessageCode("FILE_ERROR")
						.setFilename(folderOrFile.getAbsolutePath()).setMessage(e.getMessage()).setSeverity("ERROR")
						.build());
				*/
			}
		}
	}

	private boolean checkExtension(final File file) {
		for (final String ext : allowedExtensions) {
			if (file.getName().endsWith(ext)) {
				return true;
			}
		}
		return false;
	}

	static String load(final File file) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			final byte[] b = new byte[fis.available()];
			fis.read(b);
			fis.close();
			return new String(b);
		} catch (final Exception e) {
			return null;
		}
	}

	public void process(final String src, final String filename) throws ParseException, IOException {
		final CFMLSource cfmlSource = new CFMLSource(src);
		final List<Element> elements = cfmlSource.getChildElements();
		if (elements.size() == 0 && src.contains("component")) {
			// Check if pure cfscript
			CFMLParser cfmlParser = new CFMLParser();
			cfmlParser.setErrorReporter(this);
			final CFScriptStatement scriptStatement = cfmlParser.parseScript(src);
			process(scriptStatement, filename, null, null);
		} else {
			processStack(elements, " ", filename, null);
		}
	}

	public void processStack(final List<Element> elements, final String space, final String filename,
			final String functionName) throws ParseException, IOException {
		for (final Element elem : elements) {
			process(elem, space, filename, functionName);
		}
	}

	private void process(final Element elem, final String space, final String filename, final String functionName)
			throws ParseException, IOException {
		final Context context = new Context(filename, elem, functionName, inAssignment, handler);
		context.setInComponent(inComponent);

		for (final CFLintScanner plugin : extensions) {
			plugin.element(elem, context, bugs);
		}
		if (elem.getName().equals("cfif")) {
			//final int elemLine = elem.getSource().getRow(elem.getBegin());
			//final int elemColumn = elem.getSource().getColumn(elem.getBegin());
			final Pattern p = Pattern.compile("<\\w+\\s(.*[^/])/?>");
			final String expr = elem.getFirstStartTag().toString();
			final Matcher m = p.matcher(expr);
			if (m.matches()) {
				try {
					final CFExpression expression = CFExpression.getCFExpressionThrows(m.group(1));
					if (expression == null) {
						throw new NullPointerException("expression is null, parsing error");
					}
					process(expression, filename, elem, functionName);
				} catch (final Exception npe) {
					final int line = elem.getSource().getRow(elem.getBegin());
					//final int column = elem.getSource().getColumn(elem.getBegin());
					if(!quiet){
						System.err.println("Error in: " + shortSource(elem.getSource(),line) + " @ " + line + ":");
						if(verbose){
							npe.printStackTrace(System.err);
						}
					}
					//commented this and dont worry about parse errors -only small blue dragon/railo differences
					/*
					bugs.add(new BugInfo.BugInfoBuilder().setLine(elemLine).setColumn(elemColumn + column)
							.setMessageCode("PARSE_ERROR").setSeverity("ERROR").setExpression(m.group(1))
							.setFilename(filename).setFunction(functionName).setMessage("Unable to parse").build());
					*/
				}
			}
		} else if (elem.getName().equals("cfargument")) {
			final String name = elem.getAttributeValue("name");
			if (name != null) {
				handler.addArgument(name);
			}
		} else if (elem.getName().equals("cfscript")) {
			final String cfscript = elem.getContent().toString();
			CFMLParser cfmlParser = new CFMLParser();
			cfmlParser.setErrorReporter(this);
			final CFScriptStatement scriptStatement = cfmlParser.parseScript(cfscript);
			process(scriptStatement, filename, elem, functionName);
			// } else if (elem.getName().equals("cfoutput")) {
			// final Element parent = CFTool.getNamedParent(elem, "cfoutput");
			// if (parent != null && parent.getAttributeValue("query") != null
			// && parent.getAttributeValue("group") == null) {
			// final into line = elem.getSource().getRow(elem.getBegin());
			// final int column = elem.getSource().getColumn(elem.getBegin());
			// bugs.add(new
			// BugInfo.BugInfoBuilder().setLine(line).setColumn(column).setMessageCode("NESTED_CFOUTPUT")
			// .setSeverity("ERROR").setFilename(filename)
			// .setMessage("Nested CFOutput, outer CFOutput has @query.").build());
			// }
		} else {
		}

		if (elem.getName().equals("cffunction")) {
			inFunction = true;
			handler.push("function");
			processStack(elem.getChildElements(), space + " ", filename, elem.getAttributeValue("name"));
			inFunction = false;
			handler.pop();
		} else if (elem.getName().equals("cfcomponent")) {
			inComponent = true;
			handler.push("component");
			processStack(elem.getChildElements(), space + " ", filename, elem.getAttributeValue("name"));
			inComponent = false;
			handler.pop();
		} else if (elem.getName().equalsIgnoreCase("cfquery")) {
			final List<Element> list = elem.getAllElements();
			processStack(list.subList(1, list.size()), space + " ", filename, functionName);
		} else if (elem.getName().equalsIgnoreCase("cfqueryparam")) {
			if (elem.getAttributeValue("value") != null) {
				CFMLParser cfmlParser = new CFMLParser();
				cfmlParser.setErrorReporter(this);
				final CFScriptStatement scriptStatement = cfmlParser.parseScript(elem.getAttributeValue("value")
						+ ";");
				process(scriptStatement, filename, elem, functionName);
			}
		} else {
			processStack(elem.getChildElements(), space + " ", filename, functionName);
		}
	}

	private String shortSource(Source source, int line) {
		final String retval = source == null?"":source.toString().trim();
		if(retval.length()<300)
			return retval;
		try{
			BufferedReader sr = new BufferedReader(new StringReader(source.toString()));
			for(int i=1; i<line; i++){
				sr.readLine();
			}
			return sr.readLine().replaceAll("\t", " ");
		}catch(Exception e){}
		return retval.substring(0, 300);
	}

	public String stripLineComments(final String cfscript) {
		return cfscript.replaceAll("//[^\r\n]*\r?\n", "\r\n");
	}

	private void process(final CFScriptStatement expression, final String filename, final Element elem,
			final String functionName) {
		final Context context = new Context(filename, elem, functionName, inAssignment, handler);
		context.setInComponent(inComponent);

		for (final CFLintScanner plugin : extensions) {
			plugin.expression(expression, context, bugs);
		}

		if (expression instanceof CFCompoundStatement) {
			for (final CFScriptStatement statement : ((CFCompoundStatement) expression).getStatements()) {
				process(statement, filename, elem, functionName);
			}
		} else if (expression instanceof CFExpressionStatement) {
			process(((CFExpressionStatement) expression).getExpression(), filename, elem, functionName);
		} else if (expression instanceof CFCompDeclStatement) {
			inComponent = true;
			process(((CFCompDeclStatement) expression).getBody(), filename, elem, functionName);
			inComponent = false;
		} else if (expression instanceof CFForStatement) {
			process(((CFForStatement) expression).getInit(), filename, elem, functionName);
			process(((CFForStatement) expression).getCond(), filename, elem, functionName);
			process(((CFForStatement) expression).getNext(), filename, elem, functionName);
			process(((CFForStatement) expression).getBody(), filename, elem, functionName);
		} else if (expression instanceof CFForInStatement) {
			process(((CFForInStatement) expression).getVariable(), filename, elem, functionName);
			process(((CFForInStatement) expression).getStructure(), filename, elem, functionName);
			process(((CFForInStatement) expression).getBody(), filename, elem, functionName);
		} else if (expression instanceof CFIfStatement) {
			final CFIfStatement cfif = (CFIfStatement) expression;
			process(cfif.getCond(), filename, elem, functionName);
			process(cfif.getThenStatement(), filename, elem, functionName);
			if (cfif.getElseStatement() != null) {
				process(cfif.getElseStatement(), filename, elem, functionName);
			}
		} else if (expression instanceof CFFuncDeclStatement) {
			final CFFuncDeclStatement function = (CFFuncDeclStatement) expression;
			inFunction = true;
			handler.push("function");
			if ("init".equals(function.getName())) {
				inFunction = false;
			}

			for (final CFFunctionParameter param : function.getFormals()) {
				handler.addArgument(param.getName());
			}
			process(function.getBody(), filename, elem, function.getName());
			inFunction = false;
			handler.pop();
		} else {
		}
	}

	private void process(final CFExpression expression, final String filename, final Element elem,
			final String functionName) {
		final Context context = new Context(filename, elem, functionName, inAssignment, handler);
		context.setInComponent(inComponent);

		for (final CFLintScanner plugin : extensions) {
			plugin.expression(expression, context, bugs);
		}
		if (expression instanceof CFUnaryExpression) {
			process(((CFUnaryExpression) expression).getSub(), filename, elem, functionName);
		} else if (expression instanceof CFAssignmentExpression) {
			inAssignment = true;
			process(((CFAssignmentExpression) expression).getLeft(), filename, elem, functionName);
			inAssignment = false;
			process(((CFAssignmentExpression) expression).getRight(), filename, elem, functionName);
		} else if (expression instanceof CFBinaryExpression) {
			process(((CFBinaryExpression) expression).getLeft(), filename, elem, functionName);
			process(((CFBinaryExpression) expression).getRight(), filename, elem, functionName);
		} else if (expression instanceof CFFunctionExpression) {
			final CFFunctionExpression cfFunctionExpr = (CFFunctionExpression) expression;
			// if ("QueryNew".equalsIgnoreCase(cfFunctionExpr.getName()) &&
			// cfFunctionExpr.getArgs().size() == 1) {
			// bugs.add(new
			// BugInfo.BugInfoBuilder().setMessageCode("QUERYNEW_DATATYPE")
			// .setMessage("QueryNew statement should specify datatypes.").setVariable("QueryNew")
			// .setFunction(functionName).setSeverity("WARNING").setFilename(filename)
			// .setExpression(expression.Decompile(0)).build(expression, elem));
			// }
			for (final CFExpression expr : cfFunctionExpr.getArgs()) {
				process(expr, filename, elem, functionName);
			}
		} else if (expression instanceof CFIdentifier) {
			final String name = ((CFIdentifier) expression).getName();
			handler.checkVariable(name);
			// if (inFunction && inAssignment && !handler.checkVariable(name)) {
			// bugs.add(new
			// BugInfo.BugInfoBuilder().setMessageCode("MISSING_VAR")
			// .setMessage("Variable " + name +
			// " is not declared with a var statement.").setVariable(name)
			// .setFunction(functionName).setSeverity("ERROR").setFilename(filename)
			// .setExpression(expression.Decompile(0)).build(expression, elem));
			// }
		} else if (expression instanceof CFVarDeclExpression) {
			handler.addVariable(((CFVarDeclExpression) expression).getName());
			process(((CFVarDeclExpression) expression).getInit(), filename, elem, functionName);
		} else if (expression instanceof CFLiteral) {
			// } else if (expression instanceof cfFullVarExpression) {
			// if (((cfFullVarExpression) expression).getExpressions().size() ==
			// 1) {
			// process(((cfFullVarExpression)
			// expression).getExpressions().get(0), filename, elem,
			// functionName);
			// }
		} else {
		}
	}

	public BugList getBugs() {
		return bugs;
	}

	public List<String> getAllowedExtensions() {
		return allowedExtensions;
	}

	public void setAllowedExtensions(final List<String> allowedExtensions) {
		this.allowedExtensions = allowedExtensions;
	}

	public void reportError(final String arg0) {
		// TODO Auto-generated method stub
		final String file = currentFile == null ? "" : currentFile + "\r\n";
		System.out.println(file + "------4-" + arg0);

	}

	public void reportError(final RecognitionException arg0) {
		// TODO Auto-generated method stub
		final String file = currentFile == null ? "" : currentFile + "\r\n";
		System.out.println(file + "-------" + arg0);

	}

	public void reportError(final String[] arg0, final RecognitionException arg1) {
		// TODO Auto-generated method stub
		final String file = currentFile == null ? "" : currentFile + "\r\n";
		System.out.println(file + "-------" + arg0);
	}

	public void reportError(final IntStream arg0, final RecognitionException arg1, final BitSet arg2) {
		// TODO Auto-generated method stub
		final String file = currentFile == null ? "" : currentFile + "\r\n";
		System.out.println(file + "-------" + arg0);

	}

	public void setVerbose(final boolean verbose) {
		this.verbose = verbose;
	}

	public void setQuiet(final boolean quiet) {
		this.quiet = quiet;
	}

	public void addScanProgressListener(final ScanProgressListener scanProgressListener) {
		scanProgressListeners.add(scanProgressListener);
	}

	protected void fireStartedProcessing(final String srcidentifier) {
		for (final ScanProgressListener p : scanProgressListeners) {
			p.startedProcessing(srcidentifier);
		}
	}

	protected void fireFinishedProcessing(final String srcidentifier) {
		for (final ScanProgressListener p : scanProgressListeners) {
			p.finishedProcessing(srcidentifier);
		}
	}
	protected void fireClose() {
		for (final ScanProgressListener p : scanProgressListeners) {
			p.close();
		}
	}

	public void setShowProgress(final boolean showProgress) {
		this.showProgress = showProgress;
	}

	public void setProgressUsesThread(final boolean progressUsesThread) {
		this.progressUsesThread = progressUsesThread;
	}
}
