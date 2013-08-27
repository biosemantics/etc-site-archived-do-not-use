package edu.arizona.sirls.etc.site.client.builder.lib.fileManager;

import gwtupload.client.IUploader;

public class OnStartUploadHandler implements IUploader.OnStartUploaderHandler {

	private FileSelectionHandler fileSelectionHandler;
	
	public OnStartUploadHandler(FileSelectionHandler fileSelectionHandler) {
		this.fileSelectionHandler = fileSelectionHandler;
	}

	@Override
	public void onStart(IUploader uploader) {
		uploader.setServletPath(uploader.getServletPath() + "&target=" + fileSelectionHandler.getTarget());
	}

}
