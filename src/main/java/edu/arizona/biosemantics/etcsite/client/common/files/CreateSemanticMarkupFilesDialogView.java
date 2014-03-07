package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.user.client.ui.ICancelConfirmHandler;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.inject.Inject;

public class CreateSemanticMarkupFilesDialogView implements ICreateSemanticMarkupFilesDialogView {

	private TitleCloseDialogBox dialogBox;
	private ICreateSemanticMarkupFilesView createSemanticMarkupFilesView;
	private ICreateSemanticMarkupFilesDialogView.Presenter presenter;
	
	@Inject
	public CreateSemanticMarkupFilesDialogView(ICreateSemanticMarkupFilesView.Presenter presenter) {
		this.createSemanticMarkupFilesView = presenter.getView();
		this.dialogBox = new TitleCloseDialogBox(true, "File Manager");
		dialogBox.setWidget(createSemanticMarkupFilesView);
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
