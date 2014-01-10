package edu.arizona.biosemantics.etcsite.server.rpc;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

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

	private IAuthenticationService authenticationService = new AuthenticationService();
	
	//@Around("execution(RPCResult edu.arizona.biosemantics.etcsite.server.rpc..*(..)) && args(authenticationToken, ..) && " +
	//		"!within(edu.arizona.biosemantics.etcsite.server.rpc.AuthenticationService) && !within(edu.arizona.biosemantics.etcsite.server.rpc.Authentication)")
	@Around("execution(RPCResult edu.arizona.biosemantics.etcsite.server.rpc..*(..)) && " +
			"!within(edu.arizona.biosemantics.etcsite.server.rpc.AuthenticationService) && !within(edu.arizona.biosemantics.etcsite.server.rpc.Authentication)")
	public RPCResult verifyAuthentication(ProceedingJoinPoint proceedingJoinPoint) {
		//matching args(.., authenticationToken, ..) is not possible currently, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=160509
		AuthenticationToken authenticationToken = getAuthenticationToken(proceedingJoinPoint);
		if(authenticationToken == null) 
			return actualResult(proceedingJoinPoint);
		
		System.out.println(proceedingJoinPoint.getSignature().toLongString());
		RPCResult result = new RPCResult(false, "Internal Server Error");
		RPCResult<AuthenticationResult> authenticationResult = authenticationService.isValidSession(authenticationToken);
		if(authenticationResult.isSucceeded()) { 
			if(authenticationResult.getData().getResult()) {
				return actualResult(proceedingJoinPoint);
			} else {
				return new RPCResult(false, "Authentication failed");
			}
		}
		result.setMessage(authenticationResult.getMessage());
		return result;
	}

	private RPCResult actualResult(ProceedingJoinPoint proceedingJoinPoint) {
		RPCResult result = new RPCResult(false, "Internal Server Error");
		try {
			Object actualResult = proceedingJoinPoint.proceed();
			if(actualResult instanceof RPCResult) {
				return (RPCResult)actualResult;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
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
