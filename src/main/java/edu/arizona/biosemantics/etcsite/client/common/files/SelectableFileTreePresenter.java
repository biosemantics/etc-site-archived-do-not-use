package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;

public class SelectableFileTreePresenter implements ISelectableFileTreeView.Presenter {

	private ISelectableFileTreeView view;
	private ISelectListener currentListener;
	private IFileTreeView.Presenter fileTreePresenter;
	private FileFilter fileFilter;
	private Dialog dialog;
	
	@Inject
	public SelectableFileTreePresenter(ISelectableFileTreeView view, 
			IFileTreeView.Presenter fileTreePresenter) {
		this.view = view;
		this.view.setPresenter(this);
		
		dialog = new Dialog();
		dialog.setBodyBorder(false);
		dialog.setHeading("Create Files");
		dialog.setPixelSize(-1, -1);
		dialog.setMinWidth(500);
		dialog.setMinHeight(0);
	    dialog.setResizable(true);
	    dialog.setShadow(true);
		dialog.setHideOnButtonClick(true);
		dialog.setPredefinedButtons();

		dialog.add(view);
		
		this.fileTreePresenter = fileTreePresenter;
	}
	
	public void show(String title, FileFilter fileFilter, ISelectListener listener) {
		this.currentListener = listener;
		dialog.setHeading(title);
		dialog.show();
		this.fileFilter = fileFilter;
		fileTreePresenter.getView().refresh(fileFilter);
	}
	
	@Override
	public void hide() {
		dialog.hide();
	}
	
	@Override
	public void onSelect() {
		if(currentListener != null)
			currentListener.onSelect();
		dialog.hide();
	}

	@Override
	public void onClose() {
		dialog.hide();
	}
	
	@Override
	public ISelectableFileTreeView getView() {
		return view;
	}
	
	public interface ISelectListener {
		public void onSelect();
	}

	@Override
	public IFileTreeView.Presenter getFileTreePresenter() {
		return this.fileTreePresenter;
	}

}
