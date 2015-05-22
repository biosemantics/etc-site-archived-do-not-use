package edu.arizona.biosemantics.etcsite.client.content.about;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


public class AboutView extends Composite implements IAboutView {
	
	@UiField
	Anchor getStartAnchor;

	private static AboutViewUiBinder uiBinder = GWT.create(AboutViewUiBinder.class);

	interface AboutViewUiBinder extends UiBinder<Widget, AboutView> {
	}

	private Presenter presenter;

	public AboutView() {
		initWidget(uiBinder.createAndBindUi(this));
		getStartAnchor.getElement().getStyle().setCursor(Cursor.POINTER);
		getStartAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onGetStart();
			}
		});
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	

}

