package edu.arizona.biosemantics.etcsite.server.quartz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto.client.lite.OTOLiteClient;
import edu.arizona.biosemantics.oto.client.oto.OTOClient;
import edu.arizona.biosemantics.oto.common.model.GlossaryDownload;
import edu.arizona.biosemantics.oto.common.model.lite.Download;

public class GlossariesDownloadJob  implements Job {

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log(LogLevel.INFO, "Start Glossaries Download Job");
		saveCommunityDecisions();
		log(LogLevel.INFO, "Completed Glossaries Download Job");
	}

	public void saveCommunityDecisions() {
		OTOLiteClient otoLiteClient = new OTOLiteClient(edu.arizona.biosemantics.etcsite.server.Configuration.oto2Url);
		OTOClient otoClient = new OTOClient(edu.arizona.biosemantics.semanticmarkup.config.Configuration.otoUrl);
		
		otoLiteClient.open();
		otoClient.open();
		for(TaxonGroup taxonGroup : TaxonGroup.values()) {
			Future<Download> futureCommunityDownload = otoLiteClient.getCommunityDownload(taxonGroup.getDisplayName());
			try {
				Download communityDownload = futureCommunityDownload.get();
				storeToLocalCommunityDownload(communityDownload, taxonGroup);
			} catch (InterruptedException | ExecutionException e) {
				log(LogLevel.ERROR, "Couldn't download glossaryDownload", e);
			}
			
			Future<GlossaryDownload> futureGlossaryDownload = otoClient.getGlossaryDownload(taxonGroup.getDisplayName());
			try {
				GlossaryDownload glossaryDownload = futureGlossaryDownload.get();
				storeToLocalGlossaryDownload(glossaryDownload, taxonGroup);
			} catch (InterruptedException | ExecutionException e) {
				log(LogLevel.ERROR, "Couldn't download glossaryDownload", e);
			}
		}
		otoLiteClient.close();
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
