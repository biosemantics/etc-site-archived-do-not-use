package edu.arizona.sirls.etc.site.client.view.fileManager;

import gwtupload.client.SingleUploader;

public class MyUploader extends SingleUploader {
	  
	@Override
	protected void onStartUpload() {
		super.onStartUpload();
		// default implementation would make button invisible. We just want to deactivate it.
		button.setVisible(true);
	}
	
	 @Override
	 protected void onFinishUpload() {
		super.onFinishUpload();
		//the visibility flag is used in the uploader class to determine whether a second button is to be displayed for non-autosubmit
		//if the flag is not set, the non-autosubmit button will be added additionally, which is not wanted.
		button.setVisible(false);
	 }
}
