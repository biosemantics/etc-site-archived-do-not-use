package edu.arizona.biosemantics.etcsite.client.common.files;


import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.LoadingPopup;
import edu.arizona.biosemantics.etcsite.shared.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

public class FileContentPresenter implements IFileContentView.Presenter {

	private IFileContentView view;
	private IFileAccessServiceAsync fileAccessService;
	private TitleCloseDialogBox dialogBox;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private String currentPath;

	@Inject
	public FileContentPresenter(IFileContentView view, IFileAccessServiceAsync fileAccessService) {
		this.view = view;
		view.setPresenter(this);
		this.fileAccessService = fileAccessService;
		this.dialogBox = new TitleCloseDialogBox("File Content");
		this.dialogBox.setGlassEnabled(true);
		this.dialogBox.setWidget(view);
		
	}

	public void show(String path) {
		this.currentPath = path;
		this.dialogBox.center();
		loadingPopup.start();
		FileTypeEnum fileType = FileTypeEnum.getEnum(view.getFormat());
		fileAccessService.getFileContent(Authentication.getInstance().getToken(), 
				currentPath, fileType, new FileContentCallback(true, loadingPopup));
	}
	
	@Override
	public void onFormatChange(String format) {
		loadingPopup.start();
		FileTypeEnum fileType = FileTypeEnum.getEnum(format);
		fileAccessService.getFileContent(
				Authentication.getInstance().getToken(), 
				currentPath, fileType, new FileContentCallback(false, loadingPopup));
	}
	
	@Override
	public void onClose() {
		dialogBox.hide();
	}
	
	private class FileContentCallback extends RPCCallback<String> {
		private boolean center;
		public FileContentCallback(boolean center, LoadingPopup loadingPopup) {
			super(loadingPopup);
			this.center = center;
		}
		@Override
		public void onResult(String result) {
			view.setText(result);
			if(center)
				dialogBox.center();
		}
	}

}
