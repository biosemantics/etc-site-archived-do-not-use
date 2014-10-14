package edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.shared.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.MatrixGenerationException;

@Aspect
public class FailTask {

	private DAOManager daoManager = new DAOManager();
	
	@Around("execution(public * edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration.MatrixGenerationService.*(..) "
			+ "throws MatrixGenerationException)")
	public Object executeCall(ProceedingJoinPoint proceedingJoinPoint) throws MatrixGenerationException {
		try {
			return proceedingJoinPoint.proceed();
		} catch(MatrixGenerationException e) {
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
