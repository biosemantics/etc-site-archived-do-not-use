package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * A composite of a TextBox and a CheckBox that optionally enables it.
 */
public class ImageLabelComposite extends Composite {

	private Label label;
	private Image image;
	private String path;

	public ImageLabelComposite(String imageUri, String width, String height, String labelText) {
		image = new Image(UriUtils.fromString(imageUri));
		image.setWidth(width);
		image.setHeight(height);
		label = new Label(labelText);
		
		HorizontalPanel panel = new HorizontalPanel();
		//panel.setSpacing(10);
		panel.add(image);
		panel.add(label);
		
		
		// All composites must call initWidget() in their constructors.
		initWidget(panel);
		
		// Give the overall composite a style name.
		setStyleName("ImageLabelComposite");
	}
	
	public void addClickHandler(ClickHandler clickHandler) {
		image.addClickHandler(clickHandler);
		label.addClickHandler(clickHandler);
	}

	
	public void setLabelText(String text) {
		this.label.setText(text);
	}
	
	public String getLabelText() {
		return this.label.getText();
	}


	public Label getLabel() {
		return label;
	}


	public void setLabel(Label label) {
		this.label = label;
	}


	public Image getImage() {
		return image;
	}


	public void setImage(Image image) {
		this.image.setUrl(image.getUrl());
		
	}
	
	public void setImageWidth(String width) {
		this.image.setWidth(width);
	}
	
	public void setImageHeight(String height) {
		this.image.setHeight(height);
	}
}


