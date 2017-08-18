package edu.arizona.biosemantics.etcsite.server.quartz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto.client.oto.OTOClient;
import edu.arizona.biosemantics.oto.client.oto2.Client;
import edu.arizona.biosemantics.oto.model.GlossaryDownload;
import edu.arizona.biosemantics.oto.model.TermCategory;
import edu.arizona.biosemantics.oto.model.TermSynonym;
import edu.arizona.biosemantics.oto.model.lite.Decision;
import edu.arizona.biosemantics.oto.model.lite.Download;
import edu.arizona.biosemantics.oto.model.lite.Synonym;

public class GlossariesDownloadJob  implements Job {

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log(LogLevel.INFO, "Start Glossaries Download Job");
		saveGlossaryDownloads();
		log(LogLevel.INFO, "Completed Glossaries Download Job");
	}

	public void saveGlossaryDownloads() {
		Client oto2Client = new Client(edu.arizona.biosemantics.etcsite.server.Configuration.oto2Url);
		//System.out.println("otoUrl="+edu.arizona.biosemantics.semanticmarkup.config.Configuration.otoUrl);
		OTOClient otoClient = new OTOClient(edu.arizona.biosemantics.semanticmarkup.config.Configuration.otoUrl);
		
		oto2Client.open();
		otoClient.open();
		for(TaxonGroup taxonGroup : TaxonGroup.values()) {
			//store a default to have something
			Download communityDownload = new Download(false, new ArrayList<Decision>(), new ArrayList<Synonym>());
			storeToLocalCommunityDownload(communityDownload, taxonGroup);
			
			Future<Download> futureCommunityDownload = oto2Client.getCommunityDownload(taxonGroup.getDisplayName());
			try {
				communityDownload = futureCommunityDownload.get();
				if(communityDownload != null)
					storeToLocalCommunityDownload(communityDownload, taxonGroup);
			} catch (InterruptedException | ExecutionException e) {
				log(LogLevel.ERROR, "Couldn't download glossaryDownload", e);
			}
			
			//store a default to have something
			GlossaryDownload glossaryDownload = new GlossaryDownload(new ArrayList<TermCategory>(), new ArrayList<TermSynonym>(), "No Glossary Available");
			storeToLocalGlossaryDownload(new GlossaryDownload(new ArrayList<TermCategory>(), new ArrayList<TermSynonym>(), "No Glossary Available"), 
					taxonGroup);
			
			Future<GlossaryDownload> futureGlossaryDownload = otoClient.getGlossaryDownload(taxonGroup.getDisplayName());
			try {
				glossaryDownload = futureGlossaryDownload.get();
				if(glossaryDownload != null)
					storeToLocalGlossaryDownload(glossaryDownload, taxonGroup);
			} catch (InterruptedException | ExecutionException e) {
				log(LogLevel.ERROR, "Couldn't download glossaryDownload", e);
				
			}
		}
		oto2Client.close();
		otoClient.close();
	}

	private void storeToLocalGlossaryDownload(GlossaryDownload glossaryDownload, TaxonGroup taxonGroup) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
					edu.arizona.biosemantics.semanticmarkup.config.Configuration.glossariesDownloadDirectory + File.separator + 
					"GlossaryDownload." + taxonGroup.getDisplayName() + ".ser"));
			out.writeObject(glossaryDownload);
		    out.close();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't store glossaryDownload locally", e);
		}
	}

	private void storeToLocalCommunityDownload(Download communityDownload, TaxonGroup taxonGroup) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
					edu.arizona.biosemantics.semanticmarkup.config.Configuration.glossariesDownloadDirectory + File.separator + 
					"CommunityDownload." + taxonGroup.getDisplayName() + ".ser"));
			out.writeObject(communityDownload);
		    out.close();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't store glossaryDownload locally", e);
		}
	}
}
