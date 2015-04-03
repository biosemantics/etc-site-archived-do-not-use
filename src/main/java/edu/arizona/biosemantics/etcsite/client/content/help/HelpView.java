package edu.arizona.biosemantics.etcsite.client.content.help;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ObjectElement;
import com.google.gwt.dom.client.ParamElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class HelpView extends Composite implements IHelpView {

	private static HelpViewUiBinder uiBinder = GWT.create(HelpViewUiBinder.class);

	@UiField
	ObjectElement objectElement;
	 
	@UiField
	ParamElement movieParamElement;
	@UiField
	ParamElement qualityParamElement;
	@UiField
	ParamElement bgColorParamElement;
	@UiField
	ParamElement flashVarsParamElement;
	@UiField
	ParamElement allowFullScreenParamElement;
	@UiField
	ParamElement scaleParamElement;
	@UiField
	ParamElement allowScriptAccessParamElement;
	@UiField
	ParamElement baseParamElement;
	
	@UiField
	Button goToTools;
	
	interface HelpViewUiBinder extends UiBinder<Widget, HelpView> {
	}

	private Presenter presenter;

	public HelpView() {
		initWidget(uiBinder.createAndBindUi(this));
		
		initializeVideo();
	}
	
	private void initializeVideo() {         
        objectElement.setId("scPlayer");
        objectElement.setClassName("embeddedObject");
        objectElement.setWidth("800");
        objectElement.setHeight("400");
        objectElement.setData("http://content.screencast.com/users/hong2cui/folders/Jing/media/d3e4d1a7-cee9-4f8e-87b6-2de1bec34ce3/jingswfplayer.swf");
        objectElement.setType("application/x-shockwave-flash");
 		
        movieParamElement.setValue("http://content.screencast.com/users/hong2cui/folders/Jing/media/d3e4d1a7-cee9-4f8e-87b6-2de1bec34ce3/jingswfplayer.swf");
        qualityParamElement.setValue("high");
        bgColorParamElement.setValue("#FFFFFF");
        flashVarsParamElement.setValue("containerwidth=1600&containerheight=799&thumb=http://content.screencast.com/users/hong2cui/folders/Jing/media/d3e4d1a7-cee9-4f8e-87b6-2de1bec34ce3/FirstFrame.jpg&content=http://content.screencast.com/users/hong2cui/folders/Jing/media/d3e4d1a7-cee9-4f8e-87b6-2de1bec34ce3/ETC-silent-demo-TextCapture-MatrixGeneration.swf&blurover=false");
        allowFullScreenParamElement.setValue("true");
        scaleParamElement.setValue("showall");
        allowScriptAccessParamElement.setValue("always");
        baseParamElement.setValue("http://content.screencast.com/users/hong2cui/folders/Jing/media/d3e4d1a7-cee9-4f8e-87b6-2de1bec34ce3/");
    }

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	
	@UiHandler("goToTools")
	void onHomeClick(ClickEvent e) {
		presenter.onHome();
	}
	

}
