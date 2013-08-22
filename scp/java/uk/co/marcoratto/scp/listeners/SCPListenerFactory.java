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
package uk.co.marcoratto.scp.listeners;

import org.apache.log4j.Logger;

import uk.co.marcoratto.util.PropertiesManager;
import uk.co.marcoratto.util.PropertiesManagerException;

public class SCPListenerFactory {

	private static Logger logger = Logger.getLogger(SCPListenerFactory.class);
	
    private static SCPListenerFactory instance = null;

    private SCPListenerFactory() {
      logger.debug("ListenerFactory.ListenerFactory();");
    }

    public static SCPListenerFactory getInstance() {
      if (instance == null) {
        instance = new SCPListenerFactory();
      } 
      return instance;
    }
    
    public void reset() {
    	instance = null;
    }
    
    public SCPListener getLogger() throws SCPListenerFactoryException {
    	SCPListener scpLogger = null;
		try {
			String factory = PropertiesManager.getInstance().getProperty("scpListener", null);
			if (factory != null) {
		    	logger.info("factoryString=" + factory);
		    	scpLogger = (SCPListener) Class.forName(factory).newInstance();
			} else {
				throw new SCPListenerFactoryException("Why 'scpListener' is null ?");
			}
		} catch (PropertiesManagerException e) {
			throw new SCPListenerFactoryException(e);
		} catch (ClassNotFoundException e) {
			throw new SCPListenerFactoryException(e);
		} catch (InstantiationException e) {
			throw new SCPListenerFactoryException(e);
		} catch (IllegalAccessException e) {
			throw new SCPListenerFactoryException(e);
		}    
    	return scpLogger;
    }

}