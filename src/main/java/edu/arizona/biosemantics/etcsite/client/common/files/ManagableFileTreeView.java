package edu.arizona.biosemantics.etcsite.client.common.files;

import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ManagableFileTreeView extends Composite implements IManagableFileTreeView {

	private static ManagableFileTreeViewUiBinder uiBinder = GWT.create(ManagableFileTreeViewUiBinder.class);

	interface ManagableFileTreeViewUiBinder extends UiBinder<Widget, ManagableFileTreeView> {
	}

	private Presenter presenter;
	
	@UiField
	SingleUploader uploader;
	
	@UiField
	SimplePanel statusWidgetContainer;
	
	@UiField(provided = true)
	IFileTreeView fileTreeView;
	
	@UiField
	Button renameButton;
	
	@UiField
	Button deleteButton;
	
	@UiField
	Button createButton;
	
	@UiField
	Button addButton;
	
	@Inject
	public ManagableFileTreeView(IFileTreeView.Presenter fileTreePresenter) {
		this.fileTreeView = fileTreePresenter.getView();
		initWidget(uiBinder.createAndBindUi(this));
		statusWidgetContainer.setWidget(uploader.getStatusWidget().getWidget());
	}
	
	@UiHandler("createButton")
	public void onCreate(ClickEvent event) {
		presenter.onCreate();
	}
	
	@UiHandler("renameButton")
	public void onRename(ClickEvent event) {
		presenter.onRename();
	}
	
	@UiHandler("deleteButton")
	public void onDelete(ClickEvent event) {
		presenter.onDelete();
	}
	
	@UiHandler("downloadButton")
	public void onDownload(ClickEvent event) {
		presenter.onDownload();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public IUploader getUploader() {
		return uploader;
	}

	@Override
	public void setEnabledCreateDirectory(boolean value) {
		this.createButton.setEnabled(value);
	}

	@Override
	public void setEnabledRename(boolean value) {
		this.renameButton.setEnabled(value);
	}

	@Override
	public void setEnabledDelete(boolean value) {
		this.deleteButton.setEnabled(value);
	}

	@Override
	public void setEnabledUpload(boolean value) {
		this.addButton.setEnabled(value);
		if(!value) {
			this.uploader.getFileInput().getWidget().getElement().setAttribute("aria-hidden", "false");
			this.addButton.getElement().setAttribute("aria-hidden", "false");
		}
	}

	@Override
	public void setStatusWidget(Widget widget) {
		statusWidgetContainer.setWidget(widget);
	}

	@Override
	public Button getAddButton() {
		return this.addButton;
	}
}
