package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.eclipse.persistence.exceptions.QueryException;

public class PasswordResetRequestDAO {

	private static PasswordResetRequestDAO instance;

	/**
	 * Returns null if no such user exists. 
	 */
	public PasswordResetRequest getRequest(int user) {
		PasswordResetRequest request = null;
		
		try(Query query = new Query("SELECT * FROM passwordresetrequests WHERE user = ?")) {
			query.setParameter(1, user);
			ResultSet result = query.execute();
			while(result.next()){
				String authenticationCode = result.getString(2);
				Timestamp timeCreated = result.getTimestamp(3);
				request = new PasswordResetRequest(user, authenticationCode, timeCreated);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return request;
	}
	
	public void removeRequests(int user) {
		try(Query removeRequest = new Query("DELETE FROM `passwordresetrequests` WHERE `user`=?")) {
			removeRequest.setParameter(1, user);
			removeRequest.execute();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addRequest(int user, String authenticationCode) {
		try(Query addRequest = new Query("INSERT INTO `passwordresetrequests`(`user`, `authenticationcode`) VALUES (?, ?)")) {
			addRequest.setParameter(1, user);
			addRequest.setParameter(2, authenticationCode);
			addRequest.execute();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static PasswordResetRequestDAO getInstance() {
		if (instance == null){
			instance = new PasswordResetRequestDAO();
			
			//delete all 'old' password reset requests. Should only happen once per server restart. 
			try(Query deleteAll = new Query("DELETE FROM `passwordresetrequests` WHERE 1")) {
				deleteAll.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
}
