package edu.arizona.sirls.etc.site.client.view.matrixGeneration.review;

import com.google.gwt.user.cellview.client.DataGrid;

public interface MyDataGridResource extends DataGrid.Resources {

	@Override
	@Source("DataGrid.css")
	MyDataGridStyle dataGridStyle();
	
}
