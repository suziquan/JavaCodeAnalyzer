package edu.nju.analyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.nju.util.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class CodeAnalyzer {

	private ASTParser parser;

	private String sourcePath;

	private String classPath;

	private String encoding = "UTF-8";

	private List<String> globalIssues = new ArrayList<>();

	private Map<String, List<Issue>> issuesMap = new HashMap<String, List<Issue>>();
	
	public CodeAnalyzer() {
		classPath = CodeAnalyzer.class.getClassLoader().getResource("lib/rt.jar").getPath();

		parser = ASTParser.newParser(AST.JLS8);

		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);

		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setCompilerOptions(JavaCore.getOptions());

	}

	public CompilationUnit getCompilationUnit(String relativeFilePath) {

		String absoluteFilePath = FileUtil.jointPath(sourcePath, relativeFilePath);
		String source = FileUtil.read(absoluteFilePath, encoding);
		parser.setSource(source.toCharArray());

		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		return compilationUnit;
	}

	public void setSourcePath(String sourcePath) {
		setSourcePath(sourcePath, encoding);
	}

	public void setSourcePath(String sourcePath, String sourceEncoding) {
		this.sourcePath = sourcePath;
		this.encoding = sourceEncoding;
		parser.setEnvironment(new String[] { classPath }, new String[] { sourcePath }, new String[] { encoding },
				false);
	}

	public void reportGlobalIssue(String message) {
		globalIssues.add(message);
	}

	public void reportIssue(String filePath, CompilationUnit compilationUnit, ASTNode node, String message) {
		List<Issue> list = issuesMap.get(filePath);
		if (list == null) {
			list = new ArrayList<>();
			issuesMap.put(filePath, list);
		}

		node.getStartPosition();
		int lineNumber = compilationUnit.getLineNumber(node.getStartPosition());

		int startPosition = node.getStartPosition();
		int length = node.getLength();

		Issue issue = new Issue(lineNumber, startPosition, length, message);
		list.add(issue);
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public class Issue {
		private int lineNumber;
		private int startPosition;
		private int length;
		private String message;
	}

	public void generateIssueReport(String reportDirPath, String reportFileName) {

		Configuration config = new Configuration(Configuration.VERSION_2_3_23);

		try {
			String templatesPath = CodeAnalyzer.class.getClassLoader().getResource("template").getPath();

			config.setDirectoryForTemplateLoading(new File(templatesPath));
			config.setDefaultEncoding("UTF-8");
			config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			config.setLogTemplateExceptions(true);

			if (!reportFileName.endsWith(".html") || !reportFileName.endsWith(".htm")) {
				reportFileName += ".html";
			}
			
			File reportDir = new File(reportDirPath);
			if (!reportDir.exists()) {
				reportDir.mkdirs();
			}
			
			File file = new File(FileUtil.jointPath(reportDirPath, reportFileName));
			if (file.exists()) {
				file.delete();
			}
			
			file.createNewFile();

			Template template = config.getTemplate("issue_report.ftl");
			Writer out = new FileWriter(file);

			Map<String, Object> data = new HashMap<>();

			data.put("issuesMap", issuesMap);
			data.put("globalIssues", globalIssues);

			template.process(data, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
