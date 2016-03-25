package edu.arizona.biosemantics.etcsite.server.upload;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;

import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;

public abstract class Uploader {

	public static class UploadResult {

		private boolean writeFailed = false;
		private boolean fileExisted = false;
		private boolean invalidFormat = false;
		private String formatErrorMessage = "";
		private boolean invalidEncoding = false;
		private boolean directoryNotAllowedInFile = false;
		
		private FileItem fileItem;
		private File file;
		private String relativeFileName; //relative to the target upload directory
		private List<UploadResult> childResults = new LinkedList<UploadResult>();
		
		public UploadResult(FileItem fileItem,	File file, String relativeFileName) {		
			super();
			this.fileItem = fileItem;
			this.file = file;
			this.relativeFileName = relativeFileName;
		}
		
		public UploadResult(boolean writeFailed,
				boolean fileExisted, boolean invalidFormat,
				boolean invalidEncoding, 
				boolean directoryNotAllowedInFile,
				FileItem fileItem,
				File file, String relativeFileName) {
			super();
			this.writeFailed = writeFailed;
			this.fileExisted = fileExisted;
			this.invalidFormat = invalidFormat;
			this.invalidEncoding = invalidEncoding;
			this.directoryNotAllowedInFile = directoryNotAllowedInFile;
			this.fileItem = fileItem;
			this.file = file;
			this.relativeFileName = relativeFileName;
		}
		public boolean isWriteFailed() {
			return writeFailed;
		}
		public void setWriteFailed(boolean writeFailed) {
			this.writeFailed = writeFailed;
		}
		public boolean isFileExisted() {
			return fileExisted;
		}
		public void setFileExisted(boolean fileExisted) {
			this.fileExisted = fileExisted;
		}
		public boolean isInvalidFormat() {
			return invalidFormat;
		}
		public void setInvalidFormat(boolean invalidFormat, String formatErrorMessage) {
			this.invalidFormat = invalidFormat;
			this.formatErrorMessage = formatErrorMessage;
		}
		public boolean isSuccess() {
			return !writeFailed && !fileExisted && !invalidFormat && !invalidEncoding && !directoryNotAllowedInFile;
		}
		public FileItem getFileItem() {
			return fileItem;
		}
		public void setFileItem(FileItem fileItem) {
			this.fileItem = fileItem;
		}
		public File getFile() {
			return file;
		}
		public void setFile(File file) {
			this.file = file;
		}
		public boolean isInvalidEncoding() {
			return invalidEncoding;
		}
		public void setInvalidEncoding(boolean invalidEncoding) {
			this.invalidEncoding = invalidEncoding;
		}
		public List<UploadResult> getChildResults() {
			return childResults;
		}
		public void setChildResults(List<UploadResult> childResults) {
			this.childResults = childResults;
		}
		public String getRelativeFileName() {
			return relativeFileName;
		}
		public void setRelativeFileName(String relativeFileName) {
			this.relativeFileName = relativeFileName;
		}
		public String getFormatErrorMessage() {
			return formatErrorMessage;
		}
		public void setFormatErrorMessage(String formatErrorMessage) {
			this.formatErrorMessage = formatErrorMessage;
		}
		public void setDirectoryNotAllowedInZip(boolean directoryNotAllowedInFile) {
			this.directoryNotAllowedInFile = directoryNotAllowedInFile;
		}
		public boolean isDirectoryNotAllowedInFile() {
			return directoryNotAllowedInFile;
		}
	}
	
	public List<UploadResult> upload(ShortUser shortUser, List<FileItem> items, String targetPath, FileTypeEnum fileType) {
		File targetFile = new File(targetPath);
		if(targetFile.isFile())
			targetPath = targetFile.getParent();
		List<UploadResult> result = new LinkedList<UploadResult>();		
		for (FileItem item : items) {
			if (!item.isFormField()) {	
				if(FilenameUtils.getExtension(item.getName()).equals("zip")) {
					result.add(uploadZip(shortUser, item, targetPath, fileType));
				} else {
					result.add(uploadSingleFile(shortUser, item, targetPath, fileType));
				}
			}
		}
		return result;
	}
	
	protected abstract UploadResult uploadSingleFile(ShortUser shortUser, FileItem item, String targetPath, FileTypeEnum fileType);

	protected abstract UploadResult uploadZip(ShortUser shortUser, FileItem item, String targetPath,	FileTypeEnum fileType);

	protected boolean isValidUTF8(byte[] bytes) {
		/*Charset charset = null;
		try {
			charset = new AutoDetectReader(item.getInputStream()).getCharset();
		} catch(TikaException | IOException e) {
			String message = "Couldn't detect encoding. Will allow.";
			//log(message, e);
			log(LogLevel.DEBUG, message, e);
		}*/
		
		CharsetDecoder utf8Decoder = Charset.forName("UTF8").newDecoder().onMalformedInput(CodingErrorAction.REPORT);
		try {
			utf8Decoder.decode(ByteBuffer.wrap(bytes));
			return true;
		} catch (CharacterCodingException e) {
			return false;
		}
	}
	
	protected String getRelativeFileName(File file, File ancestor) {
		return file.getAbsolutePath().substring(ancestor.getAbsolutePath().length(), file.getAbsolutePath().length());
	}
}
