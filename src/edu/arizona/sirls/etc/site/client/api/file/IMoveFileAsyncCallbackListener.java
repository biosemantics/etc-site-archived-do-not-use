package edu.arizona.sirls.etc.site.client.api.file;

public interface IMoveFileAsyncCallbackListener {

	public void notifyException(Throwable caught);
	
	public void notifyResult(Boolean result);

}
