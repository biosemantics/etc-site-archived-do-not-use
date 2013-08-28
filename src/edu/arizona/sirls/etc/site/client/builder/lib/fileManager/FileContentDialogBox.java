package edu.arizona.sirls.etc.site.client.builder.lib.fileManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.builder.dialog.CloseDialogBoxClickHandler;
import edu.arizona.sirls.etc.site.shared.rpc.FileType;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessServiceAsync;

public class FileContentDialogBox extends TitleCloseDialogBox {

	private HTML html;
	private final IFileAccessServiceAsync fileAccessService = GWT.create(IFileAccessService.class);

	public FileContentDialogBox(String title, final String target) {
		super(title);
		fileAccessService.getFileContent(Authentication.getInstance().getAuthenticationToken(), target, fileContentCallback);
		
		this.html = new HTML();
		VerticalPanel verticalPanel = new VerticalPanel();
	    HorizontalPanel formatPanel = new HorizontalPanel();
	    formatPanel.add(new Label("Format for:"));
		final ListBox formatListBox = new ListBox();
	    for(FileType fileType : FileType.values())
		    formatListBox.addItem(fileType.toString());
	    formatListBox.setVisibleItemCount(1);
	    formatListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				FileType fileType = FileType.valueOf(formatListBox.getValue(formatListBox.getSelectedIndex()));
				fileAccessService.getFileContent(Authentication.getInstance().getAuthenticationToken(), target, fileType, fileContentCallback);
			}
	    });
	    formatPanel.add(formatListBox);
	    verticalPanel.add(formatPanel);
		ScrollPanel scrollPanel = new ScrollPanel(html);
		scrollPanel.addStyleName("fileContent");
		verticalPanel.add(scrollPanel);
		Button closeButton = new Button("Close");
		closeButton.addClickHandler(new CloseDialogBoxClickHandler(this));
		verticalPanel.add(closeButton);
		
		this.add(verticalPanel);
	}

	protected AsyncCallback<String> fileContentCallback = new AsyncCallback<String>() {
		public void onSuccess(String result) {
			SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();
			htmlBuilder.appendEscaped(result);
			html.setHTML(htmlBuilder.toSafeHtml());
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}
	};
}
