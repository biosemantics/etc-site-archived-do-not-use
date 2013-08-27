package edu.arizona.sirls.etc.site.client.builder.lib.fileManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Tree;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.api.file.IUserFilesAsyncCallbackListener;
import edu.arizona.sirls.etc.site.client.api.file.UserFilesAsyncCallback;
import edu.arizona.sirls.etc.site.client.widget.FileTreeComposite;
import edu.arizona.sirls.etc.site.client.widget.FileTreeDecorator;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;

import gwtupload.client.IUploader;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.MultiUploader;

public class OnFinishUploadHandler implements IUploader.OnFinishUploaderHandler, IUserFilesAsyncCallbackListener {

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
			UserFilesAsyncCallback callback = new UserFilesAsyncCallback();
			callback.addListener(this);
			fileService.getUsersFiles(Authentication.getInstance().getAuthenticationToken(), callback);
		}
		uploader.setServletPath(defaultServletPath);
	}

	@Override
	public void notifyResult(
			edu.arizona.sirls.etc.site.shared.rpc.Tree<String> result,
			UserFilesAsyncCallback userFilesAsyncCallback) {
		tree.refresh();
	}

	@Override
	public void notifyException(Throwable caught,
			UserFilesAsyncCallback userFilesAsyncCallback) {
		caught.printStackTrace();
	}
	
	
}
