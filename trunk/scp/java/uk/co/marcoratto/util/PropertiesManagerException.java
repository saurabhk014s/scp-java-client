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

import org.apache.log4j.Logger;

/**
 * Costituisce la superclasse di tutte le Exception dell'Infrastruttura.
 * <BR>Non ne &egrave; previsto l'utilizzo diretto (istanziazione e/o lancio), ma pu&ograve; essere utilizzata nella gestione del <i>trapping</i>, in modo da poter trattare facilmente tutte le Exception di Infrastruttura.
 * @author Marco Ratto
 */
public class PropertiesManagerException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3348677367947352434L;
	
	private static Logger logger = Logger.getLogger("uk.co.marcoratto.ftps");
	
  public PropertiesManagerException(String s) {
    super(s);
    logger.error(s);
  }

  public PropertiesManagerException(String s, Exception e) {
    super(s, e);
    logger.error(s, e);
  }

  public PropertiesManagerException(Exception e) {
    super(e);
    logger.error(e.getMessage(), e);
  }

  public PropertiesManagerException() {
    super();
    logger.error("");
  }
}
