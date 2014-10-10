package edu.arizona.biosemantics.etcsite.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.eclipse.persistence.exceptions.QueryException;

import edu.arizona.biosemantics.etcsite.shared.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.PasswordResetRequest;

public class PasswordResetRequestDAO {

	/**
	 * Returns null if no such user exists. 
	 */
	public PasswordResetRequest getRequest(int user) {
		PasswordResetRequest request = null;
		
		try(Query query = new Query("SELECT * FROM etcsite_passwordresetrequests WHERE user = ?")) {
			query.setParameter(1, user);
			ResultSet result = query.execute();
			while(result.next()){
				String authenticationCode = result.getString(2);
				Timestamp timeCreated = result.getTimestamp(3);
				request = new PasswordResetRequest(user, authenticationCode, timeCreated);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get password reset request", e);
		}
		
		return request;
	}
	
	public void removeRequests(int user) {
		try(Query removeRequest = new Query("DELETE FROM `etcsite_passwordresetrequests` WHERE `user`=?")) {
			removeRequest.setParameter(1, user);
			removeRequest.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't remove password reset request", e);
		}
	}
	
	public void addRequest(int user, String authenticationCode) {
		try(Query addRequest = new Query("INSERT INTO `etcsite_passwordresetrequests`(`user`, `authenticationcode`) VALUES (?, ?)")) {
			addRequest.setParameter(1, user);
			addRequest.setParameter(2, authenticationCode);
			addRequest.execute();
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't insert password reset request", e);
		}
	}
	
	public void cleanup() {
		try(Query deleteAll = new Query("DELETE FROM `etcsite_passwordresetrequests` WHERE 1")) {
			deleteAll.execute();
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't cleanup password reset requests", e);
		}
	}
}
