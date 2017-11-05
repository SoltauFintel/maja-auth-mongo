package de.mwvb.maja.auth.rememberme;

import org.pmw.tinylog.Logger;

import de.mwvb.maja.mongo.AbstractDAO;
import de.mwvb.maja.mongo.Database;
import spark.Request;
import spark.Response;

public class RememberMeInMongoDB implements RememberMeFeature {
	// https://stackoverflow.com/a/5083809/3478021
	private static final String COOKIE_NAME = "KNOWNUSERID";
	private final KnownUserDAO dao;
	
	public RememberMeInMongoDB(Database database) {
		dao = new KnownUserDAO(database);
	}
	
	@Override
	public void rememberMe(boolean rememberMeWanted, Response res, String user, String userId) {
		if (rememberMeWanted) {
			KnownUser knownUser = new KnownUser();
			knownUser.setCreatedAt(new java.util.Date(System.currentTimeMillis()));
			knownUser.setId(AbstractDAO.genId());
			knownUser.setUser(user);
			knownUser.setUserId(userId);
			dao.save(knownUser);
			
			setCookie(res, knownUser);
		} else {
			res.removeCookie(COOKIE_NAME);
			dao.delete(userId);
		}
	}
	
	@Override
	public void forget(Response res, String userId) {
		res.removeCookie(COOKIE_NAME);
		if (userId != null) {
			dao.delete(userId);
		}
	}

	@Override
	public IKnownUser getUserIfKnown(Request req, Response res) {
		String id = req.cookie(COOKIE_NAME);
		if (id == null) {
			return null;
		}
		KnownUser ku = dao.get(id);
		if (ku == null) {
			res.removeCookie(COOKIE_NAME);
		} else {
			Logger.debug("Remembered user: " + ku.getUser() + " (" + ku.getUserId() + ")");
			setCookie(res, ku); // Extends cookie life time.
		}
		return ku;
	}

	private void setCookie(Response res, KnownUser ku) {
		res.cookie("", "/", COOKIE_NAME, ku.getId(), 60 * 60 * 24 * 30 /* 30 days */, false, false);
	}
}
