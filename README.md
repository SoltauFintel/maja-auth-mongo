# maja-auth-mongo

Adds MongoDB support and the remember-me feature to maja-auth.

The remember-me feature is for remembering the user login by the browser. So the user must not log in every time.

## How to use

With Gradle:

    // in dependencies:
	compile 'com.github.SoltauFintel:maja-auth-mongo:0.2.0'
	
	// in repositories:
	maven { url 'https://jitpack.io' }

Use AuthPluginWithRememberMe instead of AuthPlugin to use the rememeber-me feature.
The app config should contain the 'app.name' parameter.
Please add KnownUser.class to the entity class list of Database.open().
