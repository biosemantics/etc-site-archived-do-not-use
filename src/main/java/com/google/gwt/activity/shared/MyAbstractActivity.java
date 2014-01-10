package com.google.gwt.activity.shared;

import com.google.gwt.activity.shared.Activity;

public abstract class MyAbstractActivity implements MyActivity {
	
	public String mayStop() {
		return null;
	}

	public void onCancel() {}

	public void onStop() {}
}
