package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison;

import java.io.File;

import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.HasTaskException;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CopyFilesFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.CreateDirectoryFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.TaxonomyComparisonException;

public class InputFileCreator {
	
	private IFileService fileService;
	private IFilePermissionService filePermissionService;

	@Inject
	public InputFileCreator(IFileService fileService, IFilePermissionService filePermissionService) {
		this.fileService = fileService;
		this.filePermissionService = filePermissionService;
	}
	
	public String createOwnedIfShared(AuthenticationToken token, String input) throws HasTaskException {
		boolean needsCopy = filePermissionService.isSharedFilePath(token.getUserId(), input) || 
				filePermissionService.isPublicFilePath(input);
		//Check here if public?? then also have to copy! also all other tasks
		String fileName = null;
		try {
			fileName = fileService.getFileName(token, input);
		} catch (PermissionDeniedException e) {
			log(LogLevel.ERROR, "Permission denied to read " + fileName);
			throw new HasTaskException();
		}
		if(needsCopy) {
			String destination;
			try {
				destination = fileService.createDirectory(token, Configuration.fileBase + File.separator + token.getUserId(), 
						fileName, true);
			} catch (PermissionDeniedException | CreateDirectoryFailedException e) {
				throw new HasTaskException();
			}
			try {
				fileService.copyDirectory(token, input, destination);
			} catch (CopyFilesFailedException | PermissionDeniedException e) {
				throw new HasTaskException();
			}
			return destination;
		}
		return input;
	}
	
	public String[] createOwnedIfShared(AuthenticationToken token, String[] inputs) throws HasTaskException {
		String[] result = new String[inputs.length];
		for(int i=0; i<inputs.length; i++) {
			String input = inputs[i];
			if(input != null && !input.isEmpty()) 
				result[i] = createOwnedIfShared(token, input);
			else
				result[i] = inputs[i];
		}
		return result;
	}
	
	public void setInUse(AuthenticationToken token, String input, Task task) throws HasTaskException {
		try {
			fileService.setInUse(token, true, input, task);
		} catch (PermissionDeniedException e) {
			log(LogLevel.ERROR, "can not set file in use for " + input + ".");
			throw new HasTaskException(task);
		}
	}

	public void setInUse(AuthenticationToken token, String[] inputs, Task task) throws HasTaskException {
		for(String input : inputs) {
			if(input != null && !input.isEmpty()) 
				setInUse(token, input, task);
		}
	}
	
}
