/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package uk.co.marcoratto.scp;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.FileFilter;
import java.io.IOException;
import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;

import uk.co.marcoratto.apache.ssh.Directory;
import uk.co.marcoratto.apache.ssh.SSHBase;
import uk.co.marcoratto.apache.ssh.ScpFromMessage;
import uk.co.marcoratto.apache.ssh.ScpFromMessageBySftp;
import uk.co.marcoratto.apache.ssh.ScpToMessage;
import uk.co.marcoratto.apache.ssh.ScpToMessageBySftp;
import uk.co.marcoratto.file.maketree.MakeTree;
import uk.co.marcoratto.file.maketree.MakeTreeException;
import uk.co.marcoratto.file.maketree.MakeTreeInterface;
import uk.co.marcoratto.scp.listeners.SCPListener;
import uk.co.marcoratto.util.Utility;

public class SCP extends SSHBase implements MakeTreeInterface {

	private static Logger logger = Logger.getLogger(SCP.class);

	private List<Directory> listOfDirectory = new ArrayList<Directory>();

	private Directory current;
	private File from = null;
	private String chmodFile = null;
	private String chmodDirectory = null;

	private String fromUri = null;
	private String toUri = null;
	private boolean preserveLastModified = false;
	private boolean isSftp = false;
	private boolean isRecursive = false;
	private boolean askPassword = false;
	private boolean overwrite = false;
	private int numberOfRetry = 0;
	private SCPListener scpLogger = null;

	public SCP(SCPListener anScpLogger) {
		super();
		this.scpLogger = anScpLogger;

		this.toUri = null;
		this.fromUri = null;
	}

