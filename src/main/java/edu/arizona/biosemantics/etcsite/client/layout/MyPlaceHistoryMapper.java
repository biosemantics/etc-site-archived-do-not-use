package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

import edu.arizona.biosemantics.etcsite.client.content.about.AboutPlace;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.AnnotationReviewPlace;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.gettingstarted.GettingStartedPlace;
import edu.arizona.biosemantics.etcsite.client.content.home.HomePlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationDefinePlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationOutputPlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationProcessPlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationReviewPlace;
import edu.arizona.biosemantics.etcsite.client.content.news.NewsPlace;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeBuildPlace;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeDefinePlace;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeOutputPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupDefinePlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupLearnPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupOutputPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupParsePlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupPreprocessPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupReviewPlace;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsPlace;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonAlignPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonDefinePlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationDefinePlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationViewPlace;
import edu.arizona.biosemantics.etcsite.client.content.visualization.VisualizationPlace;

@WithTokenizers( { 
	HomePlace.Tokenizer.class, 
	AboutPlace.Tokenizer.class,
	NewsPlace.Tokenizer.class,
	GettingStartedPlace.Tokenizer.class,
	FileManagerPlace.Tokenizer.class, 
	TaskManagerPlace.Tokenizer.class,
	SettingsPlace.Tokenizer.class,
	AnnotationReviewPlace.Tokenizer.class,
	SemanticMarkupDefinePlace.Tokenizer.class, 
	SemanticMarkupInputPlace.Tokenizer.class, 
	SemanticMarkupDefinePlace.Tokenizer.class, 
	SemanticMarkupPreprocessPlace.Tokenizer.class, 
	SemanticMarkupLearnPlace.Tokenizer.class,
	SemanticMarkupReviewPlace.Tokenizer.class,
	SemanticMarkupParsePlace.Tokenizer.class,
	SemanticMarkupOutputPlace.Tokenizer.class,
	OntologizeDefinePlace.Tokenizer.class,
	OntologizeInputPlace.Tokenizer.class,
	OntologizeBuildPlace.Tokenizer.class,
	OntologizeOutputPlace.Tokenizer.class,
	MatrixGenerationInputPlace.Tokenizer.class,
	MatrixGenerationProcessPlace.Tokenizer.class,
	MatrixGenerationReviewPlace.Tokenizer.class,
	MatrixGenerationOutputPlace.Tokenizer.class,
	TreeGenerationDefinePlace.Tokenizer.class,
	TreeGenerationInputPlace.Tokenizer.class,
	TreeGenerationViewPlace.Tokenizer.class,
	TaxonomyComparisonAlignPlace.Tokenizer.class,
	TaxonomyComparisonDefinePlace.Tokenizer.class,
	TaxonomyComparisonInputPlace.Tokenizer.class,
	VisualizationPlace.Tokenizer.class
	})
public interface MyPlaceHistoryMapper extends PlaceHistoryMapper {
}
