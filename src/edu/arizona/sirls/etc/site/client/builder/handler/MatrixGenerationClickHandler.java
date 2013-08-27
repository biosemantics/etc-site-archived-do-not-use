package edu.arizona.sirls.etc.site.client.builder.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.Element;

import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.api.auth.IAuthenticationClientListener;
import edu.arizona.sirls.etc.site.client.api.auth.lib.rpc.AuthenticationClient;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.dialog.LoginDialogBox;
import edu.arizona.sirls.etc.site.client.builder.lib.LoggedInHeaderBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.MainMenuBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.input.InputStepBuilder;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;


public class MatrixGenerationClickHandler implements ClickHandler, IAuthenticationValidatorListener {

	@Override
	public void onClick(ClickEvent event) {
		AuthenticationValidator sessionValidator = AuthenticationValidator.getInstance();
		sessionValidator.addListener(this);
		sessionValidator.validate();
	}

	@Override
	public void notify(AuthenticationResult authenticationResult) {
		AuthenticationValidator sessionValidator = AuthenticationValidator.getInstance();
		sessionValidator.removeListener(this);
		
		Session session = Session.getInstance();
		PageBuilder pageBuilder = session.getPageBuilder();
		pageBuilder.setHeaderBuilder(LoggedInHeaderBuilder.getInstance());
		pageBuilder.setMenuBuilder(MainMenuBuilder.getInstance());
		pageBuilder.setContentBuilder(MatrixGenerationContentBuilder.getInstance(InputStepBuilder.getInstance()));
		if(authenticationResult.getResult()) {
			pageBuilder.build();
		} else {
			LoginDialogBox loginBox = new LoginDialogBox("You have to login to use this functionality", pageBuilder);
			loginBox.center();
		}
	}

}
