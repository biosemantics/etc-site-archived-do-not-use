package edu.arizona.biosemantics.etcsite.filemanager.shared.log;

import edu.arizona.biosemantics.common.log.AbstractLogInjection;
import edu.arizona.biosemantics.common.log.ILoggable;

public aspect LogInjection extends AbstractLogInjection {
	
	declare parents : edu.arizona.biosemantics.etcsite.filemanager.server..* implements ILoggable;
	declare parents : edu.arizona.biosemantics.etcsite.filemanager.shared..* implements ILoggable;
}
