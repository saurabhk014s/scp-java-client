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
package uk.co.marcoratto.scp.listeners;

import java.io.File;

public interface SCPListener {

    void onStartUploadSCP(int counter, int totalFiles, File fromFile, String toUri) throws SCPListenerException;
    void onEndUploadSCP(int counter, int totalFiles, File fromFile, String toUri) throws SCPListenerException;
    
    void onStartDownloadSCP(int counter, int totalFiles, String fromUri, String toUri) throws SCPListenerException;
    void onEndDownloadSCP(int counter, int totalFiles, String fromUri, String toUri) throws SCPListenerException;
    
    void onErrorSCP(Throwable msg) throws SCPListenerException;
    
    void onStartSCP(String[] args) throws SCPListenerException;
    void onEndSCP(int returnCode) throws SCPListenerException;
}
