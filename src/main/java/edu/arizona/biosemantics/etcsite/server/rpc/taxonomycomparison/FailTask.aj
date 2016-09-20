package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.db.TaskDAO;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.TaxonomyComparisonException;

@Aspect
public class FailTask {
	
	private TaskDAO taskDAO = new TaskDAO();
	
	@Around("execution(public * edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.TaxonomyComparisonService.*(..) "
			+ "throws TaxonomyComparisonException)")
	public Object executeCall(ProceedingJoinPoint proceedingJoinPoint) throws TaxonomyComparisonException {
		try {
			return proceedingJoinPoint.proceed();
		} catch(TaxonomyComparisonException e) {
			Task task = e.getTask();
			if(task == null)
				task = getTask(proceedingJoinPoint);
			//failTask(task);
			throw e;
 		} catch (Throwable t) {
			//failTask(getTask(proceedingJoinPoint));
			log(LogLevel.ERROR, "Unexpected throwable caught in fail task aspect. Check the pointcut/joinpoint interface.", t);
			return null;
		}
	}
	
	private void failTask(Task task) {
		if(task != null) {
			task.setFailed(true);
			task.setFailedTime(new Date());
			taskDAO.updateTask(task);
		}
	}


	private Task getTask(ProceedingJoinPoint proceedingJoinPoint) {
		for(Object arg : proceedingJoinPoint.getArgs()) {
			if(arg instanceof Task) {
				return (Task)arg;
			}
		}
		return null;
	}	
}
