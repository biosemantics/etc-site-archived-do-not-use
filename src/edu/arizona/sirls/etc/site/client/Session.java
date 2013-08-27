package edu.arizona.sirls.etc.site.client;

import edu.arizona.sirls.etc.site.client.builder.PageBuilder;

public class Session {

	private static Session instance;

	public static Session getInstance() {
		if(instance == null)
			instance = new Session();
		return instance;
	}
	
	private Session() {
	}
	
	private PageBuilder pageBuilder;
		
	public void setPageBuilder(PageBuilder pageBuilder) {
		this.pageBuilder = pageBuilder;
	}
	
	public PageBuilder getPageBuilder() {
		return this.pageBuilder;
	}
}
