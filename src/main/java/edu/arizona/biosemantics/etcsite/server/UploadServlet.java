package edu.arizona.biosemantics.etcsite.server;

import edu.arizona.biosemantics.etcsite.server.rpc.AuthenticationService;
import edu.arizona.biosemantics.etcsite.shared.file.IContentValidator;
import edu.arizona.biosemantics.etcsite.shared.file.XMLValidator;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;
import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import gwtupload.shared.UConsts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

/**
 * This is an example of how to use UploadAction class.
 * 
 * This servlet saves all received files in a temporary folder, and deletes them
 * when the user sends a remove request.
 * 
 * @author Manolo Carrasco Mo�ino
 * 
 */
public class UploadServlet extends UploadAction {

	private static final long serialVersionUID = 1L;

	Hashtable<String, String> receivedContentTypes = new Hashtable<String, String>();
	/**
	 * Maintain a list with received files and their content types.
	 */
	Hashtable<String, File> receivedFiles = new Hashtable<String, File>();
	private Set<IContentValidator> contentValidators;
	//IAuthenticationServiceAsync authenticationService = GWT.create(IAuthenticationService.class);

	public UploadServlet() {
		contentValidators = new HashSet<IContentValidator>();
		//contentValidators.add(new CSVValidator());
		contentValidators.add(new XMLValidator(new File(Configuration.taxonDescriptionSchemaFile)));
		contentValidators.add(new XMLValidator(new File(Configuration.markedUpTaxonDescriptionSchemaFile)));
	}
	
	/**
	 * Override executeAction to save the received files in a custom place and
	 * delete this items from session.
	 */
	@Override
	public String executeAction(HttpServletRequest request,
			List<FileItem> sessionFiles) throws UploadActionException {
		String response = "";
		
		String username = request.getParameter("username");
		String sessionID = request.getParameter("sessionID");
		String target = request.getParameter("target");
		
		IAuthenticationService authenticationService = new AuthenticationService();
		RPCResult<AuthenticationResult> authenticationResult = 
				authenticationService.isValidSession(new AuthenticationToken(username, sessionID));
		
		int numberNotAdded = 0;
		if(authenticationResult.isSucceeded() && authenticationResult.getData().getResult()) {
			int cont = 0;
			for (FileItem item : sessionFiles) {
				if (false == item.isFormField()) {
					cont++;
					try {
						// / Create a new file based on the remote file name in the
						// client
						// String saveName =
						// item.getName().replaceAll("[\\\\/><\\|\\s\"'{}()\\[\\]]+",
						// "_");
						// File file =new File("/tmp/" + saveName);
	
						// / Create a temporary file placed in /tmp (only works in
						// unix)
						// File file = File.createTempFile("upload-", ".bin", new
						// File("/tmp"));
	
						// / Create a temporary file placed in the default system
						// temp folder
						File file = new File(target + File.separator + item.getName());
						if(!file.exists()) {
							String fileContent = item.getString("UTF-8");
							boolean valid = false;
							for(IContentValidator validator : contentValidators)
								valid |= validator.validate(fileContent);
							
							if(valid) {
								file.createNewFile();
								item.write(file);
			
								// / Save a list with the received files
								receivedFiles.put(item.getFieldName(), file);
								receivedContentTypes.put(item.getFieldName(),
										item.getContentType());
			
								// / Send a customized message to the client.
								//response += "File saved as " + file.getAbsolutePath();
							} else {
								numberNotAdded++;
								//error message would when too long (because many files are not valid) freeze the web page
								//response += "File " + item.getName() + " was not added. Invalid file format.\n";
							}
						} else {
							numberNotAdded++;
							//response += "File " + item.getName() + " was not added. File with same name exists in directory.\n";
						}
					} catch (Exception e) {
						throw new UploadActionException(e);
					}
				}
			}
		}
		
		if(numberNotAdded == 1)
			response += numberNotAdded + " file was not added due to invalid file format and or name collisions";
		if(numberNotAdded > 1)
			response += numberNotAdded + " files were not added due to invalid file format and or name collisions";

		// / Remove files from session because we have a copy of them
		removeSessionFileItems(request);

		// / Send your customized message to the client. <- this is where i specify which files were not valid
		return response;
	}

	/**
	 * Get the content of an uploaded file.
	 */
	@Override
	public void getUploadedFile(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String fieldName = request.getParameter(UConsts.PARAM_SHOW);
		File f = receivedFiles.get(fieldName);
		if (f != null) {
			response.setContentType(receivedContentTypes.get(fieldName));
			FileInputStream is = new FileInputStream(f);
			copyFromInputStreamToOutputStream(is, response.getOutputStream());
		} else {
			renderXmlResponse(request, response, XML_ERROR_ITEM_NOT_FOUND);
		}
	}

	/**
	 * Remove a file when the user sends a delete request.
	 */
	@Override
	public void removeItem(HttpServletRequest request, String fieldName)
			throws UploadActionException {
		File file = receivedFiles.get(fieldName);
		receivedFiles.remove(fieldName);
		receivedContentTypes.remove(fieldName);
		if (file != null) {
			file.delete();
		}
	}
}