package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.shared.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.SemanticMarkupException;

public aspect FailTask {

	private DAOManager daoManager = new DAOManager();
	
	@Around("execution(public * edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup.SemanticMarkupService.*(..) "
			+ "throws SemanticMarkupException)")
	public Object executeCall(ProceedingJoinPoint proceedingJoinPoint) throws SemanticMarkupException {
		try {
			return proceedingJoinPoint.proceed();
		} catch(SemanticMarkupException e) {
			Task task = e.getTask();
			if(task != null) {
				task.setFailed(true);
				task.setFailedTime(new Date());
				daoManager.getTaskDAO().updateTask(task);
			}
			throw e;
		} catch (Throwable e) {
			log(LogLevel.ERROR, "Unexpected throwable caught in fail task aspect. Check the pointcut/joinpoint interface.", e);
			return null;
		}
	}

}
