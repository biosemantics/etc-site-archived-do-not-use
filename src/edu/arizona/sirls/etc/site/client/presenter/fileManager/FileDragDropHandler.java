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
import edu.arizona.sirls.etc.site.client.presenter.MessagePresenter;
import edu.arizona.sirls.etc.site.client.view.MessageView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;

public class FileDragDropHandler implements DragStartHandler, DropHandler, DragOverHandler {

	private final IFileServiceAsync fileService = GWT.create(IFileService.class);
	private Set<IFileMoveListener> listeners = new HashSet<IFileMoveListener>();

	private MessageView messageView = new MessageView();
	private MessagePresenter messagePresenter = new MessagePresenter(messageView, "File Move");
	
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
		final String sourcePath = event.getData("sourcePath");
		Object target = event.getSource();
		if(target instanceof FileImageLabelComposite) {
			// Required: set data for the event.
			final FileImageLabelComposite fileImageLabelComposite = (FileImageLabelComposite)target;
			String targetPath = fileImageLabelComposite.getPath();
			
			if(!targetPath.contains(sourcePath)) {
				fileService.isDirectory(Authentication.getInstance().getAuthenticationToken(), sourcePath, new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					@Override
					public void onSuccess(Boolean isDirectory) {
						String sourcePathParts[] = sourcePath.split("//");
						String sourceName = sourcePathParts[sourcePathParts.length-1];
						String targetAndAddonPath = fileImageLabelComposite.getPath() + "//" + sourceName;
						if(fileImageLabelComposite.isFile())
							targetAndAddonPath = getParentDirectory(fileImageLabelComposite.getPath()) + "//" + sourceName;
						final int targetLevel = getLevel(targetAndAddonPath);
						final String targetAndAddonPathFinal = targetAndAddonPath;
						
						if(isDirectory) {
							fileService.getDepth(Authentication.getInstance().getAuthenticationToken(), sourcePath, new AsyncCallback<Integer>() {
								@Override
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
								}
								@Override
								public void onSuccess(Integer sourceDepth) {
									int overallDepth = targetLevel + (sourceDepth + 1);
									if(overallDepth > 2) {
										messagePresenter.setMessage("Only a directory depth of two is allowed.");
										messagePresenter.go();
										return;
									} else {
										moveFile(sourcePath, targetAndAddonPathFinal);
									}
								}
							});
						} else {
							moveFile(sourcePath, targetAndAddonPathFinal);
						}
					}
					private int getLevel(String target) {
						return target.split("//").length - 2;
					}
				});
			} else {
				messagePresenter.setMessage("Directory cannot be moved into its descendants.");
				messagePresenter.go();
			}
		}
	}
	
	protected void moveFile(String sourcePath, String targetPath) {
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