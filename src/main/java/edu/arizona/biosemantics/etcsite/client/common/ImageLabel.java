package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ImageLabel extends Composite implements IImageLabel, HasClickHandlers, HasMouseOutHandlers, HasMouseOverHandlers {
	
	/*interface MyStyle extends CssResource {
	    //String enabled();
	    //String disabled();
	    String systemFolder();
	  }
	 
	@UiField 
	MyStyle style;*/
	 
	@UiField(provided=true)
	Image image;
	
	@UiField(provided=true)
	Label label;
	
	private static ImageLabelCompositeUiBinder uiBinder = GWT.create(ImageLabelCompositeUiBinder.class);

	interface ImageLabelCompositeUiBinder extends UiBinder<Widget, ImageLabel> {
	}

	@UiConstructor
	public ImageLabel(String imagePath, String imageWidth, String imageHeight, String text, boolean systemFolder) {
		image = new Image(UriUtils.fromString(imagePath));
		image.setWidth(imageWidth);
		image.setHeight(imageHeight);
		label = new Label(text);
		//if(systemFolder) label.addStyleName(style.systemFolder());
		
		initWidget(uiBinder.createAndBindUi(this));
	}

	

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	public void setImage(String url) {
		image.setUrl(url);
	}
	
	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	public String getImage() {
		return image.getUrl();
	}
	
	public void setText(String text) {
		label.setText(text);
	}

	
}
