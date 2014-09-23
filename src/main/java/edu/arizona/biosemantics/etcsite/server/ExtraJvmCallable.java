package edu.arizona.biosemantics.etcsite.server;

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
	private String xmx;
	private String xms;
	
	protected ExtraJvmCallable() {  }

	public ExtraJvmCallable(Class mainClass, String classPath, String xmx, String xms, String[] args) {
		this.mainClass = mainClass;
		this.classPath = classPath;
		this.args = args;
		this.xmx = xmx;
		this.xms = xms;
	}
	
	public void setXmx(String xmx) {
		this.xmx = xmx;
	}
	
	public void setXms(String xms) {
		this.xms = xms;
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
		String command = "java -cp " + classPath + " " + mainClass.getName();
		for(String arg : args)
			command += " " + arg;
		if(xmx != null)
			command += " -Xmx " + xmx;
		if(xms != null)
			command += " -Xms " + xms;
		System.out.println("Run in an extra JVM: " + command);
		
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