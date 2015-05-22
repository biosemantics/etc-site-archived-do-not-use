package edu.arizona.biosemantics.etcsite.client.content.home;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class HomeHeaderView extends Composite implements IHomeHeaderView {

	private static HomeHeaderViewUiBinder uiBinder = GWT.create(HomeHeaderViewUiBinder.class);

	interface HomeHeaderViewUiBinder extends UiBinder<Widget, HomeHeaderView> {
	}

	private Presenter presenter;
	
	@UiField
	Anchor aboutAnchor;
	
	public HomeHeaderView() {
		initWidget(uiBinder.createAndBindUi(this));
		aboutAnchor.getElement().getStyle().setCursor(Cursor.POINTER);
		aboutAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onAbout();
			}
		});
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
}