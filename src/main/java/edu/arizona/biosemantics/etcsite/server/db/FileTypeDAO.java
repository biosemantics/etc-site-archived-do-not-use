package edu.arizona.biosemantics.etcsite.server.db;

import java.sql.ResultSet;
import java.util.Date;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileType;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;

public class FileTypeDAO {
	
	public FileType getFileType(int id) {
		FileType inputType = null;
		try(Query query = new Query("SELECT * FROM etcsite_filetypes WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				id = result.getInt(1);
				String name = result.getString(2);
				Date created = result.getTimestamp(3);
				inputType = new FileType(id, FileTypeEnum.valueOf(name), created);
			}
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get file type", e);
		}
		return inputType;
	}
	
	public FileType getFileType(String name) {		
		FileType inputType = null;
		try(Query query = new Query("SELECT * FROM etcsite_filetypes WHERE name = ?")) {
			query.setParameter(1, name);
			ResultSet result = query.execute();
			while(result.next()) {
				int id = result.getInt(1);
				name = result.getString(2);
				Date created = result.getTimestamp(3);
				inputType = new FileType(id, FileTypeEnum.valueOf(name), created);
			}
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get file type of name", e);
		}
		return inputType;
	}
	
}
