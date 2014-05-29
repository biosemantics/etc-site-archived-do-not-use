etc-site
============

Contribution


Contribution
----------

### Setup
If you want to contribute, the source is built using Maven and Google Web Toolkit.
In Eclipse you can therefore use:
* m2e - Maven Integration for Eclipse (e.g. for Juno version: http://download.eclipse.org/releases/juno)
* Google Plugin for Eclipse (https://developers.google.com/eclipse/)

and 
(1) configure your Eclipse project to be a Maven Project and to use Google Web Toolkit (2.6).
(2) run mvn package to set up /src/main/webapp/ files for GWT dev mode. Run again for changes in the directory.

### Run Dev Mode:

Class:
com.google.gwt.dev.DevMode

Arguments:
-remoteUI "${gwt_remote_ui_server_port}:${unique_id}" -startupUrl index.html -logLevel INFO -codeServerPort auto -port auto -war ..git\etc-site\target\etc-site-0.0.1-SNAPSHOT edu.arizona.biosemantics.etcsite.EtcSite edu.arizona.biosemantics.otolite.OTOLite_part2

