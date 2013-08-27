package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.input;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;

import edu.arizona.sirls.etc.site.client.api.fileFormat.IValidFileAsyncCallbackListener;
import edu.arizona.sirls.etc.site.client.widget.FileTreeComposite;

public abstract class FileSelectClickHandler implements ClickHandler, IValidFileAsyncCallbackListener {

	protected FileTreeComposite tree;
	protected DialogBox dialogBox;
	protected HasText errorText;
	
	public void setDialogBox(DialogBox dialogBox) {
		this.dialogBox = dialogBox;
	}
	
	public void setTree(FileTreeComposite tree) {
		this.tree = tree;
	}
	
	public void setErrorText(HasText errorText) {
		this.errorText = errorText;
	}
	
	public abstract void onClick(ClickEvent event);

	private Set<IFileSelectClickHandlerListener> listeners = new HashSet<IFileSelectClickHandlerListener>();
	
	
	public void addListener(IFileSelectClickHandlerListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(IFileSelectClickHandlerListener listener) {
		this.listeners.remove(listener);
	}
	
	public void notifyListeners() {
		for(IFileSelectClickHandlerListener listener : listeners) 
			listener.notifyFileSelect(this, getSelectedTarget());
	}
	
	public String getSelectedTarget() {
		return tree.getFileSelectionHandler().getTarget();
	}
	
	@Override
	public void notifyResult(Boolean result) {
		if(!result) {
			if(errorText != null)
				this.errorText.setText("Invalid format for a taxon description");
		} else {
			if(dialogBox != null)
				dialogBox.hide();
			this.notifyListeners();
		}
	}

	@Override
	public void notifyException(Throwable caught) {
		caught.printStackTrace();
	}
}
