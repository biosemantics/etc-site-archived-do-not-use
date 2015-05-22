package com.google.gwt.user.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.arizona.biosemantics.etcsite.client.common.CaptchaPresenter;

public class CaptchaPanel extends VerticalPanel{
	private static final String captchaImageBase = GWT.getModuleBaseURL() + "captcha";
	
	private TextBox captchaBox;
	private Image captchaImage;
	
	private int captchaId;
	
	private CaptchaPresenter presenter;
	
	public CaptchaPanel(){
		
		captchaBox = new TextBox();
		captchaBox.setPixelSize(100, 14);
		
		captchaImage = new Image();
		
		captchaImage.setVisible(true);
		captchaImage.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				presenter.requestNewCaptcha();
				captchaBox.setText("");
			}
		});
		
		Label introLabel = new Label("Enter the security key displayed above.");
		introLabel.getElement().getStyle().setFontSize(10.0, Unit.PX);
		
		Label cantReadLabel = new Label("(Can't read it? Click the image to generate a new code.)");
		cantReadLabel.getElement().getStyle().setFontSize(10.0, Unit.PX);
		
		this.setSpacing(3);
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		this.add(captchaImage);
		this.add(introLabel);
		this.add(captchaBox);
		
		//this.add(cantReadLabel);
	}
	
	public void setPresenter(CaptchaPresenter presenter){
		this.presenter = presenter;
	}
	
	public int getId(){
		return captchaId;
	}
	
	public String getSolution(){
		return captchaBox.getText();
	}
	
	public void clearText(){
		captchaBox.setText("");
	}

	public void updateCaptcha(int id) {
		captchaId = id;
		captchaImage.setUrl(captchaImageBase + "/" + captchaId);
		clearText();
	}
}
