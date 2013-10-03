package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.dom.client.Style;

import edu.arizona.sirls.etc.site.client.presenter.TaskManagerPresenter;

public class TaskManagerView extends Composite implements TaskManagerPresenter.Display {

	private FlexTable yourTasksTable;
	//private FlexTable sharedTasksTable;
	private FlexTable historyTable;
	
	public TaskManagerView() {
		HTMLPanel htmlPanel = new HTMLPanel("<div class='content900pxCentered'>" +
				"<div id='taskManagerContent'></div></div>");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		Label headingLabel = new Label("Task Manager");
		headingLabel.addStyleName("siteHeading");
		verticalPanel.add(headingLabel);
		
		VerticalPanel yourTasksPanel = new VerticalPanel();
		yourTasksPanel.add(new Label("Your tasks"));
		VerticalPanel historyPanel = new VerticalPanel();
		historyPanel.add(new Label("History"));
		this.yourTasksTable = createYourTasksTable();
		this.historyTable = createHistoryTable();
		historyPanel.add(historyTable);
		yourTasksPanel.add(yourTasksTable);
		
		/*this.sharedTasksTable = new FlexTable();
		sharedTasksTable.setStyleName("table");
		DOM.setElementAttribute(sharedTasksTable.getElement(), "id", "sharedTasksTable");
		sharedTasksTable.setTitle("Shared tasks");
		createdLabel = new Label("Created");
		createdLabel.setStyleName("tableHeader");
		taskTypeLabel = new Label("Task Type");
		taskTypeLabel.setStyleName("tableHeader");
		progressLabel = new Label("Progress");
		progressLabel.setStyleName("tableHeader");
		nameLabel = new Label("Name");
		nameLabel.setStyleName("tableHeader");
		resumeLabel = new Label("Resume");
		resumeLabel.setStyleName("tableHeader");
		cancelLabel = new Label("Cancel");
		cancelLabel.setStyleName("tableHeader");
		
		sharedTasksTable.setWidget(0,  0, createdLabel);
		sharedTasksTable.setWidget(0,  1, taskTypeLabel);
		sharedTasksTable.setWidget(0,  2, progressLabel);
		sharedTasksTable.setWidget(0,  3, nameLabel);
		sharedTasksTable.setWidget(0,  4, resumeLabel);
		sharedTasksTable.setWidget(0,  5, cancelLabel);
		*/
		//HorizontalPanel horizontalPanel = new HorizontalPanel();
		//horizontalPanel.add(yourTasksTable);
		//horizontalPanel.add(sharedTasksTable);

		yourTasksPanel.addStyleName("yourTasksTable");
		historyPanel.addStyleName("historyTasksTable");
		verticalPanel.add(yourTasksPanel);
		verticalPanel.add(historyPanel);
		
	    htmlPanel.add(verticalPanel, "taskManagerContent");	
		this.initWidget(htmlPanel);
	}

	/*@Override
	public FlexTable getSharedTasksTable() {
		return this.sharedTasksTable;
	}*/

	private FlexTable createHistoryTable() { 
		FlexTable result = new FlexTable();
		result.setStyleName("table");
		DOM.setElementAttribute(result.getElement(), "id", "historyTasksTable");
		result.setTitle("Task History");
		Label createdLabel = new Label("Created");
		createdLabel.setStyleName("tableHeader");
		Label taskTypeLabel = new Label("Task Type");
		taskTypeLabel.setStyleName("tableHeader");
		Label statusLabel = new Label("Status");
		statusLabel.setStyleName("tableHeader");
		Label nameLabel = new Label("Name");
		nameLabel.setStyleName("tableHeader");
		Label actionsLabel = new Label("Actions");
		actionsLabel.setStyleName("tableHeader");

		result.setWidget(0,  0, nameLabel);
		result.setWidget(0,  1, createdLabel);
		result.setWidget(0,  2, taskTypeLabel);
		result.setWidget(0,  4, actionsLabel);
		return result;
	}
	
	private FlexTable createYourTasksTable() {
		return this.createTasksTable("Your tasks", "yourTasksTable");
	}

	private FlexTable createTasksTable(String title, String cssId) {
		FlexTable result = new FlexTable();
		result.setStyleName("table");
		DOM.setElementAttribute(result.getElement(), "id", cssId);
		result.setTitle(title);
		Label createdLabel = new Label("Created");
		createdLabel.setStyleName("tableHeader");
		Label taskTypeLabel = new Label("Task Type");
		taskTypeLabel.setStyleName("tableHeader");
		Label statusLabel = new Label("Status");
		statusLabel.setStyleName("tableHeader");
		Label nameLabel = new Label("Name");
		nameLabel.setStyleName("tableHeader");
		Label actionsLabel = new Label("Actions");
		actionsLabel.setStyleName("tableHeader");

		result.setWidget(0,  0, nameLabel);
		result.setWidget(0,  1, createdLabel);
		result.setWidget(0,  2, taskTypeLabel);
		result.setWidget(0,  3, statusLabel);
		result.setWidget(0,  4, actionsLabel);
		return result;
	}

	@Override
	public FlexTable getYourTasksTable() {
		return this.yourTasksTable;
	}

	@Override
	public FlexTable getHistoryTable() {
		return this.historyTable;
	}

}
