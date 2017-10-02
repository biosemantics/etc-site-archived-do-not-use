package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent.MaximizeHandler;

public class CreateSemanticMarkupFilesDialogView implements
		ICreateSemanticMarkupFilesDialogView {

	private static CreateSemanticMarkupFilesDialogViewUiBinder uiBinder = GWT
			.create(CreateSemanticMarkupFilesDialogViewUiBinder.class);

	interface CreateSemanticMarkupFilesDialogViewUiBinder extends
			UiBinder<Widget, CreateSemanticMarkupFilesDialogView> {
	}

	private ICreateSemanticMarkupFilesDialogView.Presenter presenter;

	@UiField(provided = true)
	ICreateSemanticMarkupFilesView createSemanticMarkupFilesView;

	private Dialog dialog;

	@Inject
	public CreateSemanticMarkupFilesDialogView(
			final ICreateSemanticMarkupFilesView.Presenter presenter) {
		this.createSemanticMarkupFilesView = presenter.getView();
		Widget scrollPanelView = uiBinder.createAndBindUi(this);

		dialog = new Dialog();
		dialog.setBodyBorder(false);
		dialog.setHeading("Create Files");
		dialog.setPixelSize(-1, -1);
		dialog.setMinWidth(0);
		dialog.setMinHeight(0);
	    dialog.setResizable(true);
	    dialog.setShadow(true);
		dialog.setHideOnButtonClick(true);
		dialog.setMaximizable(true);
		dialog.getButton(PredefinedButton.OK).setText("Close");

		dialog.add(scrollPanelView);
		dialog.addHideHandler(new HideEvent.HideHandler() {
			@Override
			public void onHide(HideEvent event) {
				CreateSemanticMarkupFilesDialogView.this.presenter.onCancel();
				CreateSemanticMarkupFilesDialogView.this.presenter.onClose();
			}
		});
	}

	@Override
	public void show() {
		dialog.show();
	}

	@Override
	public void hide() {
		dialog.hide();
	}

	@Override
	public void setPresenter(
			ICreateSemanticMarkupFilesDialogView.Presenter presenter) {
		this.presenter = presenter;
	}

}
