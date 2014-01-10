package edu.arizona.biosemantics.etcsite.client.common.files;

import gwtupload.client.IUploader;

public class MyUploaderConstants implements IUploader.UploaderConstants  {

	@Override
	@DefaultStringValue(" ")
	public String uploadLabelCancel() {
		return "upload label cancel";
	}

	@Override
	@DefaultStringValue("Canceled")
	public String uploadStatusCanceled() {
		return "Canceled";
	}

	@Override
	@DefaultStringValue("Canceling ...")
	public String uploadStatusCanceling() {
		return "Canceling ...";
	}

	@Override
	@DefaultStringValue("Deleted")
	public String uploadStatusDeleted() {
		return "Deleted";
	}

	@Override
	@DefaultStringValue("Error")
	public String uploadStatusError() {
		return "Error";
	}

	@Override
	@DefaultStringValue("In progress")
	public String uploadStatusInProgress() {
		return "In progress";
	}

	@Override
	@DefaultStringValue("Queued")
	public String uploadStatusQueued() {
		return "Queued";
	}

	@Override
	@DefaultStringValue("Submitting form ...")
	public String uploadStatusSubmitting() {
		return "Submitting form ...";
	}

	@Override
	@DefaultStringValue("Done")
	public String uploadStatusSuccess() {
		return "Done";
	}

	@Override
	@DefaultStringValue("There is already an active upload, try later.")
	public String uploaderActiveUpload() {
		return "There is already an active upload, try later.";
	}

	@Override
	@DefaultStringValue("This file was already uploaded.")
	public String uploaderAlreadyDone() {
		return "This file was already uploaded.";
	}

	@Override
	@DefaultStringValue("It seems the application is configured to use GAE blobstore.\nThe server has raised an error while creating an Upload-Url\nBe sure thar you have enabled billing for this application in order to use blobstore.")
	public String uploaderBlobstoreError() {
		return "It seems the application is configured to use GAE blobstore.\nThe server has raised an error while creating an Upload-Url\nBe sure thar you ." +
				"have enabled billing for this application in order to use blobstore.";
	}

	@Override
	@DefaultStringValue("Choose a file to upload ...")
	public String uploaderBrowse() {
		return "Add files";
	}

	@Override
	@DefaultStringValue("Invalid file.\nOnly these types are allowed:\n")
	public String uploaderInvalidExtension() {
		return "Invalid file.\nOnly these types are allowed:\n";
	}

	@Override
	@DefaultStringValue("Send")
	public String uploaderSend() {
		return "Add file";
	}

	@Override
	@DefaultStringValue("Invalid server response. Have you configured correctly your application in the server side?")
	public String uploaderServerError() {
		return "Invalid server response. Have you configured correctly your application in the server side?";
	}

	@Override
	@DefaultStringValue("Unable to auto submit the form, it seems your browser has security issues with this feature.\n Developer Info: If you are using jsupload and you do not need cross-domain, try a version compiled with the standard linker?")
	public String submitError() {
		return "Unable to auto submit the form, it seems your browser has security issues with this feature.\n " +
				"Developer Info: If you are using jsupload and you do not need cross-domain, try a version compiled with the standard linker?";
	}

	@Override
	@DefaultStringValue("Unable to contact with the server: ")
	public String uploaderServerUnavailable() {
		return "Unable to contact with the server: ";
	}

	@Override
	@DefaultStringValue("Timeout sending the file:\n perhaps your browser does not send files correctly,\n your session has expired,\n or there was a server error.\nPlease try again.")
	public String uploaderTimeout() {
		return "Timeout sending the file:\n perhaps your browser does not send files correctly,\n your session has expired,\n or there was a server error.\nPlease try again.";
	}

	@Override
	@DefaultStringValue("Error uploading the file, the server response has a format which can not be parsed by the application.\n.")
	public String uploaderBadServerResponse() {
		return "Error uploading the file, the server response has a format which can not be parsed by the application.\n.";
	}

	@Override
	@DefaultStringValue("Additional information: it seems that you are using blobstore, so in order to upload large files check that your application is billing enabled.")
	public String uploaderBlobstoreBilling() {
		return "Additional information: it seems that you are using blobstore, so in order to upload large files check that your application is billing enabled.";
	}

	@Override
	@DefaultStringValue("Error you have typed an invalid file name, please select a valid one.")
	public String uploaderInvalidPathError() {
		return "Error you have typed an invalid file name, please select a valid one.";
	}

}
