package edu.arizona.biosemantics.etcsite.server.rpc.file.search;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.process.file.XMLFileFormatter;
import edu.arizona.biosemantics.etcsite.server.rpc.file.FileService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.access.FileAccessService;
import edu.arizona.biosemantics.etcsite.shared.model.file.search.ElementAttributeValuesSearch;
import edu.arizona.biosemantics.etcsite.shared.model.file.search.ElementValuesSearch;
import edu.arizona.biosemantics.etcsite.shared.model.file.search.ElementsSearch;
import edu.arizona.biosemantics.etcsite.shared.model.file.search.NumericalsSearch;
import edu.arizona.biosemantics.etcsite.shared.model.file.search.Search;
import edu.arizona.biosemantics.etcsite.shared.model.file.search.SearchResult;
import edu.arizona.biosemantics.etcsite.shared.model.file.search.XPathSearch;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.GetFileContentFailedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.PermissionDeniedException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.search.IFileSearchService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.search.InvalidSearchException;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.search.SearchException;

public class FileSearchService extends RemoteServiceServlet implements IFileSearchService {

	private static final long serialVersionUID = -6550101274590336948L;

	private IFileAccessService fileAccessService;
	private IFileService fileService;
	private XMLFileFormatter xmlFileFormatter;
	
	@Inject
	public FileSearchService(IFileService fileService, IFileAccessService fileAccessService, 
			XMLFileFormatter xmlFileFormatter) {
		this.fileService = fileService;
		this.fileAccessService = fileAccessService;
		this.xmlFileFormatter = xmlFileFormatter;
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public List<SearchResult> search(AuthenticationToken authenticationToken, String filePath, Search search) 
				throws InvalidSearchException, PermissionDeniedException, GetFileContentFailedException, SearchException {
		if(search == null)
			throw new InvalidSearchException();
		
		Map<String, Set<String>> capturedMatchFiles = new HashMap<String, Set<String>>();
		List<String> files = fileService.getDirectoriesFiles(authenticationToken, filePath);
		
		for(String file : files) {
			String content = fileAccessService.getFileContent(authenticationToken, filePath + File.separator + file);
				NodeList nodeList = this.searchNodeList(authenticationToken, content, search);
				for(int i=0; i<nodeList.getLength(); i++) {
		    		Node node = nodeList.item(i);
		    		
	    		// node can't be given to search as its used client-side. gwt's node implementation cannot be used with LSSerializer, 
	    		// no other way known to pretty print xml with gwt client
	    		String capturedMatch = null;
	    		if(search instanceof ElementsSearch) {
	    			capturedMatch = node.getNodeName();
	    		} 
    			if(search instanceof ElementAttributeValuesSearch) {
    				ElementAttributeValuesSearch elementAttributeValuesSearch = (ElementAttributeValuesSearch)search;
    				NamedNodeMap attributes = node.getAttributes();
    				for(int j=0; j<attributes.getLength(); j++) {
						Node attribute = attributes.item(j);
						if(attribute.getNodeName().equals(elementAttributeValuesSearch.getAttribute())) {
							capturedMatch = attribute.getNodeValue();
						}
    				}
	    		} 
    			if(search instanceof ElementValuesSearch) {
    				capturedMatch = node.getNodeValue();
	    		} 
				if(search instanceof NumericalsSearch) {
					StringBuilder resultBuilder = new StringBuilder();
					NamedNodeMap attributes = node.getAttributes();
					for(int j=0; j<attributes.getLength(); j++) {
						Node attribute = attributes.item(j);
						String attributeValue = attribute.getNodeValue();
						if(attributeValue.matches(((NumericalsSearch)search).getNumericalExpression())) {
							resultBuilder.append(attributeValue + "; ");
						}
					}
					capturedMatch = resultBuilder.toString();
					if(!capturedMatch.isEmpty())
						capturedMatch = capturedMatch.substring(0, capturedMatch.length() - 2);
				} 
				if(search instanceof XPathSearch) {
					capturedMatch = xmlFileFormatter.format(node, false);
				} 
		    	if(capturedMatch == null)
		    		capturedMatch = xmlFileFormatter.format(node, false);
		    	if(capturedMatch != null) {
			    	if(!capturedMatchFiles.containsKey(capturedMatch))
			    		capturedMatchFiles.put(capturedMatch, new HashSet<String>());
			    	capturedMatchFiles.get(capturedMatch).add(filePath + File.separator + file);
				}
		    }
			/*int occurrences = nodeList.getLength();
			if(occurrences > 0) {
				result.add(new SearchResult(inputDirectory + File.separator + file, occurrences));
			}*/
		}
			
		List<SearchResult> searchResult = new LinkedList<SearchResult>();
		for(String capturedMatch : capturedMatchFiles.keySet()) {
			searchResult.add(new SearchResult(capturedMatch, capturedMatchFiles.get(capturedMatch)));
		}
		Collections.sort(searchResult);
		return searchResult;
	}
	
	private NodeList searchNodeList(AuthenticationToken authenticationToken, String content, Search search) throws SearchException {
		try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes("UTF-8"))) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(byteArrayInputStream);
		    XPath xPath =  XPathFactory.newInstance(Configuration.xPathObjectModel).newXPath();
	
		    XPathExpression xpathExpression = xPath.compile(search.getXPath());
		    NodeList nodeList = (NodeList) xpathExpression.evaluate(document, XPathConstants.NODESET);
		    return nodeList;
		} catch (UnsupportedEncodingException e) {
			log(LogLevel.ERROR, "Encoding not supported", e);
			throw new SearchException();
		} catch (IOException e) {
			log(LogLevel.ERROR, "Problem closing or parsing stream", e);
			throw new SearchException();
		} catch (XPathExpressionException e) {
			log(LogLevel.ERROR, "Problem with XPath expression", e);
			throw new SearchException();
		} catch (SAXException e) {
			log(LogLevel.ERROR, "Sax problem", e);
			throw new SearchException();
		} catch (XPathFactoryConfigurationException e) {
			log(LogLevel.ERROR, "Problem with XPath factory configuration", e);
			throw new SearchException();
		} catch (ParserConfigurationException e) {
			log(LogLevel.ERROR, "Problem with parser configuration", e);
			throw new SearchException();
		}
	}
	
	@Override
	public String getFileContentSearched(AuthenticationToken authenticationToken, String filePath, Search search) 
			throws InvalidSearchException, PermissionDeniedException, GetFileContentFailedException, SearchException {
		if(search == null)
			throw new InvalidSearchException();
		
		String content = fileAccessService.getFileContent(authenticationToken, filePath);
		NodeList nodeList = this.searchNodeList(authenticationToken, content, search);
		String highlighted = xmlFileFormatter.formatAndHiglight(content, nodeList);
		return highlighted;
	}
}
