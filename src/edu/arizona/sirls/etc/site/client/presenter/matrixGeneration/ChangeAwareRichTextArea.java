package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RichTextArea;

public class ChangeAwareRichTextArea extends RichTextArea implements
		HasValueChangeHandlers<String>, HasValue<String> {

	private boolean valueChangeHandlerInitialized;

	public ChangeAwareRichTextArea() {
		super();
		sinkEvents(Event.ONPASTE);
		sinkEvents(Event.KEYEVENTS);
		sinkEvents(Event.GESTUREEVENTS);
		sinkEvents(Event.MOUSEEVENTS);
	}

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		switch (DOM.eventGetType(event)) {
		case Event.ONPASTE:
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					ValueChangeEvent.fire(ChangeAwareRichTextArea.this,
							getText());
				}
			});
			break;
		default:
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					ValueChangeEvent.fire(ChangeAwareRichTextArea.this, getText());
				}
			});
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<String> handler) {
		// Initialization code
		if (!valueChangeHandlerInitialized) {
			valueChangeHandlerInitialized = true;
			addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					ValueChangeEvent.fire(ChangeAwareRichTextArea.this,
							getValue());
				}
			});
		}
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}

	@Override
	public String getValue() {
		return getHTML();
	}

	@Override
	public void setValue(String value) {
		if (value == null) {
			return;
		}
		SafeHtml html = SimpleHtmlSanitizer.sanitizeHtml(value);
		setHTML(html);
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		SafeHtml html = SimpleHtmlSanitizer.sanitizeHtml(value);
		setHTML(html);
		if (fireEvents) {
			ValueChangeEvent.fireIfNotEqual(this, getHTML(), value);
		}
	}

}