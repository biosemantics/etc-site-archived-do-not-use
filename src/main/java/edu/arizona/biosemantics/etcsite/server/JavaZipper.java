package edu.arizona.biosemantics.etcsite.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class JavaZipper implements IZipper {

	public static void main(String[] args) throws Exception {
		JavaZipper javaZipper = new JavaZipper();
		//javaZipper.unzip("C:\\nexus\\test.zip", "C:\\nexus\\test");
		javaZipper.zip("C:\\nexus\\test", "C:\\nexus\\test.zip");
	}

	public void unzip(String zipFile, String outputFolder) throws IOException {
		File folder = new File(outputFolder);
		if (!folder.exists()) {
			folder.mkdir();
		}
		
		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		while(zipEntry != null) {
			String fileName = zipEntry.getName();
			File newFile = new File(outputFolder + File.separator + fileName);
			
			//java.util.zip needs a "/" at the end to mark a directory
			if(fileName.endsWith("/"))
				newFile.mkdirs(); 
			else {
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				byte[] buffer = new byte[1024];
				while ((len = zipInputStream.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
			}
				
			zipEntry = zipInputStream.getNextEntry();
		}
		
		zipInputStream.closeEntry();
		zipInputStream.close();
	}

	@Override
	public String zip(String source, String destination) throws Exception {
		List<String> filesToZip = createFilesToZip(source, new File(source));
		
		FileOutputStream fos = new FileOutputStream(destination);
		ZipOutputStream zos = new ZipOutputStream(fos);

		for (String fileToZip : filesToZip) {
			ZipEntry ze = new ZipEntry(fileToZip);
			zos.putNextEntry(ze);

			File file = new File(source + File.separator + fileToZip);
			if(!file.isDirectory()) {
				FileInputStream in = new FileInputStream(file);
	
				int len;
				byte[] buffer = new byte[1024];
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
	
				in.close();
			}
		}

		zos.closeEntry();
		zos.close();
		return destination;
	}

	private List<String> createFilesToZip(String source, File file) {
		List<String> files = new LinkedList<String>();
		
		if(!file.equals(new File(source)))
			files.add(generateZipFormatEntry(source, file));
		if(file.isDirectory()) {
			String[] children = file.list();
			for(String child : children){
				files.addAll(createFilesToZip(source, new File(file, child)));
			}
		}
		return files;
	}
	
    private String generateZipFormatEntry(String source, File file){
    	String path = file.getAbsolutePath();
    	String result = path.substring(source.length() + 1, path.length());;
    	if(file.isDirectory()) {
    		//java.util.zip needs a "/" at the end to mark a directory
    		result += "/";
    	}
    	return result;
    }

}
