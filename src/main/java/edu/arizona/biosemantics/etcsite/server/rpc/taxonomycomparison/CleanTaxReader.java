package edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.process.file.IContentValidator;

public class CleanTaxReader implements IContentValidator {

	private String firstLinePatternString = "taxonomy (\\w+) (\\w+) (\\w+)";
	private Pattern firstLinePattern = Pattern.compile(firstLinePatternString);
	private String invalidMessage = "";
	
	public String getRootName(File file) throws Exception {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
			String line = null;
			int lineNumber = 0;
			
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if(!isCommentLine(line)) {
					if(lineNumber == 0) {
					    Matcher matcher = firstLinePattern.matcher(line);
						if(matcher.matches()) {
							String id = matcher.group(1);
							String year = matcher.group(2);
							String author = matcher.group(3);
							return "sec " + author + " " + year + " (" + id + ")";
						} else {
							throw new Exception("Illegal format");
						}
					}
				} else {
					continue;
				}
			}
			throw new Exception("Illegal format");
		}
	}

	public boolean isValid(File file) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		String content = FileUtils.readFileToString(file, "UTF-8");
		return this.validate(content);
	}
	
	private String getParentNode(String line) {
		if(line.startsWith("(") && line.endsWith(")")) {
			line = line.substring(1, line.length() - 1).trim();
			String[] nodes = line.split("\\s");
			return nodes[0];
		}
		return null;
	}

	private List<String> getNewNodeCandidates(String line) {
		List<String> newNodes = new LinkedList<String>();
		if(line.startsWith("(") && line.endsWith(")")) {
			line = line.substring(1, line.length() - 1).trim();
			String[] nodes = line.split("(\\s+)");
			if(nodes.length > 1) {
				for(int i=1; i<nodes.length; i++) {
					newNodes.add(nodes[i]);
				}
			}
		}
		return newNodes;
	}

	private boolean isValidLine(String line) {
		if(line.startsWith("(") && line.endsWith(")")) {
			line = line.substring(1, line.length() - 1).trim();
			if(line.length() == 0)
				return false;
			return true;
		}
		return false;
	}

	private boolean isValidFirstLine(String line) {
	    Matcher matcher = firstLinePattern.matcher(line);
		if(matcher.matches()) {
			String id = matcher.group(1);
			String year = matcher.group(2);
			String author = matcher.group(3);
			return true;
		}
		return false;
	}

	private boolean isCommentLine(String fileLine) {
		return fileLine.startsWith("#");
	}

	public static void main(String[] args) throws Exception {
		CleanTaxReader r = new CleanTaxReader();
		System.out.println(r.isValid(new File("FNATaxonomy.txt")));
	}

	@Override
	public boolean validate(String input) {
		//#comments, see reader i already have in align?
		try(BufferedReader br = new BufferedReader(new StringReader(input))) {
			String line = null;
			int lineNumber = 0;
			int fileLineNumber = 0;
			
			Set<String> nodes = new HashSet<String>();
			Set<String> usedParentNodes = new HashSet<String>();
			while ((line = br.readLine()) != null) {
				fileLineNumber++;
				line = line.trim();
				if(!isCommentLine(line)) {
					if(lineNumber == 0) {
						if(!isValidFirstLine(line)) {
							invalidMessage = "First line has invalid format.";
							return false;
						}
					} else {
						if(!isValidLine(line)) {
							invalidMessage = "Line " + fileLineNumber + " has invalid format.";
							return false;
						}
						else {
							String parentNode = getParentNode(line);
							List<String> newNodeCandidates = getNewNodeCandidates(line);
							Set<String> newNodes = new HashSet<String>(newNodeCandidates);
							
							if(lineNumber > 1) {
								if(!nodes.contains(parentNode)) {
									invalidMessage = "Line " + fileLineNumber + " contains an undefined parent node.";
									return false; //parent was never established as node
								}
								if(usedParentNodes.contains(parentNode)) {
									invalidMessage = "Line " + fileLineNumber + " uses a parent node that has already defined children.";
									return false; //already used as parent in other line
								}
								if(newNodeCandidates.size() != newNodes.size()) {
									invalidMessage = "Line " + fileLineNumber + " contains duplicate nodes.";
									return false; //duplicate new nodes
								}
								for(String newNode : newNodes)
									if(nodes.contains(newNode)) {
										invalidMessage = "Line " + fileLineNumber + " contains a node that was already defined.";
										return false; //duplicate node in established nodes
									}
							}
							nodes.addAll(newNodes);
							usedParentNodes.add(parentNode);
						}
					}
					lineNumber++;
				} else 
					continue;
			}
			if(lineNumber >= 2) //at least one line with a root node has to be defined
				return true;
			else {
				invalidMessage = "At least a root node has to be defined.";
				return false;
			}
		} catch(Exception e) {
			invalidMessage = "Error reading content.";
			log(LogLevel.ERROR, "Error reading from string.", e);
			return false;
		}
	}

	@Override
	public String getInvalidMessage() {
		return invalidMessage;
	}
	
}