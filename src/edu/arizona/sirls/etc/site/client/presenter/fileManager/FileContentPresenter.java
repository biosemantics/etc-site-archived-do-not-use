package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileType;

public class FileContentPresenter {

	public interface Display {
		ListBox getFormatListBox();
		TextArea getTextArea();
		FocusWidget getCloseButton();
		Widget asWidget();
	}

	private Display display;
	private IFileAccessServiceAsync fileAccessService;
	private String target;
	private TitleCloseDialogBox dialogBox;
	private LoadingPopup loadingPopup;

	public FileContentPresenter(Display display, 
			IFileAccessServiceAsync fileAccessService, String target) {
		this.display = display;
		this.fileAccessService = fileAccessService;
		this.target = target;
		this.dialogBox = new TitleCloseDialogBox("File Content");
		bind();
	}

	private void bind() {
		final ListBox listBox = display.getFormatListBox();
		for(FileType fileType : FileType.values())
			listBox.addItem(fileType.toString());
		listBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if(loadingPopup == null) 
					loadingPopup = new LoadingPopup();
				loadingPopup.start();
				FileType fileType = FileType.valueOf(listBox.getValue(listBox.getSelectedIndex()));
				fileAccessService.getFileContent(
						Authentication.getInstance().getAuthenticationToken(), 
						target, fileType, new FileContentCallback());
			}
	    });
		display.getTextArea().setEnabled(false);
		display.getCloseButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
	}

	public void go() {
		if(loadingPopup == null) 
			loadingPopup = new LoadingPopup();
		loadingPopup.start();
		ListBox formatListBox = display.getFormatListBox();
		FileType fileType = FileType.valueOf(formatListBox.getValue(formatListBox.getSelectedIndex()));
		
		fileAccessService.getFileContent(Authentication.getInstance().getAuthenticationToken(), 
				target, fileType, new FileContentCallback());
		dialogBox.setGlassEnabled(true);
		dialogBox.clear();
		dialogBox.add(display.asWidget());
		dialogBox.center();
	}
	
	private class FileContentCallback implements AsyncCallback<String> {
		@Override
		public void onFailure(Throwable caught) {
			caught.printStackTrace();
			loadingPopup.stop();
		}
		@Override
		public void onSuccess(String result) {
			display.getTextArea().setText(result);
			loadingPopup.stop();
		}
	}
}
