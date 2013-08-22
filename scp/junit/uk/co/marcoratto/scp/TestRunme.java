/*
 * Copyright 2012 Marco Ratto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 */ 
package uk.co.marcoratto.scp;
 

import java.io.File;

import uk.co.marcoratto.scp.listeners.SCPListenerException;
import uk.co.marcoratto.util.Utility;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TestRunme extends TestBase {
	
	private String hostname = null;
	private String username = null;
	private String password = null;
	private String remoteDir = null;
	private String remoteUrlWithPassword = null;
	private String remoteUrlWithoutPassword = null;
	private String remoteUrl = null;
	private String privateKey = null;
	
	protected void setUp() {
		super.setUp();		
		this.hostname = "160.220.143.223";
		this.username = "rattom";
		this.password = "rm";
		this.privateKey = "/home/rattom/.ssh/id_dsa";
		this.remoteDir = "/home/rattom/tmp";
		this.remoteUrl = username + "@" + hostname + ":" + remoteDir;
		this.remoteUrlWithPassword = username + ":" + password + "@" + hostname + ":" + remoteDir;
		this.remoteUrlWithoutPassword = username + "@" + hostname + ":" + remoteDir;
	}
	
	protected void tearDown() {
		System.out.println(this.getName() + ".tearDown()");
	}

	public static void main (String[] args) {
		TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(TestRunme.class);
	}	
	
	public void testHelp() {
		System.out.println(this.getClass().getName() + ".testHelp()");
		try {			 
			this.params.add("-h");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			assertNull(actualThrowable);
			assertEquals(4, actualReturnCode);
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testSendOneFileAskingPassword() {
		System.out.println(this.getClass().getName() + ".testSendOneFileAskingPassword()");
		try {			 
			this.params.clear();
			this.params.add("-source");
			this.params.add("./test/samples/testSendOneFile/dummy.txt");
			this.params.add("-target");
			this.params.add(this.remoteUrlWithoutPassword);
			this.params.add("-trust");
			this.params.add("-ask");
			runme.runme((String[]) this.params.toArray(new String[0]));
			assertNull(actualThrowable);
			assertEquals(0, actualReturnCode);
			assertEquals(1, actualEndTotalFiles);	
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}
			
	public void testSendOneFileWithSftp() {
		System.out.println(this.getClass().getName() + ".testSendOneFileWithSftp()");
		try {			 
			this.params.clear();
			this.params.add("-source");
			this.params.add("./test/samples/testSendOneFile/dummy.txt");
			this.params.add("-target");
			this.params.add(this.remoteUrl); 
			this.params.add("-keyfile");
			this.params.add(this.privateKey);
			this.params.add("-trust");
			this.params.add("-sftp");
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			assertEquals(0, actualReturnCode);
			assertEquals(1, actualEndTotalFiles);

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}
	
	public void testSendOneFile() {
		System.out.println(this.getClass().getName() + ".testSendOneFile()");
		try {			 
			String filename = "testSendOneFile.txt";
			File file1 = new File("./test/samples/testSendOneFile", filename);			
			file1.delete();			
			Utility.stringToFile(file1, "UTF-8", "testSendOneFile.txt");
			assertEquals(true, file1.exists());

			this.params.clear();
			this.params.add("-source");
			this.params.add(file1.getCanonicalPath());
			this.params.add("-target");
			this.params.add(this.remoteUrl); 
			this.params.add("-keyfile");
			this.params.add(this.privateKey);
			this.params.add("-trust");
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			assertEquals(0, actualReturnCode);
			assertEquals(1, actualEndTotalFiles);

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testSendOneFileWithUsernamePassword() {
		System.out.println(this.getClass().getName() + ".testSendOneFileWithUsernamePassword()");
		try {
			this.params.clear();
			this.params.add("-source");
			this.params.add("./test/samples/testSendOneFile/dummy.txt");
			this.params.add("-target");
			this.params.add(this.remoteUrlWithPassword);
			this.params.add("-trust");
			runme.runme((String[]) this.params.toArray(new String[0]));
			assertNull(actualThrowable);
			assertEquals(0, actualReturnCode);
			assertEquals(1, actualEndTotalFiles);			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testSendMultipleFiles() {
		System.out.println(this.getClass().getName() + ".testSendMultipleFiles()");
		try {	
			String filename1 = "testSendMultipleFiles-1.txt";
			File file1 = new File("./test/samples/testSendMultipleFiles", filename1);			
			file1.delete();			
			Utility.stringToFile(file1, "UTF-8", filename1);
			assertEquals(true, file1.exists());

			String filename2 = "testSendMultipleFiles-2.txt";
			File file2 = new File("./test/samples/testSendMultipleFiles", filename2);			
			file2.delete();			
			Utility.stringToFile(file2, "UTF-8", filename2);
			assertEquals(true, file2.exists());

			String filename3 = "testSendMultipleFiles-3.txt";
			File file3 = new File("./test/samples/testSendMultipleFiles", filename3);			
			file3.delete();			
			Utility.stringToFile(file3, "UTF-8", filename3);
			assertEquals(true, file3.exists());

			String filename4 = "testSendMultipleFiles-4.log";
			File file4 = new File("./test/samples/testSendMultipleFiles", filename4);			
			file4.delete();			
			Utility.stringToFile(file4, "UTF-8", filename4);
			assertEquals(true, file4.exists());

			this.params.clear();
			this.params.add("-source");
			this.params.add("./test/samples/testSendMultipleFiles/*.txt");
			this.params.add("-target");
			this.params.add(this.remoteUrl); 
			this.params.add("-keyfile");
			this.params.add(this.privateKey);
			this.params.add("-trust");
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			// Exception must be NULL
			assertNull(actualThrowable);
			
			// Return Code must be ZERO
			assertEquals(0, actualReturnCode);
			
			// Three files expected
			assertEquals(3, actualEndTotalFiles);
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testSendMultipleFilesWithSubdirectories() {
		System.out.println(this.getClass().getName() + ".testSendMultipleFilesWithSubdirectories()");
		try {			 
			this.params.clear();
			this.params.add("-source");
			this.params.add("./test/samples/testSendMultipleFilesWithSubdirectories");
			this.params.add("-target");
			this.params.add(this.remoteUrl); 
			this.params.add("-keyfile");
			this.params.add(this.privateKey);
			this.params.add("-trust");
			this.params.add("-r");
			this.params.add("-v");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			// Exception must be NULL
			assertNull(actualThrowable);
			
			// Return Code must be ZERO
			assertEquals(0, actualReturnCode);
			
			// Three files expected
			assertEquals(1, actualEndTotalFiles);
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}
	
	public void testReceiveOneFileWithSftp() {
		System.out.println(this.getClass().getName() + ".testReceiveOneFileWithSftp()");
		try {			 
			String filename = "testReceiveOneFileWithSftp.txt";
			File file1 = new File("./test/samples/tmp", filename);			
			file1.delete();			
			Utility.stringToFile(file1, "UTF-8", "testReceiveOneFileWithSftp.txt");
			assertEquals(true, file1.exists());

			this.params.clear();
			this.params.add("-source");
			this.params.add(file1.getCanonicalPath());
			this.params.add("-target");
			this.params.add(this.remoteUrl); 
			this.params.add("-keyfile");
			this.params.add(this.privateKey);
			this.params.add("-trust");
			this.params.add("-sftp");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			File file2 = new File("./test/samples/testReceiveOneFileWithSftp", filename);			
			if (file2.exists()) {
				file2.delete();					
			}
			
			this.params.clear();
			this.params.add("-source");
			this.params.add(this.remoteUrl + "/" + filename); 
			this.params.add("-target");
			this.params.add(file2.getParent());
			this.params.add("-keyfile");
			this.params.add(this.privateKey);
			this.params.add("-trust");
			this.params.add("-sftp");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			
			assertEquals(0, actualReturnCode);
			
			assertEquals(1, actualStartTotalFiles);

			assertEquals(true, file2.exists());
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testReceiveOneFile() {
		System.out.println(this.getClass().getName() + ".testReceiveOneFile()");
		try {			 
			this.params.clear();
			this.params.add("-source");
			this.params.add(this.remoteUrl + "/dummy.txt"); 
			this.params.add("-target");
			this.params.add("./tmp");
			this.params.add("-keyfile");
			this.params.add(this.privateKey);
			this.params.add("-trust");
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			assertEquals(0, actualReturnCode);
			assertEquals(1, actualStartTotalFiles);

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}
	
	public void testReceiveMultipleFiles() {
		System.out.println(this.getClass().getName() + ".testReceiveMultipleFiles()");
		try {			 
			File dir1 = new File("./test/samples/tmp", "testReceiveMultipleFiles");			

			this.params.clear();
			this.params.add("-source");
			this.params.add(this.remoteUrl + "/*.txt"); 
			this.params.add("-target");
			this.params.add(dir1.getCanonicalPath());
			this.params.add("-keyfile");
			this.params.add(this.privateKey);
			this.params.add("-trust");
			this.params.add("-v");
			// this.params.add("-r");
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			assertEquals(0, actualReturnCode);
			assertEquals(1, actualStartTotalFiles);

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testReceiveMultipleFilesRecursive() {
		System.out.println(this.getClass().getName() + ".testReceiveMultipleFilesRecursive()");
		try {			 
			File dir1 = new File("./test/samples/testReceiveMultipleFilesRecursive");			

			this.params.clear();
			this.params.add("-source");
			this.params.add(this.remoteUrl + "/testSendMultipleFilesWithSubdirectories"); 
			this.params.add("-target");
			this.params.add(dir1.getCanonicalPath());
			this.params.add("-keyfile");
			this.params.add(this.privateKey);
			this.params.add("-trust");
			this.params.add("-v");
			this.params.add("-r");
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			assertEquals(0, actualReturnCode);
			assertEquals(1, actualStartTotalFiles);

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void onStartUploadSCP(int counter, int totalFiles, File fromFile,
				String toUri) throws SCPListenerException {
			System.out.println(fromFile.getAbsolutePath());
		}

		public void onEndUploadSCP(int counter, int totalFiles, File fromFile,
				String toUri) throws SCPListenerException {
			System.out.println(counter + "/" + totalFiles + ":" + fromFile.getAbsolutePath());	
			actualEndTotalFiles = totalFiles;
		}

		public void onStartDownloadSCP(int counter, int totalFiles, String fromUri, String toUri)
				throws SCPListenerException {
		}

		public void onEndDownloadSCP(int counter, int totalFiles, String fromUri, String toUri) throws SCPListenerException {
			actualStartTotalFiles = 1;	
		}

		public void onErrorSCP(Throwable t) throws SCPListenerException {
			actualThrowable = t;
		}

		public void onStartSCP(String[] args) throws SCPListenerException {
		}

		public void onEndSCP(int rc) throws SCPListenerException {
			actualReturnCode = rc;
		}
		
}
