package edu.arizona.biosemantics.etcsite.server.db;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.Task;

public class TasksOutputFilesDAO {

	public void addOutput(Task task, String file) {
		try(Query addOutput = new Query("INSERT INTO etcsite_tasksoutputfiles (file, task) VALUES (?, ?)")) {
			addOutput.setParameter(1, file);
			addOutput.setParameter(2, task.getId());
			addOutput.execute();
			ResultSet generatedKeys = addOutput.getGeneratedKeys();
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't insert tasks output files", e);
		}
	}
	
	public List<String> getOutputs(Task task) {
		List<String> result = new LinkedList<String>();
		try(Query addOutput = new Query("SELECT file FROM etcsite_tasksoutputfiles WHERE task = ?")) {
			addOutput.setParameter(1, task.getId());
			ResultSet resultSet = addOutput.execute();
			while(resultSet.next()) {
				String file = resultSet.getString(1);
				result.add(file);
			}
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get tasks output files", e);
		}
		return result;
	}
	
	public void removeOutput(Task task, String file) {
		try(Query addOutput = new Query("DELETE FROM etcsite_tasksoutputfiles WHERE file = ? AND task = ?")) {
			addOutput.setParameter(1, file);
			addOutput.setParameter(2, task.getId());
			addOutput.execute();
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't remove tasks output file", e);
		}
	}
	
	public void removeOutputs(Task task) {
		try(Query addOutput = new Query("DELETE FROM etcsite_tasksoutputfiles WHERE task = ?")) {
			addOutput.setParameter(1, task.getId());
			addOutput.execute();
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't remove tasks output files", e);
		}
	}
	
	
}
