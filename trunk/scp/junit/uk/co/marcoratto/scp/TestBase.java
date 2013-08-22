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

import java.util.Vector;

import uk.co.marcoratto.scp.listeners.SCPListener;

import junit.framework.TestCase;

public abstract class TestBase extends TestCase implements SCPListener {
	
	protected Runme runme = null;
	protected Vector<String> params = null;

	// MUST be static for the Factory
	protected static Throwable actualThrowable = null;  
	protected static int actualEndTotalFiles;	
	protected static int actualStartTotalFiles;
	protected static int actualReturnCode;

	protected void setUp() {
		System.out.println(this.getName() + ".setUp()");	

		try {
			runme = new Runme();
			this.params = new Vector<String>();
			System.setProperty("scp_config_file", "./test/junit.properties");
			
			actualReturnCode = -1;
			actualEndTotalFiles = -1;
			actualStartTotalFiles = -1;
			actualThrowable = null;

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 			
	}
	
}
