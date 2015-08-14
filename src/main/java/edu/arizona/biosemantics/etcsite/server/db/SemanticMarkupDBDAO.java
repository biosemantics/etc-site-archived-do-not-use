package edu.arizona.biosemantics.etcsite.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Date;

import edu.arizona.biosemantics.common.ling.know.SingularPluralProvider;
import edu.arizona.biosemantics.common.ling.know.lib.WordNetPOSKnowledgeBase;
import edu.arizona.biosemantics.common.ling.transform.lib.SomeInflector;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.db.Query.QueryException;
import edu.arizona.biosemantics.etcsite.shared.model.DatasetPrefix;

public class SemanticMarkupDBDAO {

	private SingularPluralProvider singularPluralProvider = new SingularPluralProvider();

	public SemanticMarkupDBDAO() {
		
	}
	
	public DatasetPrefix getDatasetPrefix(String prefix) {
		DatasetPrefix datasetPrefix = null;
		try(Query query = new Query("SELECT * FROM " + Configuration.charaparser_databaseName + ".datasetprefixes WHERE prefix = ?")) {
			query.setParameter(1, prefix);
			ResultSet result = query.execute();
			while(result.next()) {
				prefix = result.getString(1);
				String glossaryVersion = result.getString(2);
				int otoUploadId = result.getInt(3);
				String otoSecret = result.getString(4);
				Date created = result.getTimestamp(5);
				datasetPrefix = new DatasetPrefix(prefix, glossaryVersion, otoUploadId, otoSecret, created);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get dataset prefix", e);
		}
		return datasetPrefix;
	}

	public void renameTerm(String prefix, String term, String newName) {
		try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "wordroles SET word=? WHERE word=?")) {
			query.setParameter(1, newName);
			query.setParameter(2, term);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't update wordroles for rename of term", e);
		}
		try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "wordpos SET word=? WHERE word=?")) {
			query.setParameter(1, newName);
			query.setParameter(2, term);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't update wordpos for rename of term", e);
		}
		try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "unknownwords SET word=?, flag=? WHERE word=? AND flag=?")) {
			query.setParameter(1, newName);
			query.setParameter(2, newName);
			query.setParameter(3, term);
			query.setParameter(4, term);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't update unknownwords for rename of term", e);
		}
		try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "unknownwords SET word=?, flag='unknown' WHERE word=? AND flag='unknown'")) {
			query.setParameter(1, newName);
			query.setParameter(2, term);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't update unknownwords for rename of term", e);
		}
		try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "term_category SET term=? WHERE term=?")) {
			query.setParameter(1, newName);
			query.setParameter(2, term);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't update term_category for rename of term", e);
		}
		try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "taxonnames SET name=? WHERE name=?")) {
			query.setParameter(1, newName);
			query.setParameter(2, term);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't update taxonnames for rename of term", e);
		}
		try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "syns SET term=?, synonym=? WHERE term=?")) {
			query.setParameter(1, newName);
			query.setParameter(2, newName);
			query.setParameter(3, term);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't update syns for rename of term", e);
		}
		try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "substructure SET structure=? WHERE structure=?")) {
			query.setParameter(1, newName);
			query.setParameter(2, term);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't update substructure for rename of term", e);
		}
		try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "substructure SET substructure=? WHERE substructure=?")) {
			query.setParameter(1, newName);
			query.setParameter(2, term);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't update substructure for rename of term", e);
		}
			
		//could be singular or plural; 
		//
		//assumption newName will be singular if term was singular; newName will be plural if term was plural;
		//otherwise it will be considered a "user error"
		
		
		try(WordNetPOSKnowledgeBase wordNetPOSKnowledgeBase = new WordNetPOSKnowledgeBase(Configuration.charaparser_wordnet, false)) {
			SomeInflector someInflector = new SomeInflector(wordNetPOSKnowledgeBase, singularPluralProvider.getSingulars(), singularPluralProvider.getPlurals());
			if(!someInflector.isPlural(term)) {
				String singular = newName;
				String plural = someInflector.getPlural(newName);
				
				try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "singularplural SET singular=?, plural=? WHERE singular=?")) {
					query.setParameter(1, singular);
					query.setParameter(2, plural);
					query.setParameter(3, term);
					query.execute();
				} catch(QueryException e) {
					log(LogLevel.ERROR, "Couldn't update singularplural for rename of term", e);
				}
			} else {
				String singular = someInflector.getSingular(newName);
				String plural = newName;
				try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "singularplural SET singular=?, plural=? WHERE plural=?")) {
					query.setParameter(1, singular);
					query.setParameter(2, plural);
					query.setParameter(3, term);
					query.execute();
				} catch(QueryException e) {
					log(LogLevel.ERROR, "Couldn't update singularplural for rename of term", e);
				}
			}
		} catch (Exception e) {
			log(LogLevel.ERROR, "Could not load WordNetPOSKnowledgeBase.", e);
		}
		
		try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "permanentglossary SET term=? WHERE term=?")) {
			query.setParameter(1, newName);
			query.setParameter(2, term);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't update permanentglossary for rename of term", e);
		}
		
		try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "modifiers SET word=? WHERE word=?")) {
			query.setParameter(1, newName);
			query.setParameter(2, term);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't update modifiers for rename of term", e);
		}
		
		try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "isa SET instance=? WHERE instance=?")) {
			query.setParameter(1, newName);
			query.setParameter(2, term);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't update isa for rename of term", e);
		}
		
		try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "heuristicnouns SET word=? WHERE word=?")) {
			query.setParameter(1, newName);
			query.setParameter(2, term);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't update heuristicnouns for rename of term", e);
		}
		
		try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "discounted SET word=? WHERE word=?")) {
			query.setParameter(1, newName);
			query.setParameter(2, term);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't update discounted for rename of term", e);
		}

		//allwords table not used in parse step
		try(Query query = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "allwords SET word=?, dhword=? WHERE word=?")) {
			query.setParameter(1, newName);
			query.setParameter(2, newName);
			query.setParameter(3, term);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't update allwords for rename of term", e);
		}
		
		try(Query query = new Query("SELECT * FROM " + Configuration.charaparser_databaseName + "." + prefix + "_" + "sentence WHERE sentence RLIKE ?")) {
			query.setParameter(1, "^(.*[^a-zA-Z])?" + term + "([^a-zA-Z].*)?$");	
			ResultSet result = query.execute();
			while(result.next()) {
				int sentid = result.getInt(1);
				String source = result.getString(2);
				String sentence = result.getString(3);
				String originalsent = result.getString(4);
				String lead = result.getString(5);
				String status = result.getString(6);
				String tag = result.getString(7);
				String modifier = result.getString(8);
				String charsegment = result.getString(9);
				
				if(sentence != null)
					sentence = sentence.replaceAll("(?i)\\b" + term + "\\b", newName);
				if(originalsent != null)
					originalsent = originalsent.replaceAll("(?i)\\b" + term + "\\b", newName);
				if(originalsent != null)
					lead = lead.replaceAll("(?i)\\b" + term + "\\b", newName);
				if(tag != null)
					tag = tag.replaceAll("(?i)\\b" + term + "\\b", newName);
				if(modifier != null)
					modifier = modifier.replaceAll("(?i)\\b" + term + "\\b", newName);
				if(charsegment != null)
					charsegment = charsegment.replaceAll("(?i)\b" + term + "\b", newName);
				try(Query updateQuery = new Query("UPDATE " + Configuration.charaparser_databaseName + "." + prefix + "_" + "sentence "
						+ "SET source = ?, sentence = ?, originalsent = ?, lead = ?, status = ?, tag = ?, modifier = ?, charsegment = ? "
						+ "WHERE sentid=?")) {
					updateQuery.setParameter(1, source);
					updateQuery.setParameter(2, sentence);
					updateQuery.setParameter(3, originalsent);
					updateQuery.setParameter(4, lead);
					updateQuery.setParameter(5, status);
					updateQuery.setParameter(6, tag);
					updateQuery.setParameter(7, modifier);
					updateQuery.setParameter(8, charsegment);
					updateQuery.setParameter(9, sentid);
					updateQuery.execute();
				} catch(QueryException e) {
					log(LogLevel.ERROR, "Couldn't update sentence " + sentid + " for rename of term", e);
				}			
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't update sentence for rename of term", e);
		}	
	}

}
