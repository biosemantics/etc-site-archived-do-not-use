package edu.arizona.biosemantics.etcsite.server.rpc.auth;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Emailer;
import edu.arizona.biosemantics.etcsite.server.db.CaptchaDAO;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.db.PasswordResetRequestDAO;
import edu.arizona.biosemantics.etcsite.server.db.UserDAO;
import edu.arizona.biosemantics.etcsite.server.rpc.task.TaskService;
import edu.arizona.biosemantics.etcsite.server.rpc.user.UserService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationService;

/**
 * Make sure there are no conflicts due to AspectJ being included in charaparser's learn/markup.jars and 
 * a gwt aop library such as gwtent (and its dependencies on possibly aspectj)
 * 
 * aspectj alone without a gwt aop library works if aspects are only used on the server side
 * 
 * @author rodenhausen
 *
 */
@Aspect 
public class Authentication {

	private UserDAO userDAO = new UserDAO();
	//can't inject into aspect per guice constructor injection; hence construct minimal 'custom' authentication service
	private IAuthenticationService authenticationService = new AuthenticationService(null, userDAO, null, null, null);	
	
	//@Around("execution(RPCResult edu.arizona.biosemantics.etcsite.server.rpc..*(..)) && args(authenticationToken, ..) && " +
	//		"!within(edu.arizona.biosemantics.etcsite.server.rpc.AuthenticationService) && !within(edu.arizona.biosemantics.etcsite.server.rpc.Authentication)")
	// Can't be on MainWrapper's static main otherwise ExtraJVM Calls will fail because NoAspectBoundException (!static)
	@Around("execution(public * *(..)) && execution(!static * edu.arizona.biosemantics.etcsite.server.rpc..*(..)) && " +
			"!within(edu.arizona.biosemantics.etcsite.server.rpc.auth..*) && " +
			"!within(edu.arizona.biosemantics.etcsite.server.rpc.setup..*)")
	public Object verifyAuthentication(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		//matching args(.., authenticationToken, ..) is not possible currently, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=160509
		AuthenticationToken authenticationToken = getAuthenticationToken(proceedingJoinPoint);
		if(authenticationToken == null) 
			return actualResult(proceedingJoinPoint);
		
		AuthenticationResult authenticationResult = authenticationService.isValidSession(authenticationToken);
		if(authenticationResult.getResult()) {
			if(!isGetResumableTasksCall(proceedingJoinPoint)) {
				log(LogLevel.INFO, "User " + authenticationResult.getUser().getFullNameEmailAffiliation() + " calls: " + 
						proceedingJoinPoint.getSignature().toShortString() + " with arguments " + getArgsString(proceedingJoinPoint.getArgs()));
			}
			return actualResult(proceedingJoinPoint);
		} else {
			log(LogLevel.ERROR, "Attempt to authenticate with " + authenticationToken.toString() + " failed! "
					+ "Call made to " + proceedingJoinPoint.getSignature() + 
					" with arguments " + getArgsString(proceedingJoinPoint.getArgs()));
			throw new AuthenticationFailedException();
		}
	}
	
	private boolean isGetResumableTasksCall(ProceedingJoinPoint proceedingJoinPoint) {
		boolean result = true;
		result &= proceedingJoinPoint.getSignature().getDeclaringType().equals(TaskService.class);
		result &= proceedingJoinPoint.getSignature().getName().startsWith("getResumableOrFailedTasks");
		return result;
	}

	private String getArgsString(Object[] objects) {
		StringBuilder result = new StringBuilder();
		for(Object object : objects)
			if(!(object instanceof AuthenticationToken))
				try {
					result.append(object.toString() + "\n");
				} catch(Exception e) {
					//no big deal
				}
		String out = result.toString();
		if(out.length() == 0)
			return out;
		return out.substring(0, out.length() - 1);
	}

	private Object actualResult(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		return proceedingJoinPoint.proceed();
	}

	private AuthenticationToken getAuthenticationToken(ProceedingJoinPoint proceedingJoinPoint) {
		for(Object arg : proceedingJoinPoint.getArgs()) {
			if(arg instanceof AuthenticationToken) {
				return (AuthenticationToken)arg;
			}
		}
		return null;
	}	
}
