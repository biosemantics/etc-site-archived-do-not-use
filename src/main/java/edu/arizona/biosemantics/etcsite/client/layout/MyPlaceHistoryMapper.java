package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

import edu.arizona.biosemantics.etcsite.client.content.annotationReview.AnnotationReviewPlace;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.home.HomePlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationOutputPlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationProcessPlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationReviewPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupLearnPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupOutputPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupParsePlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupPreprocessPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupReviewPlace;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsPlace;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
import edu.arizona.biosemantics.etcsite.client.content.visualization.VisualizationPlace;

@WithTokenizers( { 
	HomePlace.Tokenizer.class, 
	FileManagerPlace.Tokenizer.class, 
	TaskManagerPlace.Tokenizer.class,
	SettingsPlace.Tokenizer.class,
	AnnotationReviewPlace.Tokenizer.class,
	SemanticMarkupInputPlace.Tokenizer.class, 
	SemanticMarkupPreprocessPlace.Tokenizer.class, 
	SemanticMarkupLearnPlace.Tokenizer.class,
	SemanticMarkupReviewPlace.Tokenizer.class,
	SemanticMarkupParsePlace.Tokenizer.class,
	SemanticMarkupOutputPlace.Tokenizer.class,
	MatrixGenerationInputPlace.Tokenizer.class,
	MatrixGenerationProcessPlace.Tokenizer.class,
	MatrixGenerationReviewPlace.Tokenizer.class,
	MatrixGenerationOutputPlace.Tokenizer.class,
	TaxonomyComparisonPlace.Tokenizer.class,
	VisualizationPlace.Tokenizer.class
	})
public interface MyPlaceHistoryMapper extends PlaceHistoryMapper {
}