	/**
	 * Execute this task.
	 * 
	 * @throws SCPPException
	 *             on error
	 */
	public void execute() throws SCPPException {
		logger.info("fromUri is " + this.fromUri);
		logger.info("toUri is " + this.toUri);
		if (toUri == null) {
			throw new SCPPException("Why the parameter 'toUri' is null ?");
		}
		if (fromUri == null) {
			throw new SCPPException("Why the parameter 'fromUri' is null ?");
		}
		boolean isFromRemote = this.isRemoteUri(this.fromUri);
		boolean isToRemote = this.isRemoteUri(this.toUri);
		logger.info("isFromRemote is " + isFromRemote);
		logger.info("isToRemote is " + isToRemote);

		if (isFromRemote && !isToRemote) {
			logger.info("Download mode");

			String remote = this.parseUri(fromUri);
			logger.info("remote is " + remote);
			
			File local = new File(toUri);
			logger.info("The local path is " + local.getAbsolutePath());

			/*if (local.isDirectory()) {
				logger.info("The local path is a directory.");
				local = new File(toUri, new File(remote).getName());
				logger.info("local change to " + local.getAbsolutePath());
			}*/

			try {
				scpLogger.onStartDownloadSCP(1, 1, fromUri, toUri);
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
			}
			try {
				this.download(fromUri, local);
			} catch (JSchException e) {
				throw new SCPPException(e);
			} catch (IOException e) {
				throw new SCPPException(e);
			}
			try {
				scpLogger.onEndDownloadSCP(1, 1, fromUri, toUri);
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
			}
		} else if (!isFromRemote && isToRemote) {
			logger.info("Upload mode");

			try {
				from = new File(fromUri).getCanonicalFile();
			} catch (IOException e) {
				throw new SCPPException(e);
			}
			logger.info("from is " + from);

			String wildcards = null;
			if (from.isFile()) {
				logger.info("File mode.");

				try {
					scpLogger.onStartUploadSCP(1, 1, from, toUri);
				} catch (Throwable t) {
					logger.error(t.getMessage(), t);
				}
				try {
					this.upload(from, toUri);
				} catch (IOException e) {
					throw new SCPPException(e);
				} catch (JSchException e) {
					throw new SCPPException(e);
				}
				try {
					scpLogger.onEndUploadSCP(1, 1, from, toUri);
				} catch (Throwable t) {
					logger.error(t.getMessage(), t);
				}
			} else if (from.isDirectory()) {
				logger.info("Directory mode.");
				wildcards = "*.*";
				logger.info("wildcards is " + wildcards);

				FileFilter fileFilter = new WildcardFileFilter(wildcards);
				File[] listOfFilesAndDirs = from.listFiles(fileFilter);
				logger.info("Found " + listOfFilesAndDirs.length + " files and directories.");
				this.current = new Directory(from, null);
				MakeTree mt = new MakeTree(this);
				try {
					mt.searchDirectoryTree(from, current);
					listOfDirectory.add(current);
				} catch (MakeTreeException e) {
					throw new SCPPException(e);
				}
				
				try {
					scpLogger.onStartUploadSCP(1, 1, from, toUri);
				} catch (Throwable t) {
					logger.error(t.getMessage(), t);
				}

				String file = parseUri(toUri);
				Session session = null;
				try {
					logger.info("listOfDirectory.size()="
							+ listOfDirectory.size());
					if (!this.listOfDirectory.isEmpty()) {
						session = openSession();
						ScpToMessage message = null;
						if (!isSftp) {
							message = new ScpToMessage(getVerbose(), session,
									this.listOfDirectory, file);
						} else {
							message = new ScpToMessageBySftp(getVerbose(),
									session, this.listOfDirectory, file);
						}
						message.setScpLogger(this.scpLogger);
						message.execute();
					}
				} catch (JSchException e) {
					throw new SCPPException(e);
				} catch (IOException e) {
					throw new SCPPException(e);
				} finally {
					if (session != null) {
						session.disconnect();
					}
				}
				try {
					scpLogger.onEndUploadSCP(1, 1, from, toUri);
				} catch (Throwable t) {
					logger.error(t.getMessage(), t);
				}
			} else if ((new File(fromUri).getName().indexOf("*") != -1)
					|| (new File(fromUri).getName().indexOf("?") != -1)) {
				logger.info("Wildcards mode");

				try {
					from = new File(fromUri).getParentFile().getCanonicalFile();
				} catch (IOException e) {
					throw new SCPPException(e);
				}
				logger.info("from is " + from);

				wildcards = new File(fromUri).getName();
				logger.info("wildcards is " + wildcards);

				this.current = new Directory(from, null);
				logger.info("current is " + current.getDirectory());

				FileFilter fileFilter = new WildcardFileFilter(wildcards);
				File[] listOfFilesAndDirs = from.listFiles(fileFilter);
				logger.info("Found " + listOfFilesAndDirs.length
						+ " files and directories.");

				Vector<File> listOfFiles = new Vector<File>();
				for (int j = 0; j < listOfFilesAndDirs.length; j++) {
					if (listOfFilesAndDirs[j].isFile()) {
						logger.info("listOfFilesAndDirs[" + j + "]="
								+ listOfFilesAndDirs[j]);
						listOfFiles.add(listOfFilesAndDirs[j]);
					}
				}
				int totalOfFilesFound = listOfFiles.size();
				logger.info("Found " + totalOfFilesFound + " files.");
				for (int j = 0; j < totalOfFilesFound; j++) {
					File localFile = listOfFiles.get(j);
					try {
						scpLogger.onStartUploadSCP(j + 1, totalOfFilesFound,
								localFile, toUri);
					} catch (Throwable t) {
						logger.error(t.getMessage(), t);
					}
					try {
						upload(localFile, toUri);
					} catch (IOException e) {
						throw new SCPPException(e);
					} catch (JSchException e) {
						throw new SCPPException(e);
					}
					try {
						scpLogger.onEndUploadSCP(j + 1, totalOfFilesFound,
								localFile, toUri);
					} catch (Throwable t) {
						logger.error(t.getMessage(), t);
					}
				}
			} else {
				throw new SCPPException(fromUri + " not valid!");
			}
		} else if (isFromRemote && isToRemote) {
			throw new SCPPException(
					"Copying from a remote server to a remote server is not supported.");
		} else {
			throw new SCPPException(
					"'toUri' and 'fromUri' attributes must have syntax like the following: user:password@host:/path");
		}
	}

	private void download(String fromSshUri, File toPath) throws JSchException,
			IOException, SCPPException {
		String file = parseUri(fromSshUri);

		Session session = null;
		try {
			session = openSession();
			ScpFromMessage message = null;
			if (!isSftp) {
				message = new ScpFromMessage(getVerbose(), session, file,
						toPath, this.isRecursive, preserveLastModified);
			} else {
				message = new ScpFromMessageBySftp(getVerbose(), session, file,
						toPath, this.isRecursive, preserveLastModified);
			}
			logger.info("Receiving file: " + file);
			message.execute();
		} finally {
			if (session != null) {
				session.disconnect();
			}
		}
	}

	private void upload(File fromPath, String toSshUri) throws IOException,
			JSchException, SCPPException {
		String file = this.parseUri(toSshUri);

		Session session = null;
		try {
			session = openSession();
			ScpToMessage message = null;
			if (!isSftp) {
				message = new ScpToMessage(getVerbose(), session, fromPath,
						file);
			} else {
				message = new ScpToMessageBySftp(getVerbose(), session,
						fromPath, file);
			}
			message.setChmodFile(this.chmodFile);
			message.setChmodDirectory(this.chmodDirectory);
			message.execute();
		} finally {
			if (session != null) {
				session.disconnect();
			}
		}
	}

