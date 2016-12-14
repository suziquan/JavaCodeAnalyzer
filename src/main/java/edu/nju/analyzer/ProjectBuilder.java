package edu.nju.analyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于创建Project
 * @author SuZiquan
 *
 */
public class ProjectBuilder {

	/**
	 * 源代码目录路径
	 */
	private List<String> sourcePaths = new ArrayList<>();
	
	/**
	 * 源代码编码，与sourcePaths一一对应
	 */
	private List<String> encodings = new ArrayList<>();
	
	/**
	 * 项目所依赖的类库的路径
	 */
	private List<String> classPaths = new ArrayList<>();
		
	private boolean resolveBinding = Project.DEFAULT_RESOLVE_BINDING;
	private boolean bindingRecovery = Project.DEFAULT_BINDING_RECOVERY;
	private boolean includeVMBootClassPath = Project.DEFAULT_INCLUDE_VM_BOOT_CLASSPATH;
	
	/**
	 * 添加源代码目录
	 * @param sourcePath
	 * @return
	 */
	public ProjectBuilder addSourcePath(String sourcePath){
		return this.addSourcePath(sourcePath, Project.DEFAUTLT_ENCODING);
	}
	
	/**
	 * 添加源代码目录，并指定文件编码
	 * @param sourcePath
	 * @param encoding
	 * @return
	 */
	public ProjectBuilder addSourcePath(String sourcePath,String encoding){
		sourcePaths.add(sourcePath);
		encodings.add(encoding);
		return this;
	}
	
	/**
	 * 添加依赖
	 * @param classPath
	 * @return
	 */
	public ProjectBuilder addClassPath(String classPath){
		classPaths.add(classPath);
		return this;
	}
	
	/**
	 * 关闭JDT的ResolveBinding的选项，默认开启。
	 * @return
	 */
	public ProjectBuilder disableResolveBinding(){
		resolveBinding = false;
		return this;
	}
	
	/**
	 * 关闭JDT的BindingRecovery的选项，默认开启。
	 * @return
	 */
	public ProjectBuilder disableBindingRecovery(){
		bindingRecovery = false;
		return this;
	}
	
	/**
	 * 关闭JDT的includeVmBootClassPath选项，默认开启。
	 * @return
	 */
	public ProjectBuilder disableIncludeVMBootClassPath(){
		includeVMBootClassPath = false;
		return this;
	}
	
	/**
	 * 创建一个project。
	 * @return
	 */
	public Project build(){
		return new Project(sourcePaths, encodings, classPaths, resolveBinding, bindingRecovery, includeVMBootClassPath);
	}
}
