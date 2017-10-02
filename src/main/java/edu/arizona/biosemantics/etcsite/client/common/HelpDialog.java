package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.Dialog;

import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserService;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserServiceAsync;
import edu.arizona.biosemantics.etcsitehelp.shared.help.Help;

public class HelpDialog extends Dialog {
	
	private static IUserServiceAsync userService = GWT.create(IUserService.class);
	
	private VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
	private CheckBox dontShowCheckBox = new CheckBox();
	private TextButton okButton = this.getButton(PredefinedButton.OK);
	
	private boolean dontShowPopup;
	private Help help;
	private String title;
	private HTML htmlString = new HTML("<HTML><Body>"
			+ "<h4><B> For Instructions Click On</h4><Br> &"
			+ "nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src = 'images/Help.gif' height = '35px' width = '35px' align='middle'/>"
			+ "<Br> &nbsp;<h4><B> at Top Right Corner</Br></Br></BODY></HTML>");

	public HelpDialog(Help help, String title) {
		this.help = help;
		this.title = title;

		layout();
		bindEvents();
	}
	
	private void layout() {
		VerticalLayoutData verticalLayoutData = new VerticalLayoutData();
		verticalLayoutContainer.add(htmlString, verticalLayoutData);
		verticalLayoutContainer.add(dontShowCheckBox, verticalLayoutData);
		dontShowCheckBox.setBoxLabel("Don't show again");
		
		setHeading(title);
		setModal(true);
		setBodyStyle("fontWeight:bold;padding:13px;");
		add(verticalLayoutContainer);

		setHideOnButtonClick(true);
		setMinHeight(125);
		setMinWidth(125);
	}
	
	private void bindEvents() {
		okButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if (dontShowCheckBox.getValue()) {
					userService.setProfile(Authentication.getInstance()
							.getToken(), help, dontShowPopup,
							new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Alerter.failedToSetProfile(caught);
								}
								@Override
								public void onSuccess(Void result) {
									// Alerter.savedSettingsSuccesfully();
								}
							});
				}
			}
		});
	}


	public void showIfDesired() { 
		userService.isProfile(Authentication.getInstance().getToken(), help,
				new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						if (!result) 
							HelpDialog.super.show();
					}

					@Override
					public void onFailure(Throwable caught) {
						Alerter.showAlert("User profile", "Could not retrieve user profile");
					}
				});
	}

}
