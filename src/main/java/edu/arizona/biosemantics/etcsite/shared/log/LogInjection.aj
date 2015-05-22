package edu.arizona.biosemantics.etcsite.shared.log;

import edu.arizona.biosemantics.common.log.AbstractLogInjection;
import edu.arizona.biosemantics.common.log.ILoggable;

public aspect LogInjection extends AbstractLogInjection {
	
	declare parents : edu.arizona.biosemantics.etcsite.server..* implements ILoggable;
	declare parents : edu.arizona.biosemantics.etcsite.shared..* implements ILoggable;
}
