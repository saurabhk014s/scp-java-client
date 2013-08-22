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
import uk.co.marcoratto.scp.listeners.SCPListenerFactoryException;

public class TestSCP extends TestBase {
	
	public void testGetScm() throws SCPListenerFactoryException, SCPPException {
        SCP scp = new SCP(this);
        scp.setVerbose(true);
        scp.setKeyfile(System.getProperty("user.home") + "/.ssh/id_dsa");
        scp.setPassphrase("");
        scp.setTrust(true);
        scp.setRecursive(true);

        scp.setFromUri("/Users/peterh@ongame.com/Temp/nexus2adt/maven_project/");        
        // scp.setFromUri("./test/samples/testGetScm/");
                
        // scp.setToUri("buildm@buildarch.bgs.gameop.net:/svn/buildarch/");
        scp.setToUri("rattom@160.220.143.223:/home/rattom/tmp/");
        
        try {
            scp.execute();
        } catch (SCPPException e) {
            e.printStackTrace();
            fail("Scp failed to execute.");
        }

        // scp.setFromUri("/Users/peterh@ongame.com/Temp/nexus2adt/.timestamp");
        scp.setFromUri("./test/samples/testGetScm/.timestamp");
        // scp.setToUri("buildm@buildarch.bgs.gameop.net:/svn/buildarch/");
        scp.setToUri("rattom@160.220.143.223:/home/rattom/tmp/");
        try {
            scp.execute();
        } catch (SCPPException e) {
            e.printStackTrace();
            fail("Scp failed to execute.");
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
