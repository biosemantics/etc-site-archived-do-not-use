package edu.arizona.biosemantics.etcsite.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.PasswordResetRequest;
import edu.arizona.biosemantics.etcsite.shared.model.User;

public class PasswordResetRequestDAO {

	public PasswordResetRequest get(User user) {
		PasswordResetRequest request = null;
		try(Query query = new Query("SELECT * FROM etcsite_passwordresetrequests WHERE user = ?")) {
			query.setParameter(1, user.getId());
			ResultSet result = query.execute();
			while(result.next()){
				request = createPasswordResetRequest(result);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get password reset request", e);
		}
		return request;
	}
	
	private PasswordResetRequest createPasswordResetRequest(ResultSet result) throws SQLException {
		int user = result.getInt(1);
		String authenticationCode = result.getString(2);
		Date requestTime = result.getTimestamp(3);
		return new PasswordResetRequest(user, authenticationCode, requestTime);
	}

	public void remove(User user) {
		try(Query removeRequest = new Query("DELETE FROM `etcsite_passwordresetrequests` WHERE `user`=?")) {
			removeRequest.setParameter(1, user.getId());
			removeRequest.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't remove password reset request", e);
		}
	}
	
	public void insert(User user, String authenticationCode) {
		try(Query addRequest = new Query("INSERT INTO `etcsite_passwordresetrequests`(`user`, `authenticationcode`) VALUES (?, ?)")) {
			addRequest.setParameter(1, user.getId());
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
