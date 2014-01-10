package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review;

import com.google.gwt.dom.client.NativeEvent;

public class UnusedCharactersClickHandler implements ClickHandler {

	private ReviewView viewImpl;

	public UnusedCharactersClickHandler(ReviewView viewImpl) {
		this.viewImpl = viewImpl;
	}
	
	public void onClick(com.google.gwt.cell.client.Cell.Context context, NativeEvent event) {
		viewImpl.orderCharactersForTaxon(context.getIndex());
	}

	public void onDoubleClick(com.google.gwt.cell.client.Cell.Context context, NativeEvent event) {
		
	}

	public void onContext(com.google.gwt.cell.client.Cell.Context context, NativeEvent event) {
		
	}

}
