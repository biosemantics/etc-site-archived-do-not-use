package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.ImageLabel;
import edu.arizona.biosemantics.etcsite.client.help.HelpActivity;

public class EtcSiteView extends Composite implements IEtcSiteView, RequiresResize {

	private static EtcSiteUiBinder uiBinder = GWT.create(EtcSiteUiBinder.class);

	interface EtcSiteUiBinder extends UiBinder<Widget, EtcSiteView> {
	}
	
	@UiField
	Button openInNewWindowButton;
	
	@UiField
	VerticalLayoutContainer eastPanel;
	
	@UiField
	SimpleLayoutPanel helpPanel;
	
	@UiField
	FocusPanel navigationPanel;
	
	@UiField
	SimpleLayoutPanel contentPanel;

	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	ImageLabel loginLogout;
	
	@UiField
	ImageLabel taskManager;
	
	@UiField
	ImageLabel help;
	
	@UiField(provided=true)
	Label name;
		
	private Presenter presenter;

	private HelpActivity helpActivity;
	
	@Inject
	public EtcSiteView(HelpActivity helpActivity) {
		this.helpActivity = helpActivity;
		name = new Label();
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void setContent(IsWidget content) {
		contentPanel.setWidget(content);
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		
	}

	@Override
	public SimpleLayoutPanel getContentContainer() {
		return this.contentPanel;
	}
	
	@Override
	public SimpleLayoutPanel getHelpContainer() {
		return helpPanel;
	}

	@Override
	public void setNavigationSize(int size, boolean animated) {
		//dockLayoutPanel.forceLayout(); //makes fast mouse movement not to collapse the menu without animation (for some reason)
		dockLayoutPanel.setWidgetSize(navigationPanel, size);
		
		if(animated)
			dockLayoutPanel.animate(300);
	}
	
	@Override
	public void setHelpSize(int size) { //, boolean animated) {
		//dockLayoutPanel.forceLayout(); //makes fast mouse movement not to collapse the menu without animation (for some reason)
		dockLayoutPanel.setWidgetSize(eastPanel, size);
		//if(animated)
		//	dockLayoutPanel.animate(300);
		
		if(size == 0) 
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					helpActivity.getView().onHide();
					helpActivity.getView().onResize();
				}
			});
		else 
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					helpActivity.getView().onShow();
					helpActivity.getView().onResize();
				}
			});
		//helpActivity.getView().expandDefaultItem();
	}
	
	/*@UiHandler("menu")
	void onMenuClick(ClickEvent e) {
		Double size = dockLayoutPanel.getWidgetSize(navigationPanel);
		if(size == 200) 
			setNavigationSize(0, true);
		else
			setNavigationSize(200, true);
	}*/
	
	@UiHandler("getstarted")
	public void onGetStarted(ClickEvent e) {
		presenter.onGetStarted();
	}
	
	@UiHandler("loginLogout")
	public void onLoginLogout(ClickEvent e) {
		presenter.onLoginLogout();
	}
	
	@UiHandler("navigationPanel") 
	public void onMouseOverMenu(MouseOverEvent event) { 
		setNavigationSize(200, true);
	}
	
	@UiHandler("navigationPanel") 
	public void onMouseOutMenu(MouseOutEvent event) {
		setNavigationSize(0, true);
	}
	
	@UiHandler("menu") 
	public void onMouseOverMenuIcon(MouseOverEvent event) { 
		setNavigationSize(200, true);
	}
	
	@UiHandler("menu") 
	public void onMouseOutMenuIcon(MouseOutEvent event) {
		setNavigationSize(0, true);
	}
	
	@UiHandler("help")
	void onHelpClick(ClickEvent e) {
		Double size = dockLayoutPanel.getWidgetSize(eastPanel);
		if(size == 400){
			setHelpSize(0);
			help.setText("Help");
		}else{
			setHelpSize(400);
			help.setText("Hide Help");
		}
	}
	
	@UiHandler("homeMenu")
	void onHomeMenuClick(ClickEvent e) {
		presenter.onHome();
	}
	
	@UiHandler("about")
	void onAboutClick(ClickEvent e) {
		presenter.onAbout();
	}
	
	@UiHandler("news")
	void onNewsClick(ClickEvent e) {
		presenter.onNews();
	}
	
	@UiHandler("taskManager")
	void onTaskManagerClick(ClickEvent e) {
		presenter.onTaskManager();
	}
	
	@UiHandler("fileManager")
	void onFileManagerClick(ClickEvent e) {
		presenter.onFileManager();
	}
	
	@UiHandler("account")
	void onAccountClick(ClickEvent e) {
		presenter.onAccount();
	}
	
	@UiHandler("textCapture")
	void onTextCaptureClick(ClickEvent e) {
		presenter.onTextCapture();
	}
	
	@UiHandler("ontologize")
	void onOntologizeClick(ClickEvent e) {
		presenter.onOntologize();
	}
	
	@UiHandler("matrixGeneration")
	void onMatrixGenerationClick(ClickEvent e) {
		presenter.onMatrixGeneration();
	}
	
	@UiHandler("treeGeneration")
	void onTreeGenerationClick(ClickEvent e) {
		presenter.onTreeGeneration();
	}
	
	@UiHandler("taxonomyComparison")
	void onTaxonomyComparisonClick(ClickEvent e) {
		presenter.onTaxonomyComparison();
	}
	
	/*@UiHandler("visualization")
	void onVisualizationClick(ClickEvent e) {
		presenter.onVisualization();
	}*/
	
	@UiHandler("openInNewWindowButton")
	void onOpenClick(ClickEvent e){
		presenter.onOpenHelpInNewWindow();
		setHelpSize(0);
		help.setText("Help");
	}
	
	@UiHandler("sample")
	void onSampleInput1(ClickEvent e) {
		presenter.onSample();
	}
	
	
	@Override
	public void setLogin() {
		loginLogout.setText("Login");
		loginLogout.setImage("images/login.gif");
	}

	@Override 
	public void setLogout() {
		name.setText(Authentication.getInstance().getFirstName());
		loginLogout.setText("Logout");
		loginLogout.setImage("images/logout.gif");
	}
	
	@Override
	public void setName(String name) {
		this.name.setText(name);

	}

	@Override
	public boolean isLogin() {
		return loginLogout.getImage().endsWith("images/login.gif");
	}

	@Override
	public boolean isLogout() {
		name.setText("");
		return loginLogout.getImage().endsWith("images/logout.gif");
	}

	@Override
	public void setResumableTasks(boolean value) {
		if(value)
			taskManager.setImage("images/TaskManager_notification.gif");
		else
			taskManager.setImage("images/TaskManager.gif");
	}

	@Override
	public void onResize() {
		dockLayoutPanel.onResize();
		contentPanel.onResize();
		helpPanel.onResize();
	}
	
}