package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.user.client.ui.IsWidget;

public interface IFileContentView extends IsWidget {

	public interface Presenter {

		void onClose();

		void onFormatChange(String format);

		void show(String filePath);
		
	}

	String getFormat();

	void setText(String result);

	void setPresenter(Presenter presenter);
	
	
	
}
