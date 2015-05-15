package edu.arizona.biosemantics.etcsite.client.common.files;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import gwtupload.client.IUploader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class FileUploadHandler {
	
	List<String> uploadedFiles;
	String serverResponse;
	ManagableFileTreePresenter presenter;

	public FileUploadHandler() {
		uploadedFiles = null;
		serverResponse = null;
		presenter = null;
	}
	
	public FileUploadHandler(ManagableFileTreePresenter presenter) {
		uploadedFiles = null;
		serverResponse = null;
		this.presenter = presenter;
	}
	
	@SuppressWarnings("deprecation")
	public String parseServerResponse(IUploader uploader){
		serverResponse = uploader.getServerInfo().message;
		uploadedFiles = uploader.getFileInput().getFilenames();
		if(serverResponse != null && !serverResponse.isEmpty()) {
			serverResponse = serverResponse.replaceAll("\n", "<br>");
			if(serverResponse.contains("#")){ //# is used in response only when there are errors
				String responseStrings[] = serverResponse.split("#");
				responseStrings[1] = responseStrings[1].trim();
				String xmlErrorFiles[] = responseStrings[1].split("\\|");
				responseStrings[2] = responseStrings[2].trim();
				String existingFiles[] = responseStrings[2].split("\\|");
				serverResponse = responseStrings[0]+"<br>";
				if(xmlErrorFiles.length>0 && !responseStrings[1].isEmpty()){
					serverResponse += "Following files have xml format errors<br>";
				}
				int i;
				for(i=0;i<20 && i<xmlErrorFiles.length;i++){
					serverResponse += xmlErrorFiles[i] + "<br>";
				}
				int j=0;
				if(i<20){
					if(existingFiles.length>0 && !responseStrings[2].isEmpty()){
						serverResponse += "<br>Following files already exist in the folder<br>";
					}
					for(;i<20 && j<existingFiles.length; i++, j++){
						serverResponse += existingFiles[j] + "<br>";
					}
				}
				if(j<existingFiles.length-1 | i<xmlErrorFiles.length-1){
					serverResponse += "and so on.<br>";
				}
				for(i=0; i<xmlErrorFiles.length; i++){
					uploadedFiles.remove(xmlErrorFiles[i]);
				}
				for(i=0;i<existingFiles.length;i++){
					uploadedFiles.remove(existingFiles[i]);
				}
			}
			
		}
		return serverResponse;
	}
	
	public void keyValidateUploadedFiles(final IFileServiceAsync fileService, final String targetUploadDirectory){
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
					box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
					
						@Override
						public void onSelect(SelectEvent event) {
							if(serverResponse != null){
								Alerter.fileManagerMessage(serverResponse);
							}
						}
					});
					box.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
						
						@Override
						public void onSelect(SelectEvent event) {
							// TODO Auto-generated method stub
							fileService.deleteUploadedFiles(Authentication.getInstance().getToken(), targetUploadDirectory, uploadedFiles, new AsyncCallback<Void>() {

								@Override
								public void onFailure(
											Throwable caught) {
									// TODO Auto-generated method stub
									Alerter.inputError("Could not delete files.");
								}

								@Override
								public void onSuccess(Void result) {
										// TODO Auto-generated method stub
									if(presenter!=null){
										presenter.refresh(FileFilter.ALL);
									}
								}
							});
						}
					});
					box.show();
				}else{
					Alerter.fileManagerMessage("File(s) uploaded successfully.");
				}
			}
		});

	}
	
	public void setServletPathOfUploader(IUploader uploader, IUploader viewUploader,String fileType, String targetUploadDirectory){
		String servletPath = viewUploader.getServletPath() + "?fileType=" + fileType + "&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId()))
				+ "&sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId());
		uploader.setServletPath(servletPath);
		
		List<String> fileNames = new LinkedList<String>();
		fileNames.add("Uploading, please wait...");
		uploader.getStatusWidget().setFileNames(fileNames);
		uploader.setServletPath(uploader.getServletPath() + "&target=" + URL.encodeQueryString(targetUploadDirectory));
	}

}
