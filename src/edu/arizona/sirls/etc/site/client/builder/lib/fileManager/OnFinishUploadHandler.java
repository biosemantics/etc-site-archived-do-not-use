package edu.arizona.sirls.etc.site.client.builder.lib.fileManager;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.widget.FileTreeComposite;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OnFinishUploadHandler implements IUploader.OnFinishUploaderHandler {

	private FileTreeComposite tree;
	private final IFileServiceAsync fileService = GWT.create(IFileService.class);
	private String defaultServletPath;

	public OnFinishUploadHandler(FileTreeComposite tree, String defaultServletPath) {
		this.tree = tree;
		this.defaultServletPath = defaultServletPath;
	}

	@Override
	public void onFinish(IUploader uploader) {
		if (uploader.getStatus() == Status.SUCCESS) {
			fileService.getUsersFiles(Authentication.getInstance().getAuthenticationToken(), userFilesCallback);
		}
		uploader.setServletPath(defaultServletPath);
	}
	
	private AsyncCallback<edu.arizona.sirls.etc.site.shared.rpc.Tree<String>> userFilesCallback = new AsyncCallback<edu.arizona.sirls.etc.site.shared.rpc.Tree<String>>() {
		public void onSuccess(edu.arizona.sirls.etc.site.shared.rpc.Tree<String> result) {
			tree.refresh();
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}
	};
	
	
}
