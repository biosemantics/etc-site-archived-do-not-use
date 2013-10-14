package edu.arizona.sirls.etc.site.server.rpc;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gwt.dev.util.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileSearchService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.file.XMLFileFormatter;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.SearchResult;

public class FileSearchService extends RemoteServiceServlet implements IFileSearchService {

	private static final long serialVersionUID = -6550101274590336948L;

	private IFileAccessService fileAccessService = new FileAccessService();
	private IFileService fileService = new FileService();
	private IAuthenticationService authenticationService = new AuthenticationService();
	
	@Override
	public List<SearchResult> search(AuthenticationToken authenticationToken, String inputDirectory, Search search) {
		List<SearchResult> result = new LinkedList<SearchResult>();
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			List<String> files = fileService.getDirectoriesFiles(authenticationToken, inputDirectory);
			for(String file : files) {
				NodeList nodeList = this.searchNodeList(authenticationToken, inputDirectory + "//" + file, search);
			    if(nodeList != null) {
					for(int i=0; i<nodeList.getLength(); i++) {
				    	Node node = nodeList.item(i);
				    	//System.out.println(node.toString());
				    }
					int occurrences = nodeList.getLength();
					if(occurrences > 0) {
						result.add(new SearchResult(inputDirectory + "//" + file, occurrences));
					}
			    }
			}
		}
		Lists.sort(result);
		return result;
	}
	
	public NodeList searchNodeList(AuthenticationToken authenticationToken, String target, Search search) {
		if(authenticationService.isValidSession(authenticationToken).getResult()) { 
			if(search != null) {
				String fileContent = fileAccessService.getFileContent(authenticationToken, target);
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				try {
					DocumentBuilder builder = builderFactory.newDocumentBuilder();
					Document document = builder.parse(new ByteArrayInputStream(fileContent.getBytes("UTF-8")));
				    XPath xPath =  XPathFactory.newInstance(Configuration.xPathObjectModel).newXPath();
				    try {
					    XPathExpression xpathExpression = xPath.compile(search.getXPath());
					    NodeList nodeList = (NodeList) xpathExpression.evaluate(document, XPathConstants.NODESET);
					    return nodeList;
				    } catch(Exception e) {
				    	e.printStackTrace();
				    }
				} catch(Exception e) { 
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	@Override
	public String getFileContentSearched(AuthenticationToken authenticationToken, String target, Search search) {
		String content = fileAccessService.getFileContent(authenticationToken, target);
		NodeList nodeList = this.searchNodeList(authenticationToken, target, search);
		return new XMLFileFormatter().formatAndHiglight(content, nodeList);
	}
}
