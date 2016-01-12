//package edu.arizona.biosemantics.etcsitehelp.server;
//
//import com.google.inject.AbstractModule;
//import com.google.inject.Scopes;
//
//import edu.arizona.biosemantics.etcsitehelp.server.rpc.help.HelpService;
//import edu.arizona.biosemantics.etcsitehelp.shared.rpc.help.IHelpService;
//
//public class ETCSiteHelpModule extends AbstractModule {
//
//	@Override
//	protected void configure() {
//		bind(HelpService.class).in(Scopes.SINGLETON);
//		bind(IHelpService.class).to(HelpService.class).in(Scopes.SINGLETON);
//	}
//
//}
