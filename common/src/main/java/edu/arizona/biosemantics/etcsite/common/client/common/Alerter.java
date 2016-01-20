package edu.arizona.biosemantics.etcsite.common.client.common;

import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;

public class Alerter {
	
	private static AutoProgressMessageBox box;
	
	public static MessageBox startLoading() {
		box = new AutoProgressMessageBox("Loading", "Loading your data, please wait...");
        box.setProgressText("Loading...");
        box.auto();
        box.show();
        return box;
	}
	
	public static void stopLoading() {
		if(box != null) {
			box.hide();
			box = null;
		}
	}
}
