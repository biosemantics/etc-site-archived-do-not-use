package edu.arizona.sirls.etc.site.client.api.file;

public interface IGetFileContentAsyncCallbackListener {

	public void notifyResult(String result);

	public void notifyException(Throwable caught);
	
}
