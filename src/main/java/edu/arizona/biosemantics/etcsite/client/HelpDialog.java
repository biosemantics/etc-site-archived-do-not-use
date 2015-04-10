package edu.arizona.biosemantics.etcsite.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.FramedPanel;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.help.Help;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserService;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserServiceAsync;

public class HelpDialog extends Dialog {
	
	private static IUserServiceAsync userService = GWT.create(IUserService.class);
	public boolean dontShowPopup;
	public String popoupType;
	
	public HelpDialog(String type, String title, String message) {
		
			final FramedPanel panel;
		
			CheckBox chkBox= new CheckBox();
			chkBox.setBoxLabel("Don't show again");
			this.popoupType=type;
			
	
			//HTML hms= new HTML("<img src = '" + "images/ButtonGray.gif" + "' height = '200px' width = '200px' />");
			HTML htmlString= new HTML(message); 
			VerticalLayoutData vLD= new VerticalLayoutData();
			  
			VerticalLayoutContainer vLC= new VerticalLayoutContainer();
		   // c.add(label1, new VerticalLayoutData(1, .5d, new Margins(4)));
		   
			vLC.add(htmlString,vLD);
			vLC.add(chkBox,vLD );
			//vLC.add(chkBox);
		    //c.add(label3, new VerticalLayoutData(1, .5d, new Margins(4)));
		 
		    Widget wg=vLC.getWidget(1); 
		 
			panel = new FramedPanel();
			panel.setHeadingText("Instructions");

			panel.setCollapsible(true);
			panel.setLayoutData(new MarginData(10));
			panel.setWidget(vLC);
		 
			setHeadingText(title);
			//d.add(panel);
			add(vLC);
			setModal(true);
			setBodyStyle("fontWeight:bold;padding:13px;");
			
			setHideOnButtonClick(true);
			setMinHeight(125);
			setMinWidth(125);
			show();
		
  		
		TextButton okButton = this.getButton(PredefinedButton.OK);
		okButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				// TODO Auto-generated method stub
				VerticalLayoutContainer vLC= (VerticalLayoutContainer) getWidget(0);
				CheckBox dontShowCheckBox= new CheckBox();
				dontShowCheckBox=	(CheckBox)vLC.getWidget(1);
				dontShowPopup= dontShowCheckBox.getValue();
				IUserServiceAsync userService = GWT.create(IUserService.class);
				if(dontShowPopup) {
				userService.setPopupPreference(Authentication.getInstance().getToken(), popoupType, dontShowPopup,new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToSaveMatrix(caught);
					}
					@Override
					public void onSuccess(Void result) {
						//Alerter.savedSettingsSuccesfully();
						}
				});
				} 
				
			}
			
			
		});
	}

}
