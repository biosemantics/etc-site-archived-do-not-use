package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ImageLabel extends Composite implements IImageLabel, HasClickHandlers {

	private static ImageLabelCompositeUiBinder uiBinder = GWT.create(ImageLabelCompositeUiBinder.class);

	interface ImageLabelCompositeUiBinder extends UiBinder<Widget, ImageLabel> {
	}

	@UiConstructor
	public ImageLabel(String imagePath, String imageWidth, String imageHeight, String text) {
		image = new Image(UriUtils.fromString(imagePath));
		image.setWidth(imageWidth);
		image.setHeight(imageHeight);
		label = new Label(text);
		
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField(provided=true)
	Image image;
	
	@UiField(provided=true)
	Label label;

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	public void setImage(String url) {
		image.setUrl(url);
	}
	
}
