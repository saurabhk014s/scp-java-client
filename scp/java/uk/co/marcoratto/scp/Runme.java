/*
 * Copyright (C) 2011 Marco Ratto
 *
 * This file is part of the project scp-java-client.
 *
 * scp-java-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 *
 * scp-java-client is free software; you can redistribute it and/or modify
 * it under the terms of the the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package uk.co.marcoratto.scp;

import org.apache.log4j.Logger;

import uk.co.marcoratto.scp.listeners.SCPListenerFactory;
import uk.co.marcoratto.scp.listeners.SCPListener;
import uk.co.marcoratto.scp.listeners.SCPListenerFactoryException;
import uk.co.marcoratto.util.Utility;
import uk.co.marcoratto.util.UtilityException;

public class Runme {

	private final static String VERSION = "scp '1.0'";
	
	private static Logger logger = Logger.getLogger("uk.co.marcoratto.scp");
	private SCPListener scpLogger = null;
	private static int retCode = -1;

	public Runme() {
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println(help);
			retCode = 3;
		} else {
			Runme runme = new Runme();
			runme.runme(args);
		}
		System.exit(retCode);
	}

	public void runme(String[] args) {
		try {
			scpLogger = SCPListenerFactory.getInstance().getLogger();
		} catch (SCPListenerFactoryException e) {
			logger.error(e.getMessage(), e);
		}

		try {
			logger.info("Call listener onStart...");
			scpLogger.onStartSCP(args);
		} catch (Throwable tt) {
			logger.error(tt.getMessage(), tt);
		}
		try {
			this.execute(args);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			try {
				logger.info("Call listener onError...");
				scpLogger.onErrorSCP(e);
			} catch (Throwable tt) {
				logger.error(tt.getMessage(), tt);
			}			
			retCode = 2;
		}
		logger.info("retCode=" + retCode);
		try {
			logger.info("Call listener onEnd...");
			scpLogger.onEndSCP(retCode);
		} catch (Throwable tt) {
			logger.error(tt.getMessage(), tt);
		}
	}

	public void execute(String[] args) throws SCPPException {
		for (int j = 0; j < args.length; j++) {
			logger.debug("args[" + j + "]=" + args[j]);
			if ((args[j].equalsIgnoreCase("-version") == true) ||
				(args[j].equalsIgnoreCase("--version") == true)){
				System.out.println(VERSION);
				retCode = 5;
				return;
			} else if (args[j].equalsIgnoreCase("-h") == true) {
				System.err.println(help);
				retCode = 4;
				return;
			} else if (args[j].equalsIgnoreCase("-retry") == true) {
				if (++j < args.length) {
					try {
						this.retry = Utility.stringToInt(args[j]);
					} catch (UtilityException e) {
						throw new SCPPException(e);
					}
				} else {
					throw new SCPPException("Too many args!");
				}
				this.delay = 60;
			} else if (args[j].equalsIgnoreCase("-dhcmod") == true) {
				if (++j < args.length) {
					this.dchmod = args[j];
				} else {
					throw new SCPPException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-fchmod") == true) {
				if (++j < args.length) {
					this.fchmod = args[j];
				} else {
					throw new SCPPException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-delay") == true) {
				if (++j < args.length) {
					try {
						this.delay = Utility.stringToInt(args[j]);
					} catch (UtilityException e) {
						throw new SCPPException(e);
					}
				} else {
					throw new SCPPException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-port") == true) {
				if (++j < args.length) {
					try {
						this.port = Utility.stringToInt(args[j]);
					} catch (UtilityException e) {
						throw new SCPPException(e);
					}
				} else {
					throw new SCPPException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-target") == true) {
				if (++j < args.length) {
					this.target = args[j];
				} else {
					throw new SCPPException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-ask") == true) {
				this.ask = true;
			} else if (args[j].equalsIgnoreCase("-trust") == true) {
				this.trust = true;
			} else if (args[j].equalsIgnoreCase("-keyFile") == true) {
				if (++j < args.length) {
					this.keyFile = args[j];
					this.passphrase = "";
				} else {
					throw new SCPPException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-passphrase") == true) {
				if (++j < args.length) {
					this.passphrase = args[j];
				} else {
					throw new SCPPException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-password") == true) {
				if (++j < args.length) {
					this.password = args[j];
				} else {
					throw new SCPPException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-knownHosts") == true) {
				if (++j < args.length) {
					this.knownHosts = args[j];
				} else {
					throw new SCPPException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-source") == true) {
				if (++j < args.length) {
					this.source = args[j];
				} else {
					throw new SCPPException("Too many args!");
				}
			} else if (args[j].equalsIgnoreCase("-v") == true) {
				this.verbose = true;
			} else if (args[j].equalsIgnoreCase("-r") == true) {
				this.recursive = true;
			} else if (args[j].equalsIgnoreCase("-sftp") == true) {
				this.sftp = true;
			} else {
				logger.warn("Parameter " + args[j] + " unknown! Skipped.");
			}
		}
		
		if (this.dchmod == null) {
			this.dchmod = "755";
		}
		if (this.fchmod == null) {
			this.fchmod = "644";
		}
				
		int counterOfRetries = 0;
		while (counterOfRetries <= this.retry) {
			try {
				SCP sftpClient = new SCP(scpLogger);
				sftpClient.setFromUri(this.source);
				sftpClient.setPort(this.port);
				sftpClient.setToUri(this.target);
				sftpClient.setTrust(this.trust);
				sftpClient.setKeyfile(this.keyFile);
				sftpClient.setPassphrase(this.passphrase);
				sftpClient.setKnownhosts(this.knownHosts);
				sftpClient.setVerbose(this.verbose);
				sftpClient.setPassword(this.password);
				sftpClient.setSftp(this.sftp);
				sftpClient.setFailonerror(true);
				sftpClient.setRecursive(this.recursive);
				sftpClient.setAskPassword(this.ask);
				sftpClient.setChmodFile(this.fchmod);
				sftpClient.setChmodDirectory(this.dchmod);
				sftpClient.execute();
				
				logger.info("counterOfRetries=" + counterOfRetries);
				retCode = 0;
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);				
				if (counterOfRetries >= this.retry) {
					throw new SCPPException(t);
				}
			} finally {
				counterOfRetries++;				
			}
			if ((counterOfRetries < this.retry) && (retCode != 0)) {
				Utility.sleep(this.delay);

				// Null the Listener, so you re-instanciate the object
				SCPListenerFactory.getInstance().reset();
			} 
		}
		logger.info("execute() finished...");
	}

	private int port = 22;
	private String password = null;
	private String source = null;
	private String target = null;

	private String keyFile = null;
	private String passphrase = null;
	private String knownHosts = null;
	private boolean trust = false;
	
	private boolean verbose = true;
	private boolean recursive = false;
	private boolean ask = false;
	private boolean sftp = false;
	private String fchmod = null;
	private String dchmod = null;
	
	private int retry = 0;	
	private int delay = 0;

	private static String help = " * Parameters:"
			+ "\n"
			+ "-source\n\tThis is an alternative to the file attribute. But this must always point to a local file. The reason this was added was that when you give file attribute it is treated as remote if it contains @ character. This character can exist also in local paths. Alternative to file attribute."
			+ "\n"
			+ "-target\n\tThis is an alternative to the file attribute. But this must always point to a remote file. Alternative to file attribute."
			+ "\n"
			+ "-port\n\tThe port to connect to on the remote host (default to 22)."
			+ "\n"
			+ "-trust\n\tThis trusts all unknown hosts if set to yes/true (default to false)."
			+ "\n\tNote If you set this to false (the default), the host you connect to must be listed in your knownhosts file, this also implies that the file exists. 	"
			+ "\n"
			+ "-knownhosts\n\tThis sets the known hosts file to use to validate the identity of the remote host. This must be a SSH2 format file. SSH1 format is not supported (default to ${user.home}/.ssh/known_hosts)."
			+ "\n"
			+ "-password\n\tThe password. Not if you are using key based authentication or the password has been given in the file or todir attribute."
			+ "\n"
			+ "-keyfile\n\tLocation of the file holding the private key."
			+ "\n"
			+ "-passphrase\n\tPassphrase for your private key (default to an empty string)."
			+ "\n"
			+ "-v\n\tDetermines whether SCP outputs verbosely to the user. Currently this means outputting dots/stars showing the progress of a file transfer (default to false)."
			+ "\n"
			+ "-sftp\n\tDetermines whether SCP uses the sftp protocol. The sftp protocol is the file transfer protocol of SSH2. It is recommended that this be set to true if you are copying to/from a server that doesn't support scp1 (default to false)."
			+ "\n"
			+ "-preserveLastModified\n\tDetermines whether the last modification timestamp of downloaded files is preserved. It only works when transferring from a remote to a local system and probably doesn't work with a server that doesn't support SSH2 (default to false)."
			+ "-retry #\n\t Number of retry in case of errors."  
			+ "\n"
			+ "-r\n\t Traverse recursive the directory and send the files."
			+ "\n" 
			+ "-ask\n\t Ask to the use to digit the password." 
			+ "\n";

}
