package edu.arizona.biosemantics.etcsite.client.content.sample;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ObjectElement;
import com.google.gwt.dom.client.ParamElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
//no longer there import com.google.gwt.widget.client.TextButton;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.User.EmailPreference;

public class SampleView extends Composite implements ISampleView {

	private static SampleViewUiBinder uiBinder = GWT.create(SampleViewUiBinder.class);

	@UiTemplate("SampleView.ui.xml")
	interface SampleViewUiBinder extends UiBinder<Widget, SampleView> {}
	
	@UiField
	TextArea textArea;
	
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
	
	private Presenter presenter;
	
	public SampleView() {
		initWidget(uiBinder.createAndBindUi(this));
		textArea.setText("author: Fernald\n" + 
				"year: 1950\n" + 
				"title: Gray's Manual of Botany\n" + 
				"family name: Rosaceae Linnaeus, 1735\n" + 
				"morphology: Plants with regular flowers, numerous (rarely few) distinct stamens inserted on the calyx, and 1-many carpels, which are quite distinct, or (in the second tribe) united and combined with the calyx-tube.\n" + 
				"\n" + 
				"author: Fernald\n" + 
				"year: 1950\n" + 
				"title: Gray's Manual of Botany\n" + 
				"family name: Rosaceae Linnaeus, 1735\n" + 
				"genus name: Rubus Linnaeus, 1735\n" + 
				"morphology: Calyx 5(3-7)-parted, without bractlets. Petals mostly 5, deciduous.\n" + 
				"\n" + 
				"\n" + 
				"author: Fernald\n" + 
				"year: 1950\n" + 
				"title: Gray's Manual of Botany\n" + 
				"family name: Rosaceae Linnaeus, 1735\n" + 
				"genus name: Rubus Linnaeus, 1735\n" + 
				"species name: Allegheniensis Porter, unspecified\n" + 
				"morphology: #Erect or high arching, mostly 1-3 m. high, armed with scattered broad-based lanceolate to lance-subulate prickles or prickles wanting, the young primocanes often ridged or angled and finely pubescent, the expanding tip often glandular;\n" + 
				"\n" + 
				"Canes up to 1 cm. or more thick at base; terminal primocane-leaflet 0.7-2 dm. long, 3.5-11 cm. broad; lower elongate pedicels often forking.#");
		
		initializeVideo();
	}
	private void initializeVideo() {
		String width = "950";
		String height = "600";
        objectElement1.setId("scPlayer");
        objectElement1.setClassName("embeddedObject");
        objectElement1.setWidth(width);
        objectElement1.setHeight(height);
        objectElement1.setData("http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/ce21afd4-dcf5-4c2a-b848-869ac1377dc4/jingswfplayer.swf");
        objectElement1.setType("application/x-shockwave-flash");
 		
        movieParamElement1.setValue("http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/ce21afd4-dcf5-4c2a-b848-869ac1377dc4/jingswfplayer.swf");
        qualityParamElement1.setValue("high");
        bgColorParamElement1.setValue("#FFFFFF");
        flashVarsParamElement1.setValue("containerwidth=" + width + "&containerheight=" + height + "&thumb=http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/ce21afd4-dcf5-4c2a-b848-869ac1377dc4/FirstFrame.jpg&content=http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/ce21afd4-dcf5-4c2a-b848-869ac1377dc4/2015-06-03_1154.swf&blurover=false");
        allowFullScreenParamElement1.setValue("true");
        scaleParamElement1.setValue("showall");
        allowScriptAccessParamElement1.setValue("always");
        baseParamElement1.setValue("http://content.screencast.com/users/thomas.rodenhausen/folders/Jing/media/ce21afd4-dcf5-4c2a-b848-869ac1377dc4/");
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}