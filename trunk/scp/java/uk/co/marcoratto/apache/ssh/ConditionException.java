package uk.co.marcoratto.apache.ssh;

import org.apache.log4j.Logger;

public class ConditionException extends Exception {

	private static Logger logger = Logger.getLogger(ConditionException.class);
	
	private static final long serialVersionUID = -8081682431340732511L;

	public ConditionException(String s) {
		super(s);
		logger.error(s);
	}

	public ConditionException(String s, Exception e) {
		super(s, e);
		logger.error(e);
	}

	public ConditionException(Exception e) {
		super(e);
		logger.error(e);
	}

}
