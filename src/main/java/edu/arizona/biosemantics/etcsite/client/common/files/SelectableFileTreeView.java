package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SelectableFileTreeView extends Composite implements ISelectableFileTreeView {

	private static SelectableFileTreeViewUiBinder uiBinder = GWT.create(SelectableFileTreeViewUiBinder.class);

	interface SelectableFileTreeViewUiBinder extends UiBinder<Widget, SelectableFileTreeView> {
	}

	@UiField
	Button selectButton;
	
	@UiField
	Button closeButton;
	
	@UiField(provided = true)
	IFileTreeView fileTreeView;

	private Presenter presenter;
	
	@Inject
	public SelectableFileTreeView(IFileTreeView.Presenter fileTreePresenter) {
		this.fileTreeView = fileTreePresenter.getView();
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("selectButton")
	public void onSelect(ClickEvent event) {
		presenter.onSelect();
	}
	
	@UiHandler("closeButton")
	public void onClose(ClickEvent event) {
		presenter.onClose();
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	
}
