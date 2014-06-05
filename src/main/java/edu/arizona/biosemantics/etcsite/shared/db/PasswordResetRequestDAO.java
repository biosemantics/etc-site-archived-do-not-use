package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PasswordResetRequestDAO {

	private static PasswordResetRequestDAO instance;

	/**
	 * Returns null if no such user exists. 
	 */
	public PasswordResetRequest getRequest(int id) throws ClassNotFoundException, SQLException, IOException{
		PasswordResetRequest request = null;
		
		Query query = new Query("SELECT * FROM passwordresetrequests WHERE uniqueid = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()){
			String authenticationCode = result.getString(2);
			Timestamp timeCreated = result.getTimestamp(3);
			request = new PasswordResetRequest(id, authenticationCode, timeCreated);
		}
		query.close();
		
		return request;
	}
	
	public void removeRequests(int id) throws ClassNotFoundException, SQLException, IOException{
		Query removeRequest = new Query("DELETE FROM `passwordresetrequests` WHERE `uniqueid`=?");
		removeRequest.setParameter(1, id);
		removeRequest.executeAndClose();
	}
	
	public void addRequest(int id, String authenticationCode) throws SQLException, ClassNotFoundException, IOException{
		Query addRequest = new Query("INSERT INTO `passwordresetrequests`(`uniqueid`, `authenticationcode`, `requesttime`) VALUES (?, ?, CURRENT_TIMESTAMP)");
		addRequest.setParameter(1, id);
		addRequest.setParameter(2, authenticationCode);
		addRequest.executeAndClose();
	}
	
	public static PasswordResetRequestDAO getInstance() {
		if (instance == null)
			instance = new PasswordResetRequestDAO();
		return instance;
	}
}