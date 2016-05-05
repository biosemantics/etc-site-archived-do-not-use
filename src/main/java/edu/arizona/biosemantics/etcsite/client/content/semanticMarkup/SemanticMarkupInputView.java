package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

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
import com.google.inject.name.Named;

import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView;
import edu.arizona.biosemantics.etcsite.client.common.InputCreateView;

public class SemanticMarkupInputView extends Composite implements ISemanticMarkupInputView{

	private static SemanticmarkupInputViewUiBinder uiBinder = GWT
			.create(SemanticmarkupInputViewUiBinder.class);

	interface SemanticmarkupInputViewUiBinder extends
			UiBinder<Widget, SemanticMarkupInputView> {
	}

	private ISemanticMarkupInputView.Presenter presenter;
	
	@UiField 
	Anchor fileManagerAnchor;
	
	@UiField(provided=true) 
	IInputCreateView inputCreateView;
	
	@Inject
	public SemanticMarkupInputView(@Named("SemanticMarkup") IInputCreateView.Presenter inputCreatePresenter) {
		this.inputCreateView = inputCreatePresenter.getView();
		initWidget(uiBinder.createAndBindUi(this));
		fileManagerAnchor.getElement().getStyle().setCursor(Cursor.POINTER);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
		
	@UiHandler("fileManagerAnchor")
	public void onFileManagerAnchor(ClickEvent event){
		presenter.onFileManager();
	}	
	
	@Override
	public IInputCreateView getInputCreateView() {
		return inputCreateView;
	}
	
}
