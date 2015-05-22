package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ProcessingView extends Composite implements IProcessingView {
	
	private static ProcessingViewwUiBinder uiBinder = GWT.create(ProcessingViewwUiBinder.class);

	interface ProcessingViewwUiBinder extends UiBinder<Widget, ProcessingView> {
	}
	
	@UiField
	Anchor taskManagerAnchor;
	
	@Inject
	public ProcessingView() { 
		initWidget(uiBinder.createAndBindUi(this));
		taskManagerAnchor.getElement().getStyle().setCursor(Cursor.POINTER);
	}
	
	private Presenter presenter;

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("taskManagerAnchor")
	public void onTaskManager(ClickEvent event) {
		presenter.onTaskManager();
	}


}
