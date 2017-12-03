package de.mwvb.maja.auth.rememberme;

import de.mwvb.maja.auth.AuthPlugin;

/**
 * This is a Auth plugin with Remember-me-function. For that it uses the MongoDB database. 
 */
public class AuthPluginWithRememberMe extends AuthPlugin {

	@Override
	protected Class<? extends RememberMeFeature> getRememberMeClass() {
		return RememberMeInMongoDB.class;
	}
}
