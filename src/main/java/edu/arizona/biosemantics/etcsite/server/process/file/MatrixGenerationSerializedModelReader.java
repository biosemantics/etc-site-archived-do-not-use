package edu.arizona.biosemantics.etcsite.server.process.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

public class MatrixGenerationSerializedModelReader {

	public Model getModel(String filePath) {
		try {
			Model model = unserializeMatrix(new File(filePath));
			return model;
		} catch (ClassNotFoundException | IOException e) {
			log(LogLevel.DEBUG, "Couldn't read model from file: " + e.getMessage());
		}
		return null;
	}
	
	private Model unserializeMatrix(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		try(ObjectInput input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			Model model = (Model)input.readObject();
			return model;
		}
	}

	public boolean isValid(File file) {
		try {
			Model model = unserializeMatrix(file);
			return true;
		} catch (ClassNotFoundException | IOException e) {
			log(LogLevel.DEBUG, "Couldn't read model from file: " + e.getMessage());
		}
		return false;
	}
	
}