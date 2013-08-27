package edu.arizona.sirls.etc.site.client;

import com.google.gwt.core.client.EntryPoint;

import edu.arizona.sirls.etc.site.client.builder.IContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.IFooterBuilder;
import edu.arizona.sirls.etc.site.client.builder.IHeaderBuilder;
import edu.arizona.sirls.etc.site.client.builder.IMenuBuilder;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.handler.IAuthenticationValidatorListener;
import edu.arizona.sirls.etc.site.client.builder.handler.AuthenticationValidator;
import edu.arizona.sirls.etc.site.client.builder.lib.FooterBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.LoggedInHeaderBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.LoggedOutHeaderBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.StartContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.StartMenuBuilder;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ETCSite implements EntryPoint, IAuthenticationValidatorListener {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {		
		AuthenticationValidator sessionValidator = AuthenticationValidator.getInstance();
		sessionValidator.validate();
		sessionValidator.addListener(this);
	}

	@Override
	public void notify(AuthenticationResult authenticationResult) {
		AuthenticationValidator sessionValidator = AuthenticationValidator.getInstance();
		sessionValidator.removeListener(this);
		Session session = Session.getInstance();
		
		IHeaderBuilder headerBuilder;
		if(authenticationResult.getResult()) {
			headerBuilder = LoggedInHeaderBuilder.getInstance();
		} else {
			headerBuilder = LoggedOutHeaderBuilder.getInstance();
		}
		PageBuilder pageBuilder = new PageBuilder(
				FooterBuilder.getInstance(), 
				StartContentBuilder.getInstance(), 
				headerBuilder, 
				StartMenuBuilder.getInstance());
		session.setPageBuilder(pageBuilder);
		pageBuilder.build();
		
//		LoggedOutHeaderBuilder loggedOutHeaderBuilder = LoggedOutHeaderBuilder.getInstance();
//		loggedOutHeaderBuilder.build();
	}
	
}
