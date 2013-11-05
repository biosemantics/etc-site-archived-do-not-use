package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import edu.arizona.sirls.etc.site.shared.rpc.InputTypeEnum;

public class InputTypeDAO {

	private static InputTypeDAO instance;
	
	public InputType getInputType(int id) throws SQLException, ClassNotFoundException, IOException {
		InputType inputType = null;
		Query query = new Query("SELECT * FROM inputtypes WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()) {
			id = result.getInt(1);
			String name = result.getString(2);
			Date created = result.getTimestamp(3);
			inputType = new InputType(id, InputTypeEnum.valueOf(name), created);
		}
		query.close();
		return inputType;
	}
	
	public InputType getInputType(String name) throws SQLException, ClassNotFoundException, IOException {		
		InputType inputType = null;
		Query query = new Query("SELECT * FROM inputtypes WHERE name = ?");
		query.setParameter(1, name);
		ResultSet result = query.execute();
		while(result.next()) {
			int id = result.getInt(1);
			name = result.getString(2);
			Date created = result.getTimestamp(3);
			inputType = new InputType(id, InputTypeEnum.valueOf(name), created);
		}
		query.close();
		return inputType;
	}
		
	public static InputTypeDAO getInstance() {
		if(instance == null)
			instance = new InputTypeDAO();
		return instance;
	}
	
}
