package edu.arizona.biosemantics.etcsite.server;

import java.io.IOException;

public interface IZipper {

	public String zip(String source, String destination) throws Exception;
	
	public void unzip(String source, String destination) throws Exception;
	
}
