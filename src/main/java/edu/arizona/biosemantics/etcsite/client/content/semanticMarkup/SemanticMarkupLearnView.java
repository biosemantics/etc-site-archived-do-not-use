package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SemanticMarkupLearnView extends Composite implements ISemanticMarkupLearnView {

	private static SemanticMarkupLearnViewUiBinder uiBinder = GWT
			.create(SemanticMarkupLearnViewUiBinder.class);

	interface SemanticMarkupLearnViewUiBinder extends
			UiBinder<Widget, SemanticMarkupLearnView> {
	}

	@UiField
	Button nextButton;
	
	@UiField
	HorizontalPanel resumablePanel;

	@UiField
	HorizontalPanel nonResumablePanel;
	
	private Presenter presenter;

	public SemanticMarkupLearnView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("taskManagerAnchor")
	public void onTaskManager(ClickEvent event) {
		presenter.onTaskManager();
	}

	@Override
	public void setNonResumable() {
		this.nextButton.setVisible(false);
		this.nonResumablePanel.setVisible(true);
		this.resumablePanel.setVisible(false);
	}
	
	@Override
	public void setResumable() {
		this.nextButton.setVisible(true);
		this.nonResumablePanel.setVisible(false);
		this.resumablePanel.setVisible(true);
	}
	
	@UiHandler("nextButton")
	public void onNext(ClickEvent event) {
		presenter.onNext();
	}
	
	@UiHandler("playImage")
	public void onPlay(ClickEvent event) {
		presenter.onNext();
	}
}
