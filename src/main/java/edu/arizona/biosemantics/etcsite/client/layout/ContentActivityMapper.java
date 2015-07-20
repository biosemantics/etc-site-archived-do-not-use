package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.activity.shared.MyActivity;
import com.google.gwt.activity.shared.MyActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.content.about.AboutActivity;
import edu.arizona.biosemantics.etcsite.client.content.about.AboutPlace;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.AnnotationReviewActivity;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.AnnotationReviewPlace;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerActivity;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.gettingstarted.GettingStartedActivity;
import edu.arizona.biosemantics.etcsite.client.content.gettingstarted.GettingStartedPlace;
import edu.arizona.biosemantics.etcsite.client.content.home.HomeActivity;
import edu.arizona.biosemantics.etcsite.client.content.home.HomePlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationActivity;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationPlace;
import edu.arizona.biosemantics.etcsite.client.content.news.NewsActivity;
import edu.arizona.biosemantics.etcsite.client.content.news.NewsPlace;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeActivity;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizePlace;
import edu.arizona.biosemantics.etcsite.client.content.sample.SampleActivity;
import edu.arizona.biosemantics.etcsite.client.content.sample.SamplePlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupActivity;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupPlace;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsActivity;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsPlace;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerActivity;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonActivity;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationActivity;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationPlace;

public class ContentActivityMapper implements MyActivityMapper {

	private GettingStartedActivity helpActivity;
	private HomeActivity homeActivity;
	private NewsActivity newsActivity;
	private AboutActivity aboutActivity;
	private SettingsActivity settingsActivity;
	private TaskManagerActivity taskManagerActivity;
	private FileManagerActivity fileManagerActivity;
	private SemanticMarkupActivity semanticMarkupActivity;
	private MatrixGenerationActivity matrixGenerationActivity;
	private AnnotationReviewActivity annotationReviewActivity;
	private TreeGenerationActivity treeGenerationActivity;
	private TaxonomyComparisonActivity taxonomyComparisonActivity;
	private OntologizeActivity ontologizeActivity;
	private SampleActivity sampleActivity;
	
	private MyActivity currentActivity;

	@Inject
	public ContentActivityMapper(GettingStartedActivity helpActivity, HomeActivity homeActivity, AboutActivity aboutActivity, NewsActivity newsActivity, SettingsActivity settingsActivity, TaskManagerActivity taskManagerActivity,
			FileManagerActivity fileManagerActivity, SemanticMarkupActivity semanticMarkupActivity, MatrixGenerationActivity matrixGenerationActivity, 
			AnnotationReviewActivity annotationReviewActivity, TreeGenerationActivity treeGenerationActivity, 
			TaxonomyComparisonActivity taxonomyComparisonActivity, SampleActivity sampleActivity, 
			OntologizeActivity ontologizeActivity) {
		super();
		this.helpActivity = helpActivity;
		this.homeActivity = homeActivity;
		this.aboutActivity = aboutActivity;
		this.newsActivity = newsActivity;
		this.settingsActivity = settingsActivity;
		this.taskManagerActivity = taskManagerActivity;
		this.fileManagerActivity = fileManagerActivity;
		this.semanticMarkupActivity = semanticMarkupActivity;
		this.matrixGenerationActivity = matrixGenerationActivity;
		this.annotationReviewActivity = annotationReviewActivity;
		this.treeGenerationActivity = treeGenerationActivity;
		this.taxonomyComparisonActivity = taxonomyComparisonActivity;
		this.sampleActivity = sampleActivity;
		this.ontologizeActivity = ontologizeActivity;
	}

	@Override
	public MyActivity getActivity(Place place) {
		if(currentActivity == null)
			currentActivity = homeActivity;
		
		if(place instanceof HomePlace) 
			currentActivity = homeActivity;
		if(place instanceof SettingsPlace)
			currentActivity = settingsActivity;
		if(place instanceof GettingStartedPlace)
			currentActivity = helpActivity;
		if(place instanceof AboutPlace)
			currentActivity = aboutActivity;
		if(place instanceof NewsPlace)
			currentActivity = newsActivity;
		if(place instanceof TaskManagerPlace)
			currentActivity = taskManagerActivity;
		if(place instanceof FileManagerPlace)
			currentActivity = fileManagerActivity;
		if(place instanceof SemanticMarkupPlace) 
			currentActivity = semanticMarkupActivity;
		if(place instanceof MatrixGenerationPlace)
			currentActivity = matrixGenerationActivity;
		if(place instanceof AnnotationReviewPlace)
			currentActivity = annotationReviewActivity;
		if(place instanceof TreeGenerationPlace)
			currentActivity = treeGenerationActivity;
		if(place instanceof TaxonomyComparisonPlace) 
			currentActivity = taxonomyComparisonActivity;
		if(place instanceof SamplePlace)
			currentActivity = sampleActivity;
		if(place instanceof OntologizePlace)
			currentActivity = ontologizeActivity;
		
		return currentActivity;
	}
	
}
