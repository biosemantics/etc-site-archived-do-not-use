package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.file.FileFilter;

public class SelectableFileTreePresenter implements ISelectableFileTreeView.Presenter {

	private ISelectableFileTreeView view;
	private TitleCloseDialogBox dialogBox;
	private ISelectListener currentListener;
	private IFileTreeView.Presenter fileTreePresenter;
	private FileFilter fileFilter;
	
	@Inject
	public SelectableFileTreePresenter(ISelectableFileTreeView view, 
			IFileTreeView.Presenter fileTreePresenter) {
		this.view = view;
		this.view.setPresenter(this);
		this.dialogBox = new TitleCloseDialogBox(true, "Select File");
		dialogBox.setGlassEnabled(true);
		dialogBox.setAnimationEnabled(true);
		dialogBox.add(view.asWidget());
		this.fileTreePresenter = fileTreePresenter;
	}
	
	public void show(String title, FileFilter fileFilter, ISelectListener listener) {
		this.currentListener = listener;
		dialogBox.setTitle(title);
		dialogBox.center();
		this.fileFilter = fileFilter;
		fileTreePresenter.refresh(fileFilter);
	}
	
	@Override
	public void hide() {
		dialogBox.hide();
	}
	
	@Override
	public void onSelect() {
		if(currentListener != null)
			currentListener.onSelect();
		dialogBox.hide();
	}

	@Override
	public void onClose() {
		dialogBox.hide();
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
