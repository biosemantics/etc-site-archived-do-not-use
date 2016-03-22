package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import gwtupload.client.IUploader;

public class FileUploadHandler {
	
	//private static final int MAX_FILES_TO_REPORT_FAILED = 20;
	//private static final String NEWLINE = "\n";
	
	List<String> uploadedFiles;
	String uploadServerResponse;
	String keyValidationServerResponse;
	String taxonNameValidationServerResponse;
	ManagableFileTreePresenter presenter;
	private IFileServiceAsync fileService;

	public FileUploadHandler(IFileServiceAsync fileService) {
		uploadedFiles = null;
		uploadServerResponse = null;
		keyValidationServerResponse = null;
		taxonNameValidationServerResponse = null;
		presenter = null;
		this.fileService = fileService;
	}
	
	public FileUploadHandler(ManagableFileTreePresenter presenter, IFileServiceAsync fileService) {
		uploadedFiles = null;
		uploadServerResponse = null;
		keyValidationServerResponse = null;
		taxonNameValidationServerResponse = null;
		this.presenter = presenter;
		this.fileService = fileService;
	}
	
	public String parseServerResponse(IUploader uploader){
		uploadServerResponse = uploader.getServerMessage().getMessage();
		uploadedFiles = uploader.getFileInput().getFilenames();
		if (uploadServerResponse != null && !uploadServerResponse.isEmpty()) {
			//uploadServerResponse = uploadServerResponse.replaceAll("\n", "<br>");
			if (uploadServerResponse.contains("#")) { // # is used in response only
												// when there are errors
				String responseStrings[] = uploadServerResponse.split("#");
				for(int i=0; i<responseStrings.length; i++)
					responseStrings[i] = responseStrings[i].trim();
				
				String directoryNotAllowedInZip[] = responseStrings[1].isEmpty() ? new String[] { } : responseStrings[1].split("\\|");
				String writeFailedFiles[] = responseStrings[2].isEmpty() ? new String[] { } : responseStrings[2].split("\\|");
				String existingFiles[] = responseStrings[3].isEmpty() ? new String[] { } : responseStrings[3].split("\\|");
				String invalidFormatFiles[] = responseStrings[4].isEmpty() ? new String[] { } : responseStrings[4].split("\n");
				String invalidEncodingFiles[] = responseStrings[5].isEmpty() ? new String[] { } : responseStrings[5].split("\\|");
				
				uploadServerResponse = responseStrings[0] + "\n";
				
				int reportedUploadFailedFiles = 0;
				
				if (invalidEncodingFiles.length > 0) {
					uploadServerResponse += "\nFollowing files have an invalid encoding. You can only upload UTF-8 encoded files.\n";
					
					for (int i = 0; /*reportedUploadFailedFiles < MAX_FILES_TO_REPORT_FAILED &&*/ i < invalidEncodingFiles.length; i++) {
						uploadServerResponse += invalidEncodingFiles[i] + "\n";
						reportedUploadFailedFiles++;
					}
				}
				
				if (invalidFormatFiles.length > 0) {
					uploadServerResponse += "\nFollowing files have format errors\n";
					
					for (int i = 0; /*reportedUploadFailedFiles < MAX_FILES_TO_REPORT_FAILED &&*/ i < invalidFormatFiles.length; i++) {
						uploadServerResponse += invalidFormatFiles[i] + "\n";
						reportedUploadFailedFiles++;
					}
				}
				
				if(existingFiles.length > 0) {
					uploadServerResponse += "\nFollowing files already exist in the folder\n";
					
					for (int i = 0; /*reportedUploadFailedFiles < MAX_FILES_TO_REPORT_FAILED &&*/ i < existingFiles.length; i++) {
						uploadServerResponse += existingFiles[i] + "\n";
						reportedUploadFailedFiles++;
					}
				}	
				
				if (writeFailedFiles.length > 0) {
					uploadServerResponse += "\nFollowing files could not be written\n";
					
					for (int i = 0; /*reportedUploadFailedFiles < MAX_FILES_TO_REPORT_FAILED &&*/ i < writeFailedFiles.length; i++) {
						uploadServerResponse += writeFailedFiles[i] + "\n";
						reportedUploadFailedFiles++;
					}
				}
				
				if (directoryNotAllowedInZip.length > 0) {
					uploadServerResponse += "\nFollowing directories are not allowed in the zip file\n";
					
					for (int i = 0; /*reportedUploadFailedFiles < MAX_FILES_TO_REPORT_FAILED &&*/ i < directoryNotAllowedInZip.length; i++) {
						uploadServerResponse += directoryNotAllowedInZip[i] + "\n";
						reportedUploadFailedFiles++;
					}
				}
				
				//if(writeFailedFiles.length + existingFiles.length + invalidFormatFiles.length + invalidEncodingFiles.length > MAX_FILES_TO_REPORT_FAILED)
				//	uploadServerResponse += "and so on.\n";
				
				//remove all failed upload files
				for (int i = 0; i < directoryNotAllowedInZip.length; i++) {
					uploadedFiles.remove(directoryNotAllowedInZip[i]);
				}
				for (int i = 0; i < writeFailedFiles.length; i++) {
					uploadedFiles.remove(writeFailedFiles[i]);
				}
				for (int i = 0; i < existingFiles.length; i++) {
					uploadedFiles.remove(existingFiles[i]);
				}
				for (int i = 0; i < invalidFormatFiles.length; i++) {
					uploadedFiles.remove(invalidFormatFiles[i]);
				}
				for (int i = 0; i < invalidEncodingFiles.length; i++) {
					uploadedFiles.remove(invalidEncodingFiles[i]);
				}
			}

		}
		return uploadServerResponse;
	}
	
