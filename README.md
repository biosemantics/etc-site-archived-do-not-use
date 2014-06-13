etc-site
============

Contribution
----------

### Setup
If you want to contribute, the source is built using Maven and Google Web Toolkit.
In Eclipse you can therefore use:
* m2e - Maven Integration for Eclipse (e.g. for Juno version: http://download.eclipse.org/releases/juno)
* Google Plugin for Eclipse (https://developers.google.com/eclipse/)

and 

1. configure your Eclipse project to be a Maven Project and to use Google Web Toolkit (2.6)
2. run `mvn package` to set up `/src/main/webapp/` files for GWT dev mode. Run again for changes in the directory
3. Import `setupDB.sql` dump to a database dedicated for the project
6. Create `config.properties` at `/src/main/resources` according to `config.properties.template` and your environment

Please [configure your git](http://git-scm.com/book/en/Customizing-Git-Git-Configuration) for this repository as:
* `core.autocrlf` true if you are on Windows 
* or `core.autocrlf input` if you are on a Unix-type OS

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

### Run Dev Mode

#### Class
com.google.gwt.dev.DevMode

#### Arguments
-remoteUI "${gwt_remote_ui_server_port}:${unique_id}" -startupUrl index.html -logLevel INFO -codeServerPort auto -port auto -war **full_path_to_your_git_dir**\etc-site\target\etc-site-0.0.1-SNAPSHOT edu.arizona.biosemantics.etcsite.EtcSite edu.arizona.biosemantics.otolite.OTOLite_part2

