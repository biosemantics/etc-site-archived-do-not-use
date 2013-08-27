package edu.arizona.sirls.etc.site.client.api.fileFormat;

public interface IValidFileAsyncCallbackListener {

	public void notifyResult(Boolean result);

	public void notifyException(Throwable caught);

}
