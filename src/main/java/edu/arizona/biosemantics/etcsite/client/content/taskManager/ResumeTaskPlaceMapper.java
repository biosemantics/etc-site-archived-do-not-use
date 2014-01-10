package edu.arizona.biosemantics.etcsite.client.content.taskManager;

import com.google.gwt.place.shared.Place;

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
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationPlace;
import edu.arizona.biosemantics.etcsite.client.content.visualization.VisualizationPlace;
import edu.arizona.biosemantics.etcsite.shared.db.Task;

public class ResumeTaskPlaceMapper {
	
	public Place getPlace(Task task) {
		switch(task.getTaskType().getTaskTypeEnum()) {
		case SEMANTIC_MARKUP:
			switch(edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.TaskStageEnum.valueOf(task.getTaskStage().getTaskStage())) {
				case INPUT:
					return new SemanticMarkupInputPlace();
				case PREPROCESS_TEXT:
					return new SemanticMarkupPreprocessPlace(task);
				case LEARN_TERMS:
					return new SemanticMarkupLearnPlace(task);
				case REVIEW_TERMS:
					return new SemanticMarkupReviewPlace(task);
				case PARSE_TEXT:
					return new SemanticMarkupParsePlace(task);
				case OUTPUT:
					return new SemanticMarkupOutputPlace(task);
			}
			return new SemanticMarkupInputPlace();
		case MATRIX_GENERATION:
			switch(edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.TaskStageEnum.valueOf(task.getTaskStage().getTaskStage())) {
				case INPUT:
					return new MatrixGenerationInputPlace();
				case PROCESS:
					return new MatrixGenerationProcessPlace(task);
				case REVIEW:
					return new MatrixGenerationReviewPlace(task);
				case OUTPUT:
					return new MatrixGenerationOutputPlace(task);
			}
			return new edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace();
		case TREE_GENERATION:
			return new TreeGenerationPlace();
		case TAXONOMY_COMPARISON:
			return new TaxonomyComparisonPlace();
		case VISUALIZATION:
			return new VisualizationPlace();
		}
		return new HomePlace();
	}
	
}	