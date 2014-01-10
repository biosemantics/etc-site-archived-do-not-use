package edu.arizona.biosemantics.etcsite.client.content.user;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import edu.arizona.biosemantics.etcsite.shared.db.ShortUser;

public class ShortUserCell extends AbstractCell<ShortUser> {
	
	/**
	 * The HTML templates used to render the cell.
	 */
	interface Templates extends SafeHtmlTemplates {
		/**
		 * The template for this Cell, which includes styles and a value.
		 * 
		 * @param styles
		 *            the styles to include in the style attribute of the div
		 * @param value
		 *            the safe value. Since the value type is {@link SafeHtml},
		 *            it will not be escaped before including it in the
		 *            template. Alternatively, you could make the value type
		 *            String, in which case the value would be escaped.
		 * @return a {@link SafeHtml} instance
		 */
		@SafeHtmlTemplates.Template("<div style=\"{0}\">{1}</div>")
		SafeHtml cell(SafeStyles styles, SafeHtml value);
	}

	/**
	 * Create a singleton instance of the templates used to render the cell.
	 */
	private static Templates templates = GWT.create(Templates.class);

	@Override
	public void render(Context context, ShortUser value, SafeHtmlBuilder sb) {
		/*
		 * Always do a null check on the value. Cell widgets can pass null to
		 * cells if the underlying data contains a null, or if the data arrives
		 * out of order.
		 */
		if (value == null) {
			return;
		}

		SafeHtml safeValue = SafeHtmlUtils.fromString(value.getName());
		SafeStyles styles = SafeStylesUtils.forTrustedColor(safeValue.asString());
		SafeHtml rendered = templates.cell(styles, safeValue);
		sb.append(rendered);
	}
}
