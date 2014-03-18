package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ICancelConfirmHandler;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.files.CreateSemanticMarkupFilesView.CreateSemanticMarkupFilesViewUiBinder;

public class CreateSemanticMarkupFilesDialogView implements ICreateSemanticMarkupFilesDialogView {

	private static CreateSemanticMarkupFilesDialogViewUiBinder uiBinder = GWT.create(CreateSemanticMarkupFilesDialogViewUiBinder.class);
	
	interface CreateSemanticMarkupFilesDialogViewUiBinder extends UiBinder<Widget, CreateSemanticMarkupFilesDialogView> { }
	
	private TitleCloseDialogBox dialogBox;
	private ICreateSemanticMarkupFilesDialogView.Presenter presenter;
	
	@UiField(provided=true)
	ICreateSemanticMarkupFilesView createSemanticMarkupFilesView;
	
	@Inject
	public CreateSemanticMarkupFilesDialogView(ICreateSemanticMarkupFilesView.Presenter presenter) {
		this.createSemanticMarkupFilesView = presenter.getView();
		Widget scrollPanelView = uiBinder.createAndBindUi(this);
		this.dialogBox = new TitleCloseDialogBox(true, "Create Input XML File for Semantic Markup Task");
		dialogBox.setWidget(scrollPanelView);
		dialogBox.setGlassEnabled(true);
	}

	@Override
	public void show() {
		dialogBox.center();
		dialogBox.setCancelConfirmHandler(new ICancelConfirmHandler() {
			@Override
			public void cancel() {
				presenter.onCancel();
			}
			@Override
			public void confirm() {
			}
		});
	}

	@Override
	public void hide() {
		dialogBox.hide();
	}

	@Override
	public void setPresenter(ICreateSemanticMarkupFilesDialogView.Presenter presenter) {
		this.presenter = presenter;
	}


}
