package edu.arizona.sirls.etc.site.client.builder.dialog;

import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.Widget;

public class WidgetDialogBox extends TitleCloseDialogBox {
	
	public WidgetDialogBox(boolean autoHide, String title, Widget widget) { 
		super(autoHide, title);
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