	private String parseUri(String uri) throws SCPPException {
		logger.info("uri is " + uri);
		int indexOfAt = uri.indexOf('@');
		int indexOfColon = uri.indexOf(':');

		if (indexOfColon > -1 && indexOfColon < indexOfAt) {
			// user:password@host:/path notation
			// everything upto the last @ before the last : is considered
			// password. (so if the path contains an @ and a : it will not work)
			int indexOfCurrentAt = indexOfAt;
			int indexOfLastColon = uri.lastIndexOf(':');
			while (indexOfCurrentAt > -1 && indexOfCurrentAt < indexOfLastColon) {
				indexOfAt = indexOfCurrentAt;
				indexOfCurrentAt = uri.indexOf('@', indexOfCurrentAt + 1);
			}
			setUsername(uri.substring(0, indexOfColon));
			setPassword(uri.substring(indexOfColon + 1, indexOfAt));
		} else if (indexOfAt > -1) {
			// no password, will require keyfile
			setUsername(uri.substring(0, indexOfAt));
		} else {
			throw new SCPPException(
					"no username was given.  Can't authenticate.");
		}

		int indexOfPath = uri.indexOf(':', indexOfAt + 1);
		if (indexOfPath == -1) {
			throw new SCPPException("no remote path in " + uri);
		}

		setHost(uri.substring(indexOfAt + 1, indexOfPath));
		String remotePath = uri.substring(indexOfPath + 1);

		if ((getUserInfo().getPassword() == null)
				&& (getUserInfo().getKeyfile() == null)
				&& (getUserInfo().getPassphrase() == null)
				&& (this.askPassword == true)) {

			String pwd = Utility.inputString(this.getUserInfo().getName() + "@"
					+ this.getHost() + "'s password:", null);
			logger.info("pwd is " + pwd);
			if (pwd == null) {
				throw new SCPPException("neither password nor keyfile for user "
						+ getUserInfo().getName() + " has been "
						+ "given.  Can't authenticate.");
			} else {
				setPassword(pwd);
			}
		}
		/*
		 * if (getUserInfo().getPassword() == null && getUserInfo().getKeyfile()
		 * == null) { throw new
		 * ScpException("neither password nor keyfile for user " +
		 * getUserInfo().getName() + " has been " +
		 * "given.  Can't authenticate."); }
		 */

		if (remotePath.equals("")) {
			remotePath = ".";
		}
		return remotePath;
	}

	private boolean isRemoteUri(String uri) {
		logger.info("Check the uri " + uri);
		boolean isRemote = true;
		File f = new File(uri);
		if (f.getParentFile().exists()) {
			isRemote = false;
		}
		return isRemote;
	}

	public void setFromUri(String value) throws SCPPException {
		this.fromUri = value;
		logger.info("fromUri is " + this.fromUri);
	}

	public void setToUri(String value) throws SCPPException {
		this.toUri = value;
		logger.info("toUri is " + this.toUri);
	}

	public boolean isRecursive() {
		return isRecursive;
	}

	public void setRecursive(boolean value) {
		this.isRecursive = value;
		logger.info("isRecursive is " + this.isRecursive);
	}

	public void onFileFound(File aFile) throws MakeTreeException {
		logger.info("Added file " + aFile.getName());
		current.addFile(aFile);
	}

	public void onDirFound(File aDirectory) throws MakeTreeException {
		// current.addDirectory(new Directory(new File(aDirectory.getName())));
		current = new Directory(aDirectory, current.getParent());
		listOfDirectory.add(current);
	}

	public boolean isAskPassword() {
		return askPassword;
	}

	public void setAskPassword(boolean value) {
		this.askPassword = value;
		logger.info("askPassword is " + this.askPassword);
	}

	public int getNumberOfRetry() {
		return numberOfRetry;
	}

	public void setNumberOfRetry(int value) {
		this.numberOfRetry = value;
		logger.info("askPassword is " + this.numberOfRetry);
	}

	public String getChmodFile() {
		return chmodFile;
	}

	public void setChmodFile(String value) {
		this.chmodFile = value;
		logger.info("chmodFile is " + this.chmodFile);
	}

	public String getChmodDirectory() {
		return chmodDirectory;
	}

	public void setChmodDirectory(String value) {
		this.chmodDirectory = value;
		logger.info("chmodDirectory is " + this.chmodDirectory);
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean value) {
		this.overwrite = value;
		logger.info("overwrite is " + this.overwrite);
	}

	public void setPreservelastmodified(boolean value) {
		this.preserveLastModified = value;
		logger.info("preserveLastModified is " + this.preserveLastModified);
	}

	/**
	 * Setting this to true to use sftp protocol.
	 * 
	 * @param yesOrNo
	 *            if true sftp protocol will be used.
	 */
	public void setSftp(boolean value) {
		this.isSftp = value;
		logger.info("isSftp is " + this.isSftp);
	}
}
