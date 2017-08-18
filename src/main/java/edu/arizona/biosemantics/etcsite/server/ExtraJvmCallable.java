package edu.arizona.biosemantics.etcsite.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.tools.ant.util.JavaEnvUtils;

import edu.arizona.biosemantics.common.log.LogLevel;

public abstract class ExtraJvmCallable<T> implements Callable<T>, Task {
	
	private String classPath;
	private Class mainClass;
	private Process process;
	private String[] args;
	protected StringBuilder stdOut = new StringBuilder();
	protected StringBuilder err = new StringBuilder();
	protected int exitStatus = -1;
	private String xmx;
	private String xms;
	private String workingDir;
	private String pathEnvironment;
	private String addPathEnvironment;
		
	protected ExtraJvmCallable() {  }

	public ExtraJvmCallable(Class mainClass, String classPath, String xmx, String xms, String workingDir, String[] args) {
		this.mainClass = mainClass;
		this.classPath = classPath;
		this.args = args;
		this.xmx = xmx;
		this.xms = xms;
		this.workingDir = workingDir;
	}
		
	public String getWorkingDir() {
		return workingDir;
	}

	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
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
	
	public void setPathEnvironment(String pathEnvironment) {
		this.pathEnvironment = pathEnvironment;
	}
	
	public void addPathEnvironment(String addPathEnvironment) {
		this.addPathEnvironment = addPathEnvironment;
	}

	public void setMainClass(Class mainClass) {
		this.mainClass = mainClass;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	@Override
	public T call() throws Exception {
		String command = "java";
		if(xmx != null)
			command += " -Xmx" + xmx;
		if(xms != null)
			command += " -Xms" + xms;
		command += " -cp '" + classPath + "'";
		command += " '" + mainClass.getName() + "'";
		for(String arg : args)
			if(arg.startsWith("-"))
				command += " " + arg;
			else
				command += " '" + arg + "'";
		log(LogLevel.INFO, "Run in an extra JVM: " + command);
		
		exitStatus = -1;
		if(classPath != null && mainClass != null && args != null) {
			String javaExecutable = "java";
			try {
				javaExecutable = JavaEnvUtils.getJreExecutable("java");
			} catch(Throwable t) {
				log(LogLevel.ERROR, "Couldn't find JRE Executable", t);
				javaExecutable = "java";
			}
			List<String> commandParts = new LinkedList<String>();
			commandParts.add(javaExecutable);
			if(xmx != null)
				commandParts.add("-Xmx" + xmx);
			if(xms != null)
				commandParts.add("-Xms" + xms);
			commandParts.add("-cp");
			commandParts.add(classPath);
			commandParts.add(mainClass.getName());		
			for(String arg : args)
				commandParts.add(arg);
				
			System.out.println(commandParts.toString());
			ProcessBuilder processBuilder = new ProcessBuilder(commandParts);
			if(pathEnvironment != null)
				processBuilder.environment().put("PATH", pathEnvironment);
			else {
				if(addPathEnvironment != null) {
					String path = addPathEnvironment + ":" + processBuilder.environment().get("PATH");
					processBuilder.environment().put("PATH", path);
				}
			}
			
			//redirect outputs
			//processBuilder.redirectError(Redirect.INHERIT); //only redirects to system.out not to log
			//processBuilder.redirectOutput(Redirect.INHERIT);
			
			try {
				if(workingDir != null)
					processBuilder.directory(new File(workingDir));
				long time = System.currentTimeMillis();
				process = processBuilder.start();
				
				try (BufferedReader stdInput = new BufferedReader(
						new InputStreamReader(process.getInputStream()))) {
					String s = "";
					while ((s = stdInput.readLine()) != null) {
						log(LogLevel.DEBUG,
								s + " at " + (System.currentTimeMillis() - time)
										/ 1000 + " seconds");
						stdOut.append(s + "\n");
					}
				}

				try (BufferedReader errInput = new BufferedReader(
						new InputStreamReader(process.getErrorStream()))) {
					String e = "";
					while ((e = errInput.readLine()) != null) {
						log(LogLevel.DEBUG,
								e + " at " + (System.currentTimeMillis() - time)
										/ 1000 + " seconds");
						err.append(e + "\n");
					}
				}
				
				exitStatus = process.waitFor();
			} catch(IOException | InterruptedException e) {
				log(LogLevel.ERROR, "Process couldn't execute successfully", e);
			}
			
			return createReturn();
		} 

		return null;
	}
	
	public abstract T createReturn() throws Exception;
	
	public String getStdOut() {
		return stdOut.toString();
	}
	
	public String getErr() {
		return err.toString();
	}
	
	public void destroy() {
		log(LogLevel.INFO, "Destory: " + this);
		if(process != null)
			process.destroy();
	}

	@Override
	public boolean isExecutedSuccessfully() {
		return exitStatus == 0;
	}

}
