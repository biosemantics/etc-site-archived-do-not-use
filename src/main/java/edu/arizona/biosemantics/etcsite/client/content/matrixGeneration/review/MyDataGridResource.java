package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review;

import com.google.gwt.user.cellview.client.DataGrid;

public interface MyDataGridResource extends DataGrid.Resources {

	@Override
	@Source("DataGrid.css")
	MyDataGridStyle dataGridStyle();
	
}
