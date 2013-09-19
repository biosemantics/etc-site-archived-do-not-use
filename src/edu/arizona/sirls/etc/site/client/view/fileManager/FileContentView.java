package edu.arizona.sirls.etc.site.client.view.fileManager;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileContentPresenter;

public class FileContentView extends Composite implements FileContentPresenter.Display {

	private ListBox formatListBox;
	private TextArea textArea;
	private Button closeButton;

	public FileContentView() {
		VerticalPanel verticalPanel = new VerticalPanel();
	    HorizontalPanel formatPanel = new HorizontalPanel();
	    formatPanel.add(new Label("Format for:"));
		this.formatListBox = new ListBox();
	    formatListBox.setVisibleItemCount(1);
	    formatPanel.add(formatListBox);
	    verticalPanel.add(formatPanel);
	    this.textArea = new TextArea();
	    textArea.addStyleName("fileContent");
		verticalPanel.add(textArea);
		this.closeButton = new Button("Close");
		verticalPanel.add(closeButton);
		this.initWidget(verticalPanel);
	}

	@Override
	public ListBox getFormatListBox() {
		return formatListBox;
	}

	@Override
	public TextArea getTextArea() {
		return textArea;
	}

	@Override
	public FocusWidget getCloseButton() {
		return closeButton;
	}
	
}
