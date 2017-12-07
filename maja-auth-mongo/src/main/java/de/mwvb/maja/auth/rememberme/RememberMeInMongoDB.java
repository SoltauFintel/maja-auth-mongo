package de.mwvb.maja.auth.rememberme;

import org.pmw.tinylog.Logger;

import com.google.inject.Inject;

import de.mwvb.maja.mongo.AbstractDAO;
import de.mwvb.maja.mongo.MongoPlugin;
import de.mwvb.maja.web.AppConfig;
import de.mwvb.maja.web.Broadcaster;
import spark.Request;
import spark.Response;

public class RememberMeInMongoDB implements RememberMeFeature {
	// https://stackoverflow.com/a/5083809/3478021
	
	@Inject
	private AppConfig config;
	@Inject
	private KnownUserDAO dao;
	@Inject
	private Broadcaster broadcaster;
	private Cookie cookie;
	
	@Override
	public void install() {
		broadcaster.broadcast(MongoPlugin.ENTITY_CLASS, KnownUser.class.getName());

		cookie = new Cookie("KNOWNUSERID" + getAppName());
	}

	private String getAppName() {
		String appName = config.get("app.name");
		if (appName == null) {
			appName = "";
		} else {
			for (int i = 0; i < appName.length(); i++) {
				char c = appName.charAt(i);
				if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_')) {
					throw new RuntimeException("Illegal char '" + c + "' in parameter 'app.name'. Please fix config.");
				}
			}
		}
		return appName;
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
			
			cookie.set(knownUser.getId(), res, "remember-me");
		} else {
			cookie.remove(res);
			dao.delete(userId);
		}
	}
	
	@Override
	public void forget(Response res, String userId) {
		cookie.remove(res);
		if (userId != null) {
			dao.delete(userId);
		}
	}

	@Override
	public IKnownUser getUserIfKnown(Request req, Response res) {
		String id = cookie.get(req);
		if (id == null) {
			return null;
		}
		KnownUser ku = dao.get(id);
		if (ku == null) {
			cookie.remove(res);
		} else {
			Logger.debug("Remembered user: " + ku.getUser() + " (" + ku.getUserId() + ")");
			cookie.extendLifeTime(ku.getId(), res);
		}
		return ku;
	}

	@Override
	public void printInfo() {
	}
}
