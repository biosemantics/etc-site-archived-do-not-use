package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;

public interface IFileContentView extends IsWidget {

	public interface Presenter {

		void onClose();
		void onSave();
		void onFormatChange(String format);

		void show(FileTreeItem fileTreeItem);
		void onEdit();
		
	}

	String getFormat();

	void setText(String result);

	void setPresenter(Presenter presenter);

	void setEditable(boolean enabled);

	String getText();
	
	
	
}
