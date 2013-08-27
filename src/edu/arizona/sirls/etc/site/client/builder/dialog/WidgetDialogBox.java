package edu.arizona.sirls.etc.site.client.builder.dialog;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class WidgetDialogBox extends DialogBox {
	
	public WidgetDialogBox(String title, Widget widget) { 
		this.setText(title);
		this.setAnimationEnabled(true);
		this.setWidget(widget);
	}
	
	/*public void scheduledCenter() { 
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
		    public void execute() {
		    	WidgetDialogBox.this.center();
		    }
		}); 
	}*/
}
