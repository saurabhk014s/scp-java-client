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
package uk.co.marcoratto.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.apache.log4j.Logger;

public class Utility {

	private static Logger logger = Logger.getLogger("uk.co.marcoratto.scp");

	public final static String NEWLINE = System.getProperty("line.separator");

	public static String inputString(String msg, String defaultValue) {
		System.out.print(msg);
		String out = defaultValue;
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		// read in user input 
		try {
		    out = reader.readLine(); 
		} catch(Exception e) { }
		return out;
	}
	
	public static int stringToInt(String s) throws UtilityException {
		int out = -1;		
		try {
			out = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new UtilityException(e);
		}
		return out;
	}

	public static boolean stringToBoolean(String s) throws UtilityException {
		if (s == null) {
			return false;
		} else {
			return s.trim().equalsIgnoreCase("true");
		}
	}
	
	public static void stringToFile(File file, String encoding, String buffer) throws UtilityException {
		BufferedWriter bw = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new StringReader(buffer));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
			String line = null;
			String space = "";
			while ((line = br.readLine()) != null) {
				bw.write(space);
				bw.write(line);
				space = NEWLINE;
			}
			logger.info("Write " + buffer.length() + " bytes to file.");
			logger.debug("The buffer is:" + NEWLINE + buffer);
		} catch (IOException ioe) {
			throw new UtilityException(ioe);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
					// Ignore
				}
			}
		}
	}

	public static void sleep(int sec) {
		try {
			logger.info("Sleep " + sec + " seconds...");
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			logger.warn(e.getMessage(), e);
		}
	}

}
