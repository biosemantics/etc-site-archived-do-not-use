package edu.arizona.sirls.etc.site.client.api.file;

import edu.arizona.sirls.etc.site.shared.rpc.Tree;

public interface IUserFilesAsyncCallbackListener {

	public void notifyResult(Tree<String> result, UserFilesAsyncCallback userFilesAsyncCallback);

	public void notifyException(Throwable caught, UserFilesAsyncCallback userFilesAsyncCallback);

}
