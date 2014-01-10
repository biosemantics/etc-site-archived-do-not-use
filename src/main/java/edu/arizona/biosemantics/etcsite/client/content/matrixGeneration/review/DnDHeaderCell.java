package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review;

import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class DnDHeaderCell extends CompositeCell<String> {

	public DnDHeaderCell(List<HasCell<String, ?>> hasCells) {
		super(hasCells);
	}

	protected <X> void render(Context context, String value, SafeHtmlBuilder sb, HasCell<String, X> hasCell) {
		Cell<X> cell = hasCell.getCell();
		sb.appendHtmlConstant("<div style='display:block;padding-bottom:5px;'>");
		cell.render(context, hasCell.getValue(value), sb);
		sb.appendHtmlConstant("</div>");
	}

}
