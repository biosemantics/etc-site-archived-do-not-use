package edu.arizona.biosemantics.etcsite.client.content.fileManager;

import com.google.gwt.activity.shared.MyActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;


public class FileManagerActivity implements MyActivity {

	private IFileManagerView.Presenter fileManagerPresenter;
	
	@Inject
	public FileManagerActivity(IFileManagerView.Presenter fileManagerPresenter) {
		super();
		this.fileManagerPresenter = fileManagerPresenter;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		fileManagerPresenter.refresh();
		panel.setWidget(fileManagerPresenter.getView());
	}

	@Override
	public String mayStop() {
		return null;
	}

	@Override
	public void onCancel() { }

	@Override
	public void onStop() { }

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
