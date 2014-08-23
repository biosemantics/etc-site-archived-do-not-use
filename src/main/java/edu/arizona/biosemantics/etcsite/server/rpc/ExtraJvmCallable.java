package edu.arizona.biosemantics.etcsite.server.rpc;

import java.lang.ProcessBuilder.Redirect;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.tools.ant.util.JavaEnvUtils;

public abstract class ExtraJvmCallable<T> implements Callable<T> {
	
	private String classPath;
	private Class mainClass;
	private Process process;
	private String[] args;
	protected Integer exitStatus;
	
	protected ExtraJvmCallable() {  }

	public ExtraJvmCallable(Class mainClass, String classPath, String[] args) {
		this.mainClass = mainClass;
		this.classPath = classPath;
		this.args = args;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public void setMainClass(Class mainClass) {
		this.mainClass = mainClass;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	@Override
	public T call() throws Exception {
		exitStatus = null;
		if(classPath != null && mainClass != null && args != null) {
			String javaExecutable = JavaEnvUtils.getJreExecutable("java");
			List<String> commandParts = new LinkedList<String>();
			commandParts.add(javaExecutable);
			commandParts.add("-cp");
			commandParts.add(classPath);
			commandParts.add(mainClass.getName());		
			for(String arg : args)
				commandParts.add(arg);
				
			ProcessBuilder processBuilder = new ProcessBuilder(commandParts);
			
			//redirect outputs
			processBuilder.redirectError(Redirect.INHERIT);
			processBuilder.redirectOutput(Redirect.INHERIT);
			
			process = processBuilder.start();
			exitStatus = process.waitFor();
			return createReturn();
		} 
		return null;
	}
	
	public abstract T createReturn();
	
	public void destroy() {
		if(process != null)
			process.destroy();
	}

}
