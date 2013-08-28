package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.input;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;

import edu.arizona.sirls.etc.site.client.widget.FileTreeComposite;

public abstract class FileSelectClickHandler implements ClickHandler {

	protected FileTreeComposite tree;
	protected TitleCloseDialogBox dialogBox;
	protected HasText errorText;
	
	public void setDialogBox(TitleCloseDialogBox dialogBox) {
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
	
	
	protected AsyncCallback<Boolean> validFileCallback = new AsyncCallback<Boolean>() {
		public void onSuccess(Boolean result) {
			if(!result) {
				if(errorText != null)
					errorText.setText("Invalid format for a taxon description");
			} else {
				if(dialogBox != null)
					dialogBox.hide();
				notifyListeners();
			}
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}
	};
}
