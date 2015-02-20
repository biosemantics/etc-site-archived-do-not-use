#### Setting up Google Developers project
In order to use the 'Sign in with Google' functionality, a Google Developers project must be set up. To set up the project,

1. Go to https://console.developers.google.com
2. Log into your Google account.
3. Go to 'Projects' --> 'Create Project'
4. Choose project name and id and create the project. 
5. In the project page, go to 'APIs & auth' --> 'Credentials'
6. Under OAuth, click 'Create new Client ID' and fill in the information. Application type is 'Web application'. Authorized Redirect URI are the pages that Google could possibly redirect to after authentication. (e.g. http://etc.cs.umb.edu/etcsite for production and http://etc-dev.cs.umb.edu/etcsite for development. http://127.0.0.1:8888/index.html?gwt.codesvr=127.0.0.1:9997 might be added for local use.) Multiple URIs can be specified. No trailing slash. 
7. Click on 'APIs & auth' --> 'Consent Screen' to change product name and add logo if desired. 

Then tell etc-site where to find the Google Developers project: 

1. In config.properties, copy the Client ID value from the Google Developers project page to the google_client id field. (e.g. a2g389asdj3ksa023gj8ke.apps.googleusercontent.com)
2. In config.properties, specify the page Google authentication should redirect to in the google_redirect_URI field. Must be one of the Redirect URIs specified in the Google Developers project page. (e.g. http://etc.cs.umb.edu/etcsite) 
