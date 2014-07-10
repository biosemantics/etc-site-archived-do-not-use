package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import edu.arizona.biosemantics.etcsite.shared.db.Query.QueryException;
import edu.arizona.biosemantics.etcsite.shared.file.FileTypeEnum;

public class FileTypeDAO {

	private static FileTypeDAO instance;
	
	public FileType getFileType(int id) {
		FileType inputType = null;
		try(Query query = new Query("SELECT * FROM filetypes WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				id = result.getInt(1);
				String name = result.getString(2);
				Date created = result.getTimestamp(3);
				inputType = new FileType(id, FileTypeEnum.valueOf(name), created);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return inputType;
	}
	
	public FileType getFileType(String name) {		
		FileType inputType = null;
		try(Query query = new Query("SELECT * FROM filetypes WHERE name = ?")) {
			query.setParameter(1, name);
			ResultSet result = query.execute();
			while(result.next()) {
				int id = result.getInt(1);
				name = result.getString(2);
				Date created = result.getTimestamp(3);
				inputType = new FileType(id, FileTypeEnum.valueOf(name), created);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return inputType;
	}
		
	public static FileTypeDAO getInstance() {
		if(instance == null)
			instance = new FileTypeDAO();
		return instance;
	}
	
}
