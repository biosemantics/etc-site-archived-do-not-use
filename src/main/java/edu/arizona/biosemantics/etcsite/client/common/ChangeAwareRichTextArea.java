package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.impl.TextBoxImpl;

public class ChangeAwareRichTextArea extends RichTextArea implements
		HasValueChangeHandlers<String>, HasValue<String> {

	private boolean valueChangeHandlerInitialized;
	private String oldText = null;
	private static TextBoxImpl impl = GWT.create(TextBoxImpl.class);

	public ChangeAwareRichTextArea() {
		super();
		sinkEvents(Event.KEYEVENTS);
		sinkEvents(Event.GESTUREEVENTS);
		sinkEvents(Event.MOUSEEVENTS);
	}

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		switch (DOM.eventGetType(event)) {
		default:
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					if(oldText == null || !getText().equals(oldText)) {
						ValueChangeEvent.fire(ChangeAwareRichTextArea.this, getText());
						oldText = getText();
					}
				}
			});
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<String> handler) {
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
	
	/**
	 * Copied from ValueBoxBase source, a subclass of the TextArea
	 * 
	 * Gets the current position of the cursor (this also serves as the
	 * beginning of the text selection).
	 * 
	 * @return the cursor's position
	 */
	public int getCursorPos() {
		return impl.getCursorPos(getElement());
	}

	/**
	 * 	Copied from ValueBoxBase source, a subclass of the TextArea
	 * 
	 * Sets the cursor position.
	 * 
	 * This will only work when the widget is attached to the document and not
	 * hidden.
	 * 
	 * @param pos
	 *            the new cursor position
	 */
	public void setCursorPos(int pos) {
		setSelectionRange(pos, 0);
	}

	/**
	 * Copied from ValueBoxBase source, a subclass of the TextArea
	 * 
	 * Sets the range of text to be selected.
	 * 
	 * This will only work when the widget is attached to the document and not
	 * hidden.
	 * 
	 * @param pos
	 *            the position of the first character to be selected
	 * @param length
	 *            the number of characters to be selected
	 */
	public void setSelectionRange(int pos, int length) {
		// Setting the selection range will not work for unattached elements.
		if (!isAttached()) {
			return;
		}

		if (length < 0) {
			throw new IndexOutOfBoundsException("Length must be a positive integer. Length: " + length);
		}
		if ((pos < 0) || (length + pos > getText().length())) {
			throw new IndexOutOfBoundsException("From Index: " + pos + "  To Index: " + (pos + length) + "  Text Length: " + getText().length());
		}
		impl.setSelectionRange(getElement(), pos, length);
	}

}