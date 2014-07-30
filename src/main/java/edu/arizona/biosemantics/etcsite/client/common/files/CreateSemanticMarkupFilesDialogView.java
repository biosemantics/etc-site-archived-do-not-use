package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.ICancelConfirmHandler;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.files.CreateSemanticMarkupFilesView.CreateSemanticMarkupFilesViewUiBinder;

public class CreateSemanticMarkupFilesDialogView implements ICreateSemanticMarkupFilesDialogView {

	private static CreateSemanticMarkupFilesDialogViewUiBinder uiBinder = GWT.create(CreateSemanticMarkupFilesDialogViewUiBinder.class);
	
	interface CreateSemanticMarkupFilesDialogViewUiBinder extends UiBinder<Widget, CreateSemanticMarkupFilesDialogView> { }
	
	private PopupPanel dialogBox;
	private ICreateSemanticMarkupFilesDialogView.Presenter presenter;
	
	@UiField(provided=true)
	ICreateSemanticMarkupFilesView createSemanticMarkupFilesView;
	
	@Inject
	public CreateSemanticMarkupFilesDialogView(final ICreateSemanticMarkupFilesView.Presenter presenter) {
		this.createSemanticMarkupFilesView = presenter.getView();
		Widget scrollPanelView = uiBinder.createAndBindUi(this);
		this.dialogBox = new PopupPanel(true); //true means that the popup will close when the user clicks outside of it. 
		this.dialogBox.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				CreateSemanticMarkupFilesDialogView.this.presenter.onCancel();
				CreateSemanticMarkupFilesDialogView.this.presenter.onClose();
			}
		});
		dialogBox.setGlassEnabled(true);
		dialogBox.add(scrollPanelView);
	}

	@Override
	public void show() {
		dialogBox.center();
	}
	
	@UiHandler("cancelButton")
	public void onCancel(ClickEvent event) {
		presenter.onCancel();
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
