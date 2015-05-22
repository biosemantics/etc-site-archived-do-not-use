package edu.arizona.biosemantics.etcsite.client.content.gettingstarted;

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

public class GettingStartedView extends Composite implements IGettingStartedView {

	private static GettingStartedViewUiBinder uiBinder = GWT.create(GettingStartedViewUiBinder.class);

	@UiField
	ObjectElement objectElement1;
	@UiField
	ParamElement movieParamElement1;
	@UiField
	ParamElement qualityParamElement1;
	@UiField
	ParamElement bgColorParamElement1;
	@UiField
	ParamElement flashVarsParamElement1;
	@UiField
	ParamElement allowFullScreenParamElement1;
	@UiField
	ParamElement scaleParamElement1;
	@UiField
	ParamElement allowScriptAccessParamElement1;
	@UiField
	ParamElement baseParamElement1;
	
	@UiField
	ObjectElement objectElement2;
	@UiField
	ParamElement movieParamElement2;
	@UiField
	ParamElement qualityParamElement2;
	@UiField
	ParamElement bgColorParamElement2;
	@UiField
	ParamElement flashVarsParamElement2;
	@UiField
	ParamElement allowFullScreenParamElement2;
	@UiField
	ParamElement scaleParamElement2;
	@UiField
	ParamElement allowScriptAccessParamElement2;
	@UiField
	ParamElement baseParamElement2;
	
	@UiField
	ObjectElement objectElement3;
	@UiField
	ParamElement movieParamElement3;
	@UiField
	ParamElement qualityParamElement3;
	@UiField
	ParamElement bgColorParamElement3;
	@UiField
	ParamElement flashVarsParamElement3;
	@UiField
	ParamElement allowFullScreenParamElement3;
	@UiField
	ParamElement scaleParamElement3;
	@UiField
	ParamElement allowScriptAccessParamElement3;
	@UiField
	ParamElement baseParamElement3;
	
	@UiField
	ObjectElement objectElement4;
	@UiField
	ParamElement movieParamElement4;
	@UiField
	ParamElement qualityParamElement4;
	@UiField
	ParamElement bgColorParamElement4;
	@UiField
	ParamElement flashVarsParamElement4;
	@UiField
	ParamElement allowFullScreenParamElement4;
	@UiField
	ParamElement scaleParamElement4;
	@UiField
	ParamElement allowScriptAccessParamElement4;
	@UiField
	ParamElement baseParamElement4;
	
	interface GettingStartedViewUiBinder extends UiBinder<Widget, GettingStartedView> {
	}

	private Presenter presenter;

	public GettingStartedView() {
		initWidget(uiBinder.createAndBindUi(this));
		
		initializeVideo();
	}
	
	private void initializeVideo() {
        objectElement1.setId("scPlayer");
        objectElement1.setClassName("embeddedObject");
        objectElement1.setWidth("800");
        objectElement1.setHeight("400");
        objectElement1.setData("http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/34abe802-8172-42af-80e7-7780c3e6523e/jingswfplayer.swf");
        objectElement1.setType("application/x-shockwave-flash");
 		
        movieParamElement1.setValue("http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/34abe802-8172-42af-80e7-7780c3e6523e/jingswfplayer.swf");
        qualityParamElement1.setValue("high");
        bgColorParamElement1.setValue("#FFFFFF");
        flashVarsParamElement1.setValue("thumb=http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/34abe802-8172-42af-80e7-7780c3e6523e/FirstFrame.jpg&containerwidth=1557&containerheight=975&content=http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/34abe802-8172-42af-80e7-7780c3e6523e/intro.swf&blurover=false");
        allowFullScreenParamElement1.setValue("true");
        scaleParamElement1.setValue("showall");
        allowScriptAccessParamElement1.setValue("always");
        baseParamElement1.setValue("http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/34abe802-8172-42af-80e7-7780c3e6523e/");
    
        objectElement2.setId("scPlayer");
        objectElement2.setClassName("embeddedObject");
        objectElement2.setWidth("800");
        objectElement2.setHeight("400");
        objectElement2.setData("http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/d7f26a11-07eb-4262-a161-b1282d044fbc/jingswfplayer.swf");
        objectElement2.setType("application/x-shockwave-flash");
 		
        movieParamElement2.setValue("http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/d7f26a11-07eb-4262-a161-b1282d044fbc/jingswfplayer.swf");
        qualityParamElement2.setValue("high");
        bgColorParamElement2.setValue("#FFFFFF");
        flashVarsParamElement2.setValue("thumb=http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/d7f26a11-07eb-4262-a161-b1282d044fbc/FirstFrame.jpg&containerwidth=1554&containerheight=977&content=http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/d7f26a11-07eb-4262-a161-b1282d044fbc/textCapture.swf&blurover=false");
        allowFullScreenParamElement2.setValue("true");
        scaleParamElement2.setValue("showall");
        allowScriptAccessParamElement2.setValue("always");
        baseParamElement2.setValue("http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/d7f26a11-07eb-4262-a161-b1282d044fbc/");
        
        objectElement3.setId("scPlayer");
        objectElement3.setClassName("embeddedObject");
        objectElement3.setWidth("800");
        objectElement3.setHeight("400");
        objectElement3.setData("http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/7c11ff72-d2a9-47bb-9fe1-0980b273a3fc/jingswfplayer.swf");
        objectElement3.setType("application/x-shockwave-flash");
 		
        movieParamElement3.setValue("http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/7c11ff72-d2a9-47bb-9fe1-0980b273a3fc/jingswfplayer.swf");
        qualityParamElement3.setValue("high");
        bgColorParamElement3.setValue("#FFFFFF");
        flashVarsParamElement3.setValue("thumb=http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/7c11ff72-d2a9-47bb-9fe1-0980b273a3fc/FirstFrame.jpg&containerwidth=1554&containerheight=975&content=http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/7c11ff72-d2a9-47bb-9fe1-0980b273a3fc/matrix_2.swf&blurover=false");
        allowFullScreenParamElement3.setValue("true");
        scaleParamElement3.setValue("showall");
        allowScriptAccessParamElement3.setValue("always");
        baseParamElement3.setValue("http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/7c11ff72-d2a9-47bb-9fe1-0980b273a3fc/");
        
        objectElement4.setId("scPlayer");
        objectElement4.setClassName("embeddedObject");
        objectElement4.setWidth("800");
        objectElement4.setHeight("400");
        objectElement4.setData("http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/9209f3fd-5d9c-4039-9f4f-013f0b019232/jingswfplayer.swf");
        objectElement4.setType("application/x-shockwave-flash");
 		
        movieParamElement4.setValue("http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/9209f3fd-5d9c-4039-9f4f-013f0b019232/jingswfplayer.swf");
        qualityParamElement4.setValue("high");
        bgColorParamElement4.setValue("#FFFFFF");
        flashVarsParamElement4.setValue("thumb=http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/9209f3fd-5d9c-4039-9f4f-013f0b019232/FirstFrame.jpg&containerwidth=1555&containerheight=980&content=http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/9209f3fd-5d9c-4039-9f4f-013f0b019232/taxonomyComparison2.swf&blurover=false");
        allowFullScreenParamElement4.setValue("true");
        scaleParamElement4.setValue("showall");
        allowScriptAccessParamElement4.setValue("always");
        baseParamElement4.setValue("http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/9209f3fd-5d9c-4039-9f4f-013f0b019232/");
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
