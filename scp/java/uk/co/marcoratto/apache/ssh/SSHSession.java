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

package uk.co.marcoratto.apache.ssh;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;

import uk.co.marcoratto.scp.SCPPException;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Establishes an ssh session with a remote machine, optionally
 * establishing port forwarding, then executes any nested task(s)
 * before closing the session.
 * @since Ant 1.8.0
 */
public class SSHSession extends SSHBase {
	
	private static Logger logger = Logger.getLogger(SSHSession.class);

    /** units are milliseconds, default is 0=infinite */
    private long maxwait = 0;

    private Vector localTunnels = new Vector();
    private Set localPortsUsed = new TreeSet();
    private Vector remoteTunnels = new Vector();
    private Set remotePortsUsed = new TreeSet();    

    private static final String TIMEOUT_MESSAGE =
        "Timeout period exceeded, connection dropped.";

    /**
     * The connection can be dropped after a specified number of
     * milliseconds. This is sometimes useful when a connection may be
     * flaky. Default is 0, which means &quot;wait forever&quot;.
     *
     * @param timeout  The new timeout value in seconds
     */
    public void setTimeout(long timeout) {
        maxwait = timeout;
    }

    /**
     * Changes the comma-delimited list of local tunnels to establish
     * on the connection.
     *
     * @param tunnels a comma-delimited list of lport:rhost:rport
     * tunnel specifications
     * @throws SCPPException 
     */
    public void setLocaltunnels(String tunnels) throws SCPPException {
        String[] specs = tunnels.split(", ");
        for (int i = 0; i < specs.length; i++) {
            if (specs[i].length() > 0) {
                String[] spec = specs[i].split(":", 3);
                int lport = Integer.parseInt(spec[0]);
                String rhost = spec[1];
                int rport = Integer.parseInt(spec[2]);
                LocalTunnel tunnel = createLocalTunnel();
                tunnel.setLPort(lport);
                tunnel.setRHost(rhost);
                tunnel.setRPort(rport);
            }
        }
    }

    /**
     * Changes the comma-delimited list of remote tunnels to establish
     * on the connection.
     *
     * @param tunnels a comma-delimited list of rport:lhost:lport
     * tunnel specifications
     * @throws SCPPException 
     */
    public void setRemotetunnels(String tunnels) throws SCPPException {
        String[] specs = tunnels.split(", ");
        for (int i = 0; i < specs.length; i++) {
            if (specs[i].length() > 0) {
                String[] spec = specs[i].split(":", 3);
                int rport = Integer.parseInt(spec[0]);
                String lhost = spec[1];
                int lport = Integer.parseInt(spec[2]);
                RemoteTunnel tunnel = createRemoteTunnel();
                tunnel.setRPort(rport);
                tunnel.setLHost(lhost);
                tunnel.setLPort(lport);
            }
        }
    }


    /**
     * Establish the ssh session and execute all nestedTasks
     *
     * @exception SCPPException if one of the nested tasks fails, or
     * network error or bad parameter.
     */
    public void execute() throws SCPPException {
        if (getHost() == null) {
            throw new SCPPException("Host is required.");
        }
        if (getUserInfo().getName() == null) {
            throw new SCPPException("Username is required.");
        }
        if (getUserInfo().getKeyfile() == null
            && getUserInfo().getPassword() == null) {
            throw new SCPPException("Password or Keyfile is required.");
        }

        Session session = null;
        try {
            // establish the session
            session = openSession();
            session.setTimeout((int) maxwait);

            for (Iterator i = localTunnels.iterator(); i.hasNext();) {
                LocalTunnel tunnel = (LocalTunnel) i.next();
                session.setPortForwardingL(tunnel.getLPort(),
                                           tunnel.getRHost(),
                                           tunnel.getRPort());
            }

            for (Iterator i = remoteTunnels.iterator(); i.hasNext();) {
                RemoteTunnel tunnel = (RemoteTunnel) i.next();
                session.setPortForwardingR(tunnel.getRPort(),
                                           tunnel.getLHost(),
                                           tunnel.getLPort());
            }

            // completed successfully

        } catch (JSchException e) {
            if (e.getMessage().indexOf("session is down") >= 0) {
                if (getFailonerror()) {
                    throw new SCPPException(TIMEOUT_MESSAGE, e);
                } else {
                    log(TIMEOUT_MESSAGE);
                }
            } else {
                if (getFailonerror()) {
                    throw new SCPPException(e);
                } else {
                    log("Caught exception: " + e.getMessage());
                }
            }
        } catch (SCPPException e) {
            // avoid wrapping it into yet another ScpException further down
            throw e;
        } catch (Exception e) {
            if (getFailonerror()) {
                throw new SCPPException(e);
            } else {
                log("Caught exception: " + e.getMessage());
            }
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public LocalTunnel createLocalTunnel() {
        LocalTunnel tunnel = new LocalTunnel();
        localTunnels.add(tunnel);
        return tunnel;
    }

    public RemoteTunnel createRemoteTunnel() {
        RemoteTunnel tunnel = new RemoteTunnel();
        remoteTunnels.add(tunnel);
        return tunnel;
    }

    public class LocalTunnel {
        public LocalTunnel() {}

        int lport = 0;
        String rhost = null;
        int rport = 0;
        public void setLPort(int lport) throws SCPPException {
            Integer portKey = new Integer(lport);
            if (localPortsUsed.contains(portKey))
                throw new SCPPException("Multiple local tunnels defined to"
                                         + " use same local port " + lport);
            localPortsUsed.add(portKey);
            this.lport = lport;
        }
        public void setRHost(String rhost) { this.rhost = rhost; }
        public void setRPort(int rport) { this.rport = rport; }
        public int getLPort() throws SCPPException {
            if (lport == 0) throw new SCPPException("lport is required for"
                                                     + " LocalTunnel.");
            return lport;
        }
        public String getRHost() throws SCPPException {
            if (rhost == null) throw new SCPPException("rhost is required"
                                                        + " for LocalTunnel.");
            return rhost;
        }
        public int getRPort() throws SCPPException {
            if (rport == 0) throw new SCPPException("rport is required for"
                                                     + " LocalTunnel.");
            return rport;
        }
    }

    public class RemoteTunnel {
        public RemoteTunnel() {}

        int lport = 0;
        String lhost = null;
        int rport = 0;
        public void setLPort(int lport) { this.lport = lport; }
        public void setLHost(String lhost) { 
        	this.lhost = lhost; 
        }
        
        public void setRPort(int rport) throws SCPPException {
            Integer portKey = new Integer(rport);
            if (remotePortsUsed.contains(portKey))
                throw new SCPPException("Multiple remote tunnels defined to"
                                         + " use same remote port " + rport);
            remotePortsUsed.add(portKey);
            this.rport = rport;
        }
        public int getLPort() throws SCPPException {
            if (lport == 0) throw new SCPPException("lport is required for"
                                                     + " RemoteTunnel.");
            return lport;
        }
        public String getLHost() throws SCPPException {
            if (lhost == null) throw new SCPPException("lhost is required for"
                                                        + " RemoteTunnel.");
            return lhost;
        }
        public int getRPort() throws SCPPException {
            if (rport == 0) throw new SCPPException("rport is required for"
                                                     + " RemoteTunnel.");
            return rport;
        }
    }


	public void log(String message) {
		logger.info(message);
	}
	
}
