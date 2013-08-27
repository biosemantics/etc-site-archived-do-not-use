package edu.arizona.sirls.etc.site.client.api.file;

public interface ISetFileContentAsyncCallbackListener {

	public void notifyResult(boolean result);

	public void notifyException(Throwable caught);
	
}