	public void keyValidateUploadedFiles(final String targetUploadDirectory) {
		fileService.validateKeys(Authentication.getInstance().getToken(), targetUploadDirectory, uploadedFiles, new AsyncCallback<HashMap<String,String>>() {

			@Override
			public void onFailure(Throwable caught) {
				
				String errorMessage = "";
				if(uploadServerResponse != null){
					errorMessage += uploadServerResponse;
				}
				keyValidationServerResponse = "Key Validation Failed.";
				if(taxonNameValidationServerResponse != null){
					errorMessage += taxonNameValidationServerResponse;
				}
				errorMessage += keyValidationServerResponse;
				Alerter.inputError(errorMessage);
			}

			@Override
			public void onSuccess(final HashMap<String, String> result) {
				if(!result.isEmpty()){
					String infoMessage = "The following files have key errors and will not be parsed.<br><br>";
					String errorMessage = "";
					int allowedErrorCounts = 2;
					for(String filename: result.keySet()){
						if (allowedErrorCounts <= 0 ){
							errorMessage += "and so on.<br>";
							break;
						}
						String errorsInFile = result.get(filename);
						errorMessage += errorsInFile.replace("\n", "<br>")+"<br>";
						allowedErrorCounts--;
					}
					if(taxonNameValidationServerResponse != null){
						errorMessage = taxonNameValidationServerResponse + errorMessage;
					}
					MessageBox box = Alerter.showKeyValidationResult(infoMessage, errorMessage);
					/*box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
					
						@Override
						public void onSelect(SelectEvent event) {
							if(uploadServerResponse != null){
								Alerter.fileManagerMessage(uploadServerResponse);
							}
						}
					}); */
					box.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
						
						@Override
						public void onSelect(SelectEvent event) {
							// TODO Auto-generated method stub
							fileService.deleteUploadedFiles(Authentication.getInstance().getToken(), targetUploadDirectory, uploadedFiles, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Alerter.inputError("Could not delete files.");
								}
								@Override
								public void onSuccess(Void result) {
								}
							});
						}
					});
					box.show();
				}else{
					String showMessage = "";
					if((uploadServerResponse == null || uploadServerResponse.isEmpty()) && (taxonNameValidationServerResponse == null || taxonNameValidationServerResponse.isEmpty())){
						Alerter.fileManagerMessage("File(s) uploaded successfully.");
					}else{
						if(uploadServerResponse != null){
							showMessage += uploadServerResponse;
						}
						if(taxonNameValidationServerResponse != null){
							showMessage += taxonNameValidationServerResponse;
						}
						Alerter.failedToUpload(showMessage);
					}
				}
			}
		});
	}
	
	public void validateTaxonNames(final String targetUploadDirectory){
		taxonNameValidationServerResponse = null;
		fileService.validateTaxonNames(Authentication.getInstance().getToken(), targetUploadDirectory, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				keyValidateUploadedFiles(targetUploadDirectory);
			}

			@Override
			public void onSuccess(String result) {
				if(!result.equals("success")){
					taxonNameValidationServerResponse = result;
				}else{
					taxonNameValidationServerResponse = "";
				}
				keyValidateUploadedFiles(targetUploadDirectory);
			}
		});
	}
	
	public void setServletPathOfUploader(IUploader uploader, String fileType, String targetUploadDirectory){
		String servletPath = uploader.getServletPath() + "?fileType=" + fileType + "&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId()))
				+ "&sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId());
		uploader.setServletPath(servletPath);
		
		List<String> fileNames = new LinkedList<String>();
		fileNames.add("Uploading, please wait...");
		uploader.getStatusWidget().setFileNames(fileNames);
		uploader.setServletPath(uploader.getServletPath() + "&target=" + URL.encodeQueryString(targetUploadDirectory));
	}

	public void validateTaxonDescriptionFiles(String uploadDirectory) {
		validateTaxonNames(uploadDirectory);
	}

}
