package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.activity.shared.MyActivity;
import com.google.gwt.activity.shared.MyActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.content.annotationReview.AnnotationReviewActivity;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.AnnotationReviewPlace;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerActivity;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.help.HelpActivity;
import edu.arizona.biosemantics.etcsite.client.content.help.HelpPlace;
import edu.arizona.biosemantics.etcsite.client.content.home.HomeActivity;
import edu.arizona.biosemantics.etcsite.client.content.home.HomePlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationActivity;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupActivity;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupPlace;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsActivity;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsPlace;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerActivity;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.top.LoggedOutPlace;

public class ContentActivityMapper implements MyActivityMapper {

	private HelpActivity helpActivity;
	private HomeActivity homeActivity;
	private SettingsActivity settingsActivity;
	private TaskManagerActivity taskManagerActivity;
	private FileManagerActivity fileManagerActivity;
	private SemanticMarkupActivity semanticMarkupActivity;
	private MatrixGenerationActivity matrixGenerationActivity;
	private AnnotationReviewActivity annotationReviewActivity;
	
	private MyActivity currentActivity;

	@Inject
	public ContentActivityMapper(HelpActivity helpActivity, HomeActivity homeActivity, SettingsActivity settingsActivity, TaskManagerActivity taskManagerActivity,
			FileManagerActivity fileManagerActivity, SemanticMarkupActivity semanticMarkupActivity, MatrixGenerationActivity matrixGenerationActivity, 
			AnnotationReviewActivity annotationReviewActivity) {
		super();
		this.helpActivity = helpActivity;
		this.homeActivity = homeActivity;
		this.settingsActivity = settingsActivity;
		this.taskManagerActivity = taskManagerActivity;
		this.fileManagerActivity = fileManagerActivity;
		this.semanticMarkupActivity = semanticMarkupActivity;
		this.matrixGenerationActivity = matrixGenerationActivity;
		this.annotationReviewActivity = annotationReviewActivity;
	}

	@Override
	public MyActivity getActivity(Place place) {
		if(currentActivity == null)
			currentActivity = homeActivity;
		
		if(place instanceof HomePlace) 
			currentActivity = homeActivity;
		if(place instanceof SettingsPlace)
			currentActivity = settingsActivity;
		if(place instanceof HelpPlace)
			currentActivity = helpActivity;
		if(place instanceof LoggedOutPlace)
			currentActivity = homeActivity;
		if(place instanceof TaskManagerPlace)
			currentActivity = taskManagerActivity;
		if(place instanceof FileManagerPlace)
			currentActivity = fileManagerActivity;
		if(place instanceof SemanticMarkupPlace) {
			semanticMarkupActivity.setTask(((SemanticMarkupPlace) place).getTask());
			currentActivity = semanticMarkupActivity;
		}
		if(place instanceof MatrixGenerationPlace) {
			matrixGenerationActivity.setTask(((MatrixGenerationPlace)place).getTask());
			currentActivity = matrixGenerationActivity;
		}
		if(place instanceof AnnotationReviewPlace)
			currentActivity = annotationReviewActivity;
		
		return currentActivity;
	}

}
