package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;

public class FileDragDropHandler implements DragStartHandler, DropHandler, DragOverHandler {

	private final IFileServiceAsync fileService = GWT.create(IFileService.class);
	private Set<IFileMoveListener> listeners = new HashSet<IFileMoveListener>();

	@Override
	public void onDragStart(DragStartEvent event) {		
		Object source = event.getSource();
		if(source instanceof FileImageLabelComposite) {
			// Required: set data for the event.
			FileImageLabelComposite fileImageLabelComposite = (FileImageLabelComposite)source;
			event.setData("sourcePath", fileImageLabelComposite.getPath());
		}
	}

	@Override
	public void onDrop(DropEvent event) {
		event.preventDefault();
		String sourcePath = event.getData("sourcePath");
		Object target = event.getSource();
		if(target instanceof FileImageLabelComposite) {
			// Required: set data for the event.
			FileImageLabelComposite fileImageLabelComposite = (FileImageLabelComposite)target;
			
			String sourcePathParts[] = sourcePath.split("//");
			String sourceName = sourcePathParts[sourcePathParts.length-1];
			String targetPath = fileImageLabelComposite.getPath() + "//" + sourceName;
			if(fileImageLabelComposite.isFile())
				 targetPath = getParentDirectory(fileImageLabelComposite.getPath()) + "//" + sourceName;
			fileService.moveFile(Authentication.getInstance().getAuthenticationToken(), sourcePath, targetPath, 
					new AsyncCallback<Boolean>() {
				public void onSuccess(Boolean result) {
					if(result) {
						notifyListeners();
					}	
				}
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
		}
	}

	private String getParentDirectory(String path) {
		return path.substring(0, path.lastIndexOf("//"));
	}

	@Override
	public void onDragOver(DragOverEvent event) {
		//this is required for drop to work, see 
		// http://static.googleusercontent.com/external_content/untrusted_dlcp/www.google.com/it//events/io/2011/static/presofiles/gwt_html5_a_web_develops_dream.pdf slide 62
	}
	
	public void addListener(IFileMoveListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(IFileMoveListener listener) { 
		this.listeners.remove(listener);
	}
	
	public void notifyListeners() { 
		for(IFileMoveListener listener : listeners)
			listener.notifyFileMove();
	}	
}
