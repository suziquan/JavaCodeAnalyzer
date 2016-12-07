# JavaCodeAnalyzer
##简介##
一个使用JDT（Eclipse Java Development Tools）分析Java代码的工具。
##使用JavaCodeAnalyzer##
###1 在Maven项目中使用JavaCodeAnalyzer###
####1.1 在pom.xml中添加仓库####

		<repositories>
			......
			<repository>
				<id>suziquan-maven-repo</id>
				<url>https://raw.githubusercontent.com/suziquan/maven-repo/</url>
			</repository>
		</repositories>


####1.2 在pom.xml中添加依赖####

		<dependencies>
			......
			<dependency>
				<groupId>edu.nju</groupId>
				<artifactId>JavaCodeAnalyzer</artifactId>
				<version>1.2.0</version>
			</dependency>
		</dependencies>
####1.3 下载JavaDoc和源代码####
这一步不是必须的，但可以在你编写代码时提供帮助。如果你使用的是Eclipse或IntelliJ IDEA，可以通过项目右键菜单"Maven"->"Download JavaDoc"和"Download Sources"下载JavaCodeAnalyzer及其依赖的类库的JavaDoc和源代码，这样在编写代码时可以通过IDE查看文档及实现。

###2 使用示例###
建议先看后面的"<strong>关于JDT</strong>"一节，对JDT先有个大致的了解。
####2.0 被分析代码####
源码目录为d:/demo/src，java文件的编码为"GBK"。Test.java在d:/demo/src/com/test目录下，其内容如下：
<pre><code>
package com.test;

import java.util.*;

public class Test{
	
	public void method1(){
		List list = new ArrayList();
		list.forEach((e)->System.out.println(e));
	}
	
	public String method2(){
		return null;
	}
	
}</code></pre>
####2.1 不使用Visitor访问抽象语法树结点####
例：打印所有方法名称。
<pre><code>			
		CodeAnalyzer analyzer = new CodeAnalyzer();
		analyzer.setSourcePath("d:/demo/src", "GBK");
		
		CompilationUnit compilationUnit = analyzer.getCompilationUnit("com/test/Test.java");
		
		List<?> types = compilationUnit.types();
		
		if (types.get(0) instanceof TypeDeclaration) {
			TypeDeclaration typeDeclaration = (TypeDeclaration) types.get(0);
			MethodDeclaration[] methods = typeDeclaration.getMethods();
			for(MethodDeclaration method:methods){
				System.out.println(method.getName());
			}
		};
</code></pre>
####2.2 使用Visitor访问抽象语法树结点####
例：打印所有方法名称。
<pre><code>			
		CodeAnalyzer analyzer = new CodeAnalyzer();
		analyzer.setSourcePath("d:/demo/src", "GBK");
		
		CompilationUnit compilationUnit = analyzer.getCompilationUnit("com/test/Test.java");
		
		compilationUnit.accept(new ASTVisitor() {

			@Override
			public boolean visit(MethodDeclaration node) {
				System.out.println(node.getName());
				return super.visit(node);
			}
			
		});
</code></pre>
####2.3 通过BindingResolve获得一个变量或方法所绑定的更多信息####
例：打印出所有的方法调用，以及该方法在哪个类型中声明。
<pre><code>			
		CodeAnalyzer analyzer = new CodeAnalyzer();
		analyzer.setSourcePath("d:/demo/src", "GBK");
		
		CompilationUnit compilationUnit = analyzer.getCompilationUnit("com/test/Test.java");
		
		compilationUnit.accept(new ASTVisitor() {

			@Override
			public boolean visit(MethodInvocation node) {
				
				String methodName = node.getName().toString();
				System.out.print("方法"+methodName+"被调用。");

				IMethodBinding methodBinding = node.resolveMethodBinding();
				if (methodBinding != null) {
					ITypeBinding declaringClass = methodBinding.getDeclaringClass();
					String declaringClassQualifiedName = declaringClass.getQualifiedName();
					System.out.print("这个方法在"+declaringClassQualifiedName+"中声明。");
				}

				System.out.println();				
				return super.visit(node);
			}
			
		});
</code></pre>
####2.4 生成报告####
<pre><code>			
		final CodeAnalyzer analyzer = new CodeAnalyzer();
		analyzer.setSourcePath("d:/demo/src", "GBK");
		
		final CompilationUnit compilationUnit = analyzer.getCompilationUnit("com/test/Test.java");
		
		compilationUnit.accept(new ASTVisitor() {

			@Override
			public boolean visit(MethodDeclaration node) {
				analyzer.reportIssue("com/test/Test.java", compilationUnit, node,
						"This issue is attached to MethodDeclaration.");
				return super.visit(node);
			}
			
		});
		
		analyzer.reportGlobalIssue("This is a global issue.This means it is not bound to a AST node.");
		analyzer.reportGlobalIssue("This is a global issue, too.");
		
		analyzer.generateIssueReport("d://demo/report", "report.html");
</code></pre>
生成的报告如下：
<br/>
 <img src="/md-res/report.png"  style="border:1px solid #000"/>
##关于JDT##
###1 JDT的使用###

在<a href="http://help.eclipse.org/neon/index.jsp">http://help.eclipse.org/neon/index.jsp</a>下的 "JDT Plug-in Developer Guide"。

###2 JDT中的BindingResolve###

单从一个 Java 文件中我们很难得到其中出现的一些元素（类、方法等）更详细的信息，没有 Binding Resolve，我们获得的信息只是一个类名或者方法名字符串。通过 Binding Resolve，我们可以从项目中的其它java代码及使用的类库中获得这个类或方法更多的信息。但是BindingResolve会对性能有一定的影响。JavaCodeAnalyzer默认开启了BindingResolve。

###3 一个很有帮助的 Eclipse 插件： AST View ###
<strong>安装AST View：</strong>
 "Help" > "Eclipse Marketplace" 中搜索 "AST View" 或者在 "Help" > "Install New Software" 中使用网址 <a>http://www.eclipse.org/jdt/ui/update-site</a>。

我们可以通过 AST View 插件查看一个 Java 文件的抽象语法树（AST），如下图所示。通过这种方法我们可以更直观地了解 AST 的结构。 AST View 已经使用了
Binding Resolve，所以我们可以看到代码中出现的类、方法等元素更详细的信息。

![](/md-res/astview.png) 

###4 更多资料 ###
<a href="http://www.eclipse.org/articles/article.php?file=Article-JavaCodeManipulation_AST/index.html ">Abstract Syntax Tree</a>

<a href="http://www.ibm.com/developerworks/cn/opensource/os-ast/index.html">探索Eclipse的ASTParser</a>