package edu.arizona.sirls.etc.site.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.builder.dialog.CloseDialogBoxClickHandler;
import edu.arizona.sirls.etc.site.client.builder.dialog.WidgetDialogBox;
import edu.arizona.sirls.etc.site.shared.rpc.FileFormatter;
import edu.arizona.sirls.etc.site.shared.rpc.FileType;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;

public class FileImageLabelCompositeDoubleClickHandler implements DoubleClickHandler {

	private final IFileAccessServiceAsync fileAccessService = GWT.create(IFileAccessService.class);
	
	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		Object source = event.getSource();
		if(source instanceof FileImageLabelComposite) { 
			String target = ((FileImageLabelComposite) source).getPath();
			fileAccessService.getFileContent(Authentication.getInstance().getAuthenticationToken(), target, fileContentCallback);
		}
	}

	protected AsyncCallback<String> fileContentCallback = new AsyncCallback<String>() {
		public void onSuccess(String result) {
			SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder();
			htmlBuilder.appendEscaped(result);
			final HTML html = new HTML(htmlBuilder.toSafeHtml());
			
			VerticalPanel verticalPanel = new VerticalPanel();
			
			// Make a new list box, adding a few items to it.
		    HorizontalPanel formatPanel = new HorizontalPanel();
		    formatPanel.add(new Label("Format for:"));
			final ListBox formatListBox = new ListBox();
		    for(FileType fileType : FileType.values())
			    formatListBox.addItem(fileType.displayName());
		    formatListBox.setVisibleItemCount(1);
		    
		    formatListBox.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					FileType selection = FileType.valueOf(formatListBox.getValue(formatListBox.getSelectedIndex()));
					FileFormatter fileFormatter = new FileFormatter();
					html.setText(fileFormatter.format(html.getText(), selection));
				}
		    });
		    
		    formatPanel.add(formatListBox);
			
		    
		    verticalPanel.add(formatPanel);
			
			
			ScrollPanel scrollPanel = new ScrollPanel(html);
			scrollPanel.addStyleName("fileContent");
			WidgetDialogBox widgetDialogBox = new WidgetDialogBox(true, "File content", verticalPanel);
			verticalPanel.add(scrollPanel);
		
			Button closeButton = new Button("Close");
			closeButton.addClickHandler(new CloseDialogBoxClickHandler(widgetDialogBox));
			verticalPanel.add(closeButton);
					
			widgetDialogBox.setGlassEnabled(true); 
			
			widgetDialogBox.center();
			widgetDialogBox.show(); 
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}
	};
		
}
