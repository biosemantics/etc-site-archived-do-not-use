package edu.arizona.biosemantics.etcsite.server.rpc.ontologyconstruction;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.jdom2.JDOMException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.Zipper;
import edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup.MarkupResultReader;
import edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup.MarkupResultReader.BiologicalEntity;
import edu.arizona.biosemantics.etcsite.shared.model.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.Description;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.GetFileContentFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.ontologize.IOntologyConstructionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupService;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.SemanticMarkupException;
import edu.arizona.biosemantics.oto2.ontologize.server.rest.client.Client;

public class OntologyConstructionService extends RemoteServiceServlet implements IOntologyConstructionService {

	@Override
	public Task ontologize(AuthenticationToken token, Task task) throws SemanticMarkupException {
		/*SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		String input = config.getInput();
		try {		
			try(Client client = new Client(Configuration.ontologizeUrl)) {
				client.open();
				MarkupResultReader reader = new MarkupResultReader();
				String charaParserOutputDirectory = Configuration.charaparser_tempFileBase + File.separator + task.getId() + File.separator + "out";
				File charaparserOutputFile = new File(charaParserOutputDirectory);
				List<MarkupResultReader.BiologicalEntity> structures = reader.getBiologicalEntities(charaparserOutputFile);
				List<MarkupResultReader.Character> characters = reader.getCharacters(charaparserOutputFile, false);
				List<MarkupResultReader.Character> rangeValueCharacters = reader.getRangeValueCharacters(charaparserOutputFile, false);
				
				List<edu.arizona.biosemantics.oto2.ontologize.shared.model.Term> terms = 
						new LinkedList<edu.arizona.biosemantics.oto2.ontologize.shared.model.Term>();					
				Set<String> containedStructures = new HashSet<String>();
				for(BiologicalEntity structure : structures) {
					if(!containedStructures.contains(structure.getName())) {
						terms.add(new edu.arizona.biosemantics.oto2.ontologize.shared.model.Term(
								structure.getName(), "", "/structure", "structure"));
						containedStructures.add(structure.getName());
					}
				}
				Set<String> containedCharacters = new HashSet<String>();
				for(MarkupResultReader.Character character : characters) {
					//filter comparison values such as "wider than long". "twice of leaf"
					if(character.getValue().split("\\W+").length < 3) {
						if(!containedCharacters.contains(character.getValue() + character.getCategory())) {
							terms.add(new edu.arizona.biosemantics.oto2.ontologize.shared.model.Term(
									character.getValue(), "", "/character/" + character.getCategory(), character.getCategory()));
							containedCharacters.add(character.getValue() + character.getCategory());
						}
					}
				}
				edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection collection = 
						new edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection(
								task.getName(), 
								edu.arizona.biosemantics.common.biology.TaxonGroup.valueOf(config.getTaxonGroup().getName().toUpperCase()),
								String.valueOf(task.getId()), 
								terms);
				collection = client.post(collection).get();

				List<edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection> collections = 
						daoManager.getUserDAO().getOntologizeCollections(token.getUserId());
				ontologizeCollectionService.insertLinkedCollections(collection, collections);
				daoManager.getUserDAO().insertOntologizeCollection(token.getUserId(), collection.getId(), collection.getSecret());
				
				config.setOntologizeUploadId(collection.getId());
				config.setOntologizeSecret(collection.getSecret());
				daoManager.getSemanticMarkupConfigurationDAO().updateSemanticMarkupConfiguration(config);
							
				List<edu.arizona.biosemantics.oto2.ontologize.shared.model.Context> contexts = 
						new LinkedList<edu.arizona.biosemantics.oto2.ontologize.shared.model.Context>();
				List<String> files = new LinkedList<String>();
				try {
					files = fileService.getDirectoriesFiles(token, input);
				} catch (PermissionDeniedException e) {
					throw new SemanticMarkupException(task);
				}
				for(String file : files) {
					List<Description> descriptions = getDescriptions(token, input + File.separator + file);
					for(Description description : descriptions) {
						try {
							contexts.add(new edu.arizona.biosemantics.oto2.ontologize.shared.model.Context(collection.getId(), 
									getTaxonIdentification(token, input + File.separator + file), 
									description.getContent()));
						} catch (PermissionDeniedException | GetFileContentFailedException e) {
							throw new SemanticMarkupException(task);
						}
					}
				}
				try {
					List<edu.arizona.biosemantics.oto2.ontologize.shared.model.Context> result = 
							client.post(collection.getId(), collection.getSecret(), contexts).get();
				} catch (InterruptedException | ExecutionException e) {
					throw new SemanticMarkupException(task);
				}
				
				return task;
			} catch (InterruptedException | ExecutionException | JDOMException | IOException e) {
				log(LogLevel.ERROR, "Couldn't prepare ontologize", e);
				throw new SemanticMarkupException();
			}
		
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get local ontologies for collection", e);
			throw new SemanticMarkupException();
		}*/
		return null;
	}

	@Override
	public String downloadOntologize(AuthenticationToken token, Task task) throws SemanticMarkupException {
		/*final SemanticMarkupConfiguration config = getSemanticMarkupConfiguration(task);
		int uploadId = config.getOntologizeUploadId();
		String secret = config.getOntologizeSecret();
		
		edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection collection;
		try {
			collection = ontologizeCollectionService.get(uploadId, secret);
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't get oto collection", e);
			throw new SemanticMarkupException(task);
		}
		
		String zipSource = Configuration.compressedFileBase + File.separator + token.getUserId() + File.separator + "ontologize" + 
				File.separator + task.getId() + File.separator + task.getName() + "_ontologies";
		File zipSourceFile = new File(zipSource);
		try {
			FileUtils.deleteDirectory(zipSourceFile);
		} catch (IOException e) {
			log(LogLevel.ERROR, "Couldn't clean/remove directory to zip", e);
			throw new SemanticMarkupException(task);
		}
		zipSourceFile.mkdirs();
		
		String source = edu.arizona.biosemantics.oto2.ontologize.server.Configuration.collectionOntologyDirectory + 
				File.separator + uploadId;
		File sourceFile = new File(source);
		for(File file : sourceFile.listFiles()) {
			if(file.isDirectory()) {
				try {
					
					FileUtils.copyDirectoryToDirectory(file, zipSourceFile);
				} catch (IOException e) {
					log(LogLevel.ERROR, "Couldn't copy ontology file", e);
					throw new SemanticMarkupException(task);
				}
			}
		}
		
		String zipFilePath = Configuration.compressedFileBase + File.separator + token.getUserId() + File.separator + "ontologize" + 
				File.separator + task.getId() + File.separator + task.getName() + "_ontologies.zip";
		Zipper zipper = new Zipper();
		zipFilePath = zipper.zip(zipSource, zipFilePath);
		if(zipFilePath != null)
			return zipFilePath;
		throw new SemanticMarkupException("Saving failed");*/
		return null;
	}

	@Override
	public Task getLatestResumable(AuthenticationToken authenticationToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancel(AuthenticationToken authenticationToken, Task task)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
