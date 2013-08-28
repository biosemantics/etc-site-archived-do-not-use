package edu.arizona.sirls.etc.site.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;

import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.FileContentDialogBox;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessServiceAsync;

public class FileImageLabelCompositeDoubleClickHandler implements DoubleClickHandler {

	private final IFileAccessServiceAsync fileAccessService = GWT.create(IFileAccessService.class);
	private String target;
	
	public FileImageLabelCompositeDoubleClickHandler(FileImageLabelComposite source) {
		this.target = source.getPath();
	}
	
	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		FileContentDialogBox dialog = new FileContentDialogBox("File Content", target);
		dialog.setGlassEnabled(true);
		dialog.center();
		dialog.show();
	}
}
