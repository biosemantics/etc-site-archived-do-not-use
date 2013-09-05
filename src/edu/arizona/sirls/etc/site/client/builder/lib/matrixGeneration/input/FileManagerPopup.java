package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.input;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.widget.FileTreeAndMenuComposite;
import edu.arizona.sirls.etc.site.client.widget.ILoadListener;
import edu.arizona.sirls.etc.site.shared.rpc.FileFilter;

public class FileManagerPopup extends TitleCloseDialogBox implements ILoadListener {

	private FileTreeAndMenuComposite fileTreeAndMenuComposite;
	
    public FileManagerPopup() { 
        // The popup's constructor's argument is a boolean specifying that it 
        // auto-close itself when the user clicks outside of it. 
        super(true, "File Manager"); 
        
        fileTreeAndMenuComposite = new FileTreeAndMenuComposite(true, FileFilter.ALL);
        fileTreeAndMenuComposite.addLoadListener(this);
    } 	

	@Override
	public void notifyLoadFinished(Widget widget) {
		fileTreeAndMenuComposite.addStyleName("fileSelect");
        this.add(fileTreeAndMenuComposite);
        
        notifyLoadListeners();
	}
	
	private Set<ILoadListener> listeners = new HashSet<ILoadListener>();
	
	public void addLoadListener(ILoadListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeLoadListener(ILoadListener listener) { 
		this.listeners.remove(listener);
	}
	
	public void notifyLoadListeners() { 
		for(ILoadListener listener : listeners)
			listener.notifyLoadFinished(this);
	}
	
}
