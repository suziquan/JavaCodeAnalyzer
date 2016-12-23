package edu.nju.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import edu.nju.util.FileUtil;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class Project {

	static final boolean DEFAULT_RESOLVE_BINDING = true;

	static final boolean DEFAULT_BINDING_RECOVERY = true;

	static final boolean DEFAULT_INCLUDE_VM_BOOT_CLASSPATH = true;

	static final String DEFAUTLT_ENCODING = "UTF-8";

	@Getter
	private List<String> sourcePaths;

	@Getter
	private List<String> encodings;

	@Getter
	private List<String> classPaths;

	@Getter
	private boolean resolveBinding;

	@Getter
	private boolean bindingRecovery;

	@Getter
	private boolean includeVMBootClassPath;

	private Map<String, CompilationUnit> filePath_CompilationUnit_Map = new HashMap<>();

	private Map<String, AbstractTypeDeclaration> qualifiedMame_Type_Map = new HashMap<>();

	private List<String> globalIssues = new ArrayList<>();

	private Map<String, List<Issue>> issuesMap = new HashMap<String, List<Issue>>();

	private ASTParser newASTParser() {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setResolveBindings(resolveBinding);
		parser.setBindingsRecovery(bindingRecovery);

		parser.setEnvironment(classPaths.toArray(new String[0]), sourcePaths.toArray(new String[0]),
				encodings.toArray(new String[0]), includeVMBootClassPath);

		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		Map<String,String> compilerOptions = JavaCore.getOptions();
		
		compilerOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		compilerOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
		compilerOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		
		parser.setCompilerOptions(compilerOptions);
		
		return parser;
	}

	Project(List<String> sourcePaths, List<String> encodings, List<String> classPaths, boolean resolveBinding,
			boolean bindingRecovery, boolean includeVMBootClassPath) {
		this.sourcePaths = sourcePaths;
		this.encodings = encodings;
		this.classPaths = classPaths;
		this.resolveBinding = resolveBinding;
		this.bindingRecovery = bindingRecovery;
		this.includeVMBootClassPath = includeVMBootClassPath;

		for (String sourcePath : sourcePaths) {
			List<File> javaFiles = FileUtil.findJavaFiles(sourcePath);
			for (File javaFile : javaFiles) {

				ASTParser parser = this.newASTParser();

				String filePath = javaFile.getPath();
				int index = sourcePaths.indexOf(sourcePath);
				String encoding = encodings.get(index);
				String source = FileUtil.read(javaFile,encoding);
				parser.setSource(source.toCharArray());
				parser.setUnitName(filePath);

				CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
				filePath_CompilationUnit_Map.put(filePath, compilationUnit);
				compilationUnit.accept(new ASTVisitor() {

					@Override
					public boolean visit(EnumDeclaration node) {
						addAnnotationTypeDeclaration(node);
						return super.visit(node);
					}

					@Override
					public boolean visit(AnnotationTypeDeclaration node) {
						addAnnotationTypeDeclaration(node);
						return super.visit(node);
					}
					
					

					@Override
					public boolean visit(TypeDeclaration node) {
						addAnnotationTypeDeclaration(node);
						return super.visit(node);
					}

				});
			}
		}

	}

	private void addAnnotationTypeDeclaration(AbstractTypeDeclaration node) {
		ITypeBinding classTypeBinding = node.resolveBinding();
		String qualifiedName = classTypeBinding.getQualifiedName();
		qualifiedMame_Type_Map.put(qualifiedName, node);
	}

	/**
	 * 获得项目中所有枚举类型声明
	 * @return
	 */
	public List<EnumDeclaration> getEnumDeclarations() {
		return qualifiedMame_Type_Map.values().stream().filter(type -> type instanceof EnumDeclaration)
				.map(type -> (EnumDeclaration) type).collect(Collectors.toList());
	}

	/**
	 * 获得项目中所有注解类型声明
	 * @return
	 */
	public List<AnnotationTypeDeclaration> getAnnotationTypeDeclarations() {
		return qualifiedMame_Type_Map.values().stream().filter(type -> type instanceof AnnotationTypeDeclaration)
				.map(type -> (AnnotationTypeDeclaration) type).collect(Collectors.toList());
	}

	/**
	 * 获取项目中所有非接口的类的类型声明
	 * @return
	 */
	public List<TypeDeclaration> getClassDeclarations() {
		return qualifiedMame_Type_Map.values().stream().filter(type -> type instanceof TypeDeclaration)
				.map(type -> (TypeDeclaration) type).filter(type -> !type.isInterface()).collect(Collectors.toList());
	}

	/**
	 * 获取项目中所有接口声明
	 * @return
	 */
	public List<TypeDeclaration> getInterfaceDeclarations() {
		return qualifiedMame_Type_Map.values().stream().filter(type -> type instanceof TypeDeclaration)
				.map(type -> (TypeDeclaration) type).filter(type -> type.isInterface()).collect(Collectors.toList());
	}

	/**
	 * 通过文件名查找编译单元（通常代表一个Java文件）
	 * @param fileName
	 * @return
	 */
	public CompilationUnit findCompilationUnitByFileName(String fileName) {
		Optional<String> path = filePath_CompilationUnit_Map.keySet().stream()
				.filter(filePath -> filePath.endsWith(fileName)).findFirst();
		return path.isPresent() ? filePath_CompilationUnit_Map.get(path.get()) : null;
	}

	/**
	 * 通过限定名查找非接口的类
	 * @param qualifiedName
	 * @return
	 */
	public TypeDeclaration findClassDelarationByQualifiedName(String qualifiedName) {
		AbstractTypeDeclaration abstractTypeDeclaration = qualifiedMame_Type_Map.get(qualifiedName);
		if(abstractTypeDeclaration instanceof TypeDeclaration){
			TypeDeclaration typeDeclaration = (TypeDeclaration)abstractTypeDeclaration;
			if (!typeDeclaration.isInterface()) {
				return typeDeclaration;
			}
		}
		return null;
	}

	/**
	 * 通过限定名查找接口
	 * @param qualifiedName
	 * @return
	 */
	public TypeDeclaration findInterfaceDelarationByQualifiedName(String qualifiedName) {
		AbstractTypeDeclaration abstractTypeDeclaration = qualifiedMame_Type_Map.get(qualifiedName);
		if(abstractTypeDeclaration instanceof TypeDeclaration){
			TypeDeclaration typeDeclaration = (TypeDeclaration)abstractTypeDeclaration;
			if (typeDeclaration.isInterface()) {
				return typeDeclaration;
			}
		}
		return null;
	}

	/**
	 * 通过限定名查找枚举类型
	 * @param qualifiedName
	 * @return
	 */
	public EnumDeclaration findEnumDeclarationByQualifiedName(String qualifiedName) {
		return (EnumDeclaration) qualifiedMame_Type_Map.get(qualifiedName);
	}

	/**
	 * 通过限定名查找注解类型
	 * @param qualifiedName
	 * @return
	 */
	public AnnotationTypeDeclaration findAnnotationTypeDeclarationByQualifiedName(String qualifiedName) {
		return (AnnotationTypeDeclaration) qualifiedMame_Type_Map.get(qualifiedName);
	}

	/**
	 * 获取某个类型中的所有成员变量
	 * @param typeQualifiedName 类型的限定名
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<VariableDeclaration> getFieldsIn(String typeQualifiedName) {
		TypeDeclaration typeDeclaration = (TypeDeclaration) qualifiedMame_Type_Map.get(typeQualifiedName);
		FieldDeclaration[] fieldDeclarations = typeDeclaration.getFields();
		return Arrays.asList(fieldDeclarations).stream()
				.flatMap(fieldDeclaration -> ((List<VariableDeclaration>) fieldDeclaration.fragments()).stream())
				.collect(Collectors.toList());
	}

	/**
	 * 获取某个类型中的所有方法
	 * @param typeQualifiedName 类型的限定名
	 * @return
	 */
	public List<MethodDeclaration> getMethodsIn(String typeQualifiedName) {
		TypeDeclaration typeDeclaration = (TypeDeclaration) qualifiedMame_Type_Map.get(typeQualifiedName);
		return Arrays.asList(typeDeclaration.getMethods());
	}

	/**
	 * 根据变量名在指定类型中查找成员变量
	 * @param typeQualifiedName 类型的限定名
	 * @param variableName 成员变量名
	 * @return
	 */
	public VariableDeclaration findFieldByName(String typeQualifiedName, String variableName) {
		Optional<VariableDeclaration> field = getFieldsIn(typeQualifiedName).stream()
				.filter(variableDEclaration -> variableDEclaration.getName().toString().equals(variableName))
				.findFirst();
		return field.isPresent() ? field.get() : null;
	}

	/**
	 * 根据方法名在指定类型中查找方法
	 * @param typeQualifiedName 类型的限定名
	 * @param methodName 方法名
	 * @return
	 */
	public MethodDeclaration findMethodByName(String typeQualifiedName, String methodName) {
		Optional<MethodDeclaration> method = this.getMethodsIn(typeQualifiedName).stream()
				.filter(methodDeclaration -> methodDeclaration.getName().toString().equals(methodName)).findFirst();
		return method.isPresent() ? method.get() : null;
	}

	/**
	 * 根据方法名在指定类型中查找方法
	 * @param typeQualifiedName 类型的限定名
	 * @param methodName 方法名
	 * @return
	 */
	public List<MethodDeclaration> findMethodsByName(String typeQualifiedName, String methodName) {
		List<MethodDeclaration> methods = this.getMethodsIn(typeQualifiedName).stream()
				.filter(methodDeclaration -> methodDeclaration.getName().toString().equals(methodName)).collect(Collectors.toList());
		return methods;
	}
	
	/**
	 * 获取某个类型的所有构造器
	 * @param typeQualifiedName
	 * @return
	 */
	public List<MethodDeclaration> getConstructorsOf(String typeQualifiedName){
		List<MethodDeclaration> constructors = getMethodsIn(typeQualifiedName).stream().filter(methodDeclaration->methodDeclaration.isConstructor()).collect(Collectors.toList());
		return constructors;
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

	/**
	 * 生成报告
	 * 
	 * @param reportDirPath
	 * @param reportFileName
	 */
	public void generateIssueReport(String reportDirPath, String reportFileName) {

		Configuration config = new Configuration(Configuration.VERSION_2_3_23);

		try {

			InputStream inputStream = Project.class.getClassLoader().getResourceAsStream("template/issue_report.ftl");

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			String content = "";
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				content += line + System.getProperty("line.separator");
			}

			StringTemplateLoader stringLoader = new StringTemplateLoader();
			stringLoader.putTemplate("issue_report.ftl", content);
			config.setTemplateLoader(stringLoader);

			config.setDefaultEncoding("UTF-8");
			config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			config.setLogTemplateExceptions(true);

			if ((!reportFileName.endsWith(".html")) && (!reportFileName.endsWith(".htm"))) {
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
