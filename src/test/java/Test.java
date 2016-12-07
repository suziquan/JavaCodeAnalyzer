import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import edu.nju.analyzer.CodeAnalyzer;

public class Test {

	@org.junit.Test
	public void test() {
		CodeAnalyzer analyzer = new CodeAnalyzer();
		analyzer.setSourcePath("d:/demo/src", "GBK");
		
		CompilationUnit compilationUnit = analyzer.getCompilationUnit("com/test/Test.java");
		
		compilationUnit.accept(new ASTVisitor() {

			@Override
			public boolean visit(MethodInvocation node) {
				
				String methodName = node.getName().toString();
				
				System.out.println(methodName);
				IMethodBinding methodBinding = node.resolveMethodBinding();
				
				if (methodBinding != null) {
					ITypeBinding declaringClass = methodBinding.getDeclaringClass();
					String declaringClassQualifiedName = declaringClass.getQualifiedName();
					System.out.println(declaringClassQualifiedName);
				}
				
				return super.visit(node);
			}
			
		});
		
		analyzer.reportGlobalIssue("This is a global issue.");
		analyzer.reportGlobalIssue("Global issue means that it is global and it is not bound to a AST node.");
		
		analyzer.generateIssueReport("d://demo/report", "report.html");
	}

}
