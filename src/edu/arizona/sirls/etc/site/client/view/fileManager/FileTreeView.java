package edu.arizona.sirls.etc.site.client.view.fileManager;

import com.google.gwt.user.client.ui.Widget;

public interface FileTreeView {
	
	public interface Presenter {
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	FileImageLabelTree getTree();
}
