//package edu.arizona.biosemantics.etcsitehelp.server;
//
//import com.google.inject.Guice;
//import com.google.inject.Injector;
//import com.google.inject.servlet.GuiceServletContextListener;
//import com.google.inject.servlet.ServletModule;
//
//import edu.arizona.biosemantics.etcsitehelp.server.rpc.help.HelpService;
//
//public class GuiceServletConfig extends GuiceServletContextListener {
//	@Override
//	protected Injector getInjector() {
//		return Guice.createInjector(new ServletModule() {
//			/* http://www.gwtproject.org/doc/latest/DevGuideServerCommunication.html#DevGuideImplementingServices 
//				-> Common pitfalls: for url-pattern help */
//			@Override
//			protected void configureServlets() {
//				serve("/etcsitehelp/help").with(HelpService.class);
//			}
//			
//		}, new ETCSiteHelpModule());
//	}
//}
