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
	String serverResponse;
	ManagableFileTreePresenter presenter;
	private IFileServiceAsync fileService;

	public FileUploadHandler(IFileServiceAsync fileService) {
		uploadedFiles = null;
		serverResponse = null;
		presenter = null;
		this.fileService = fileService;
	}
	
	public FileUploadHandler(ManagableFileTreePresenter presenter, IFileServiceAsync fileService) {
		uploadedFiles = null;
		serverResponse = null;
		this.presenter = presenter;
		this.fileService = fileService;
	}
	
	public String parseServerResponse(IUploader uploader){
		serverResponse = uploader.getServerMessage().getMessage();
		uploadedFiles = uploader.getFileInput().getFilenames();
		if (serverResponse != null && !serverResponse.isEmpty()) {
			//serverResponse = serverResponse.replaceAll("\n", "<br>");
			if (serverResponse.contains("#")) { // # is used in response only
												// when there are errors
				String responseStrings[] = serverResponse.split("#");
				for(int i=0; i<responseStrings.length; i++)
					responseStrings[i] = responseStrings[i].trim();
				
				String writeFailedFiles[] = responseStrings[1].isEmpty() ? new String[] { } : responseStrings[1].split("\\|");
				String existingFiles[] = responseStrings[2].isEmpty() ? new String[] { } : responseStrings[2].split("\\|");
				String invalidFormatFiles[] = responseStrings[3].isEmpty() ? new String[] { } : responseStrings[3].split("\n");
				String invalidEncodingFiles[] = responseStrings[4].isEmpty() ? new String[] { } : responseStrings[4].split("\\|");
				
				serverResponse = responseStrings[0] + "\n";
				
				int reportedUploadFailedFiles = 0;
				
				if (invalidEncodingFiles.length > 0) {
					serverResponse += "\nFollowing files have an invalid encoding. You can only upload UTF-8 encoded files.\n";
					
					for (int i = 0; /*reportedUploadFailedFiles < MAX_FILES_TO_REPORT_FAILED &&*/ i < invalidEncodingFiles.length; i++) {
						serverResponse += invalidEncodingFiles[i] + "\n";
						reportedUploadFailedFiles++;
					}
				}
				
				if (invalidFormatFiles.length > 0) {
					serverResponse += "\nFollowing files have format errors\n";
					
					for (int i = 0; /*reportedUploadFailedFiles < MAX_FILES_TO_REPORT_FAILED &&*/ i < invalidFormatFiles.length; i++) {
						serverResponse += invalidFormatFiles[i] + "\n";
						reportedUploadFailedFiles++;
					}
				}
				
				if(existingFiles.length > 0) {
					serverResponse += "\nFollowing files already exist in the folder\n";
					
					for (int i = 0; /*reportedUploadFailedFiles < MAX_FILES_TO_REPORT_FAILED &&*/ i < existingFiles.length; i++) {
						serverResponse += existingFiles[i] + "\n";
						reportedUploadFailedFiles++;
					}
				}	
				
				if (writeFailedFiles.length > 0) {
					serverResponse += "\nFollowing files could not be written\n";
					
					for (int i = 0; /*reportedUploadFailedFiles < MAX_FILES_TO_REPORT_FAILED &&*/ i < writeFailedFiles.length; i++) {
						serverResponse += writeFailedFiles[i] + "\n";
						reportedUploadFailedFiles++;
					}
				}
				
				//if(writeFailedFiles.length + existingFiles.length + invalidFormatFiles.length + invalidEncodingFiles.length > MAX_FILES_TO_REPORT_FAILED)
				//	serverResponse += "and so on.\n";
				
				//remove all failed upload files
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
		return serverResponse;
	}
	
	public void keyValidateUploadedFiles(final String targetUploadDirectory) {
		fileService.validateKeys(Authentication.getInstance().getToken(), targetUploadDirectory, uploadedFiles, new AsyncCallback<HashMap<String,String>>() {

			@Override
			public void onFailure(Throwable caught) {
				Alerter.inputError("Key Validation Failed.");
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
					MessageBox box = Alerter.showKeyValidationResult(infoMessage, errorMessage);
					/*box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
					
						@Override
						public void onSelect(SelectEvent event) {
							if(serverResponse != null){
								Alerter.fileManagerMessage(serverResponse);
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
					if(serverResponse == null || serverResponse.isEmpty()){
						Alerter.fileManagerMessage("File(s) uploaded successfully.");
					}else{
						Alerter.fileManagerMessage(serverResponse);
					}
				}
			}
		});
	}
	
	public void validateTaxonNames(String targetUploadDirectory){
		fileService.validateTaxonNames(Authentication.getInstance().getToken(), targetUploadDirectory, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(String result) {
				if(!result.equals("success")){
					serverResponse = result;
				}else{
					serverResponse = "";
				}
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

}
