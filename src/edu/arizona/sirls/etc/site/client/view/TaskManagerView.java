package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import edu.arizona.sirls.etc.site.client.presenter.TaskManagerPresenter;

public class TaskManagerView extends Composite implements TaskManagerPresenter.Display {

	private FlexTable yourTasksTable;
	private FlexTable sharedTasksTable;
	
	public TaskManagerView() {
		HTMLPanel htmlPanel = new HTMLPanel("<div class='content900pxCentered'>" +
				"<div id='taskManagerContent'></div></div>");
		
		this.yourTasksTable = new FlexTable();
		yourTasksTable.setStyleName("table");
		DOM.setElementAttribute(yourTasksTable.getElement(), "id", "yourTasksTable");
		yourTasksTable.setTitle("Your tasks");
		Label startedLabel = new Label("Started");
		startedLabel.setStyleName("tableHeader");
		Label taskTypeLabel = new Label("Task Type");
		taskTypeLabel.setStyleName("tableHeader");
		Label taskName = new Label("Name");
		taskName.setStyleName("tableHeader");
		Label progressLabel = new Label("Progress");
		progressLabel.setStyleName("tableHeader");
		
		yourTasksTable.setWidget(0,  0, startedLabel);
		yourTasksTable.setWidget(0,  1, taskTypeLabel);
		yourTasksTable.setWidget(0,  2, taskName);
		yourTasksTable.setWidget(0,  3, progressLabel);
		
		this.sharedTasksTable = new FlexTable();
		sharedTasksTable.setStyleName("table");
		DOM.setElementAttribute(sharedTasksTable.getElement(), "id", "sharedTasksTable");
		sharedTasksTable.setTitle("Shared tasks");
		startedLabel = new Label("Started");
		startedLabel.setStyleName("tableHeader");
		taskTypeLabel = new Label("Task Type");
		taskTypeLabel.setStyleName("tableHeader");
		taskName = new Label("Name");
		taskName.setStyleName("tableHeader");
		progressLabel = new Label("Progress");
		progressLabel.setStyleName("tableHeader");
		
		sharedTasksTable.setWidget(0,  0, startedLabel);
		sharedTasksTable.setWidget(0,  1, taskTypeLabel);
		sharedTasksTable.setWidget(0,  2, taskName);
		sharedTasksTable.setWidget(0,  3, progressLabel);

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(yourTasksTable);
		horizontalPanel.add(sharedTasksTable);

	    htmlPanel.add(horizontalPanel, "taskManagerContent");	
		this.initWidget(htmlPanel);
	}

	@Override
	public FlexTable getSharedTasksTable() {
		return this.sharedTasksTable;
	}

	@Override
	public FlexTable getYourTasksTable() {
		return this.yourTasksTable;
	}

}
