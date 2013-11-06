package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;

import edu.arizona.sirls.etc.site.client.view.fileManager.FileContentView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessServiceAsync;

public class FileImageLabelCompositeDoubleClickHandler implements DoubleClickHandler {

	private final IFileAccessServiceAsync fileAccessService = GWT.create(IFileAccessService.class);
	private String filePath;
	private FileContentPresenter fileContentPresenter;
	
	public FileImageLabelCompositeDoubleClickHandler(FileImageLabelComposite source) {
		this.filePath = source.getFileInfo().getFilePath();
	}
	
	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		if(fileContentPresenter == null) 
			this.fileContentPresenter = new FileContentPresenter(
				new FileContentView(), fileAccessService, filePath);
		fileContentPresenter.go();
	}
}
