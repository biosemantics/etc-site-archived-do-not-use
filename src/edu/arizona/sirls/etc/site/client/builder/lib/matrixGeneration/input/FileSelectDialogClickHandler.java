package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.input;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.client.builder.dialog.CloseDialogBoxClickHandler;
import edu.arizona.sirls.etc.site.client.builder.dialog.WidgetDialogBox;
import edu.arizona.sirls.etc.site.client.widget.FileTreeAndSelectComposite;
import edu.arizona.sirls.etc.site.shared.rpc.FileFilter;

public class FileSelectDialogClickHandler implements ClickHandler {
	
	private CloseDialogBoxClickHandler closeClickHandler;
	private FileSelectClickHandler selectClickHandler;
	private FileFilter fileFilter;

	public FileSelectDialogClickHandler(FileFilter fileFilter, CloseDialogBoxClickHandler closeClickHandler, 
			FileSelectClickHandler selectClickHandler) {
		this.fileFilter = fileFilter;
		this.closeClickHandler = closeClickHandler;
		this.selectClickHandler = selectClickHandler;
	}

	@Override
	public void onClick(ClickEvent event) {		
		FileTreeAndSelectComposite fileTreeAndSelectComposite = new FileTreeAndSelectComposite(true, fileFilter,
				selectClickHandler, closeClickHandler);
		fileTreeAndSelectComposite.addStyleName("fileSelect");
		VerticalPanel panel = new VerticalPanel();
		panel.add(fileTreeAndSelectComposite);
		Label errorLabel = new Label();
		panel.add(errorLabel);
		
		WidgetDialogBox dialogBox = new WidgetDialogBox(false, "Select File", panel);
		closeClickHandler.setDialogBox(dialogBox);
		selectClickHandler.setDialogBox(dialogBox);	
		selectClickHandler.setErrorText(errorLabel);
		selectClickHandler.setTree(fileTreeAndSelectComposite.getFileTreeComposite());
		
		dialogBox.center();
		dialogBox.setGlassEnabled(true);
 		dialogBox.show(); 
	}


}
