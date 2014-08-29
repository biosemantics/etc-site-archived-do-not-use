package edu.arizona.biosemantics.etcsite.client.common;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class MessagePresenter {

	public interface IConfirmListener {
		public void onConfirm();
		public void onCancel();
	}
	
	public abstract static class AbstractConfirmListener implements IConfirmListener {
		public void onCancel() { }
	}
	
	public void showYesNoBox(String title, String message) {
		ConfirmMessageBox box = new ConfirmMessageBox(title, message);
		box.show();
	}
	
	public void showOkBox(String title, String message) {
		showBoxWithButtonText(title, message, "Ok");
	}	
	
	public void showCloseBox(String title, String message) {
		showBoxWithButtonText(title, message, "Close");
	}
	
	private void showBoxWithButtonText(String title, String message, String buttonText) {
		ConfirmMessageBox box = new ConfirmMessageBox(title, message);
		box.setPredefinedButtons(PredefinedButton.OK);
		box.getButton(PredefinedButton.OK).setText(buttonText);
		box.show();		
	}
	

	public void showOkCandelBox(String title, String message, final IConfirmListener listener) {
		ConfirmMessageBox box = new ConfirmMessageBox("Confirm", "Are you sure you want to do that?");
		
		box.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if(listener != null)
					listener.onCancel();
			}
		});
		
		box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if(listener != null)
					listener.onConfirm();
			}
		});
		
		box.setHeadingText(title);
		box.setMessage(message);
        box.show();
        
	}
	
	public void showOkCandelBox(String title, String message, String noText, String yesText, final IConfirmListener listener) {
		ConfirmMessageBox box = new ConfirmMessageBox("Confirm", "Are you sure you want to do that?");
		
		box.getButton(PredefinedButton.NO).setText(noText);
		box.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if(listener != null)
					listener.onCancel();
			}
		});
		
		box.getButton(PredefinedButton.YES).setText(yesText);
		box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if(listener != null)
					listener.onConfirm();
			}
		});
		
		box.setHeadingText(title);
		box.setMessage(message);
        box.show();
        
	}
}
