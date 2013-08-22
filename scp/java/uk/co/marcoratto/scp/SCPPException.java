package uk.co.marcoratto.scp;

import org.apache.log4j.Logger;

public class SCPPException extends Exception {

	private static Logger logger = Logger.getLogger(SCPPException.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8081682431340732511L;

	public SCPPException(String s) {
		super(s);
	}

	public SCPPException(Throwable t) {
		super(t);
	}

	public SCPPException(String s, Exception e) {
		super(s, e);
		logger.error(s, e);
	}

	public SCPPException(Exception e) {
		super(e);
		logger.error(e.getMessage(), e);
	}
	
    public static SCPPException exactlyOne(String[] attrs) {
        return exactlyOne(attrs, null);
    }
	
    public static SCPPException exactlyOne(String[] attrs, String alt) {
        StringBuffer buf = new StringBuffer("Exactly one of ").append(
                '[').append(attrs[0]);
        for (int i = 1; i < attrs.length; i++) {
            buf.append('|').append(attrs[i]);
        }
        buf.append(']');
        if (alt != null) {
            buf.append(" or ").append(alt);
        }
        return new SCPPException(buf.append(" is required.").toString());
    }

}
