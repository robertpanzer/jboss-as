/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.as.protocol.support.server.manager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jboss.as.process.ProcessManagerSlave.Handler;

/**
 * A server manager side Handler implementation that allows us to see the commands
 * received from a server instance
 * *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public class TestServerManagerMessageHandler implements Handler{

    private final BlockingQueue<ServerMessage> messages = new LinkedBlockingQueue<ServerMessage>();

    @Override
    public void handleMessage(String sourceProcessName, byte[] message) {
        messages.add(new ServerMessage(sourceProcessName,message));
    }

    @Override
    public void shutdown() {
        messages.add(new ServerMessage(null, "SHUTDOWN".getBytes()));
    }

    public ServerMessage awaitAndReadMessage() {
        try {
            ServerMessage sm = messages.poll(10, TimeUnit.SECONDS);
            if (sm == null)
                throw new RuntimeException("Read timed out");
            return sm;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdownServers() {
        messages.add(new ServerMessage(null, "SHUTDOWN_SERVERS".getBytes()));
    }

    @Override
    public void down(String downProcessName) {
    }

    public class ServerMessage {
        String sourceProcess;
        byte[] message;

        public ServerMessage(String sourceProcess, byte[] message) {
            super();
            this.sourceProcess = sourceProcess;
            this.message = message;
        }

        public String getSourceProcess() {
            return sourceProcess;
        }

        public byte[] getMessage() {
            return message;
        }
    }
}
