package org.onosproject.system.Super;


import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.onosproject.api.Super.HCPSuperController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.onlab.util.Tools.groupedThreads;
import static org.onlab.util.Tools.timeAgo;

/**
 * @Author ldy
 * @Date: 20-3-4 下午10:23
 * @Version 1.0
 */
public class HCPSuperConnector {
    public final static Logger log = LoggerFactory.getLogger(HCPSuperConnector.class);

    private HCPSuperController superController;

    private NioServerSocketChannelFactory execfactory;
    protected static final int SEND_BUFFER_SIZE = 4 * 1024 * 1024;
    protected long systemStartTime;
    private ChannelGroup channelGroup;
    protected int workerThreads = 16;

    public HCPSuperConnector(HCPSuperController superController) {
        this.superController = superController;
    }

    public void init() {
        this.systemStartTime = System.currentTimeMillis();
    }

    public void run() {
        try {
            final ServerBootstrap bootstrap = createServerBootStrap();
            bootstrap.setOption("reuseAddr", true);
            bootstrap.setOption("child.keepAlive", true);
            bootstrap.setOption("child.tcpNoDelay", true);
            bootstrap.setOption("child.sendBufferSize", SEND_BUFFER_SIZE);

            ChannelPipelineFactory pipelineFactory = new HCPSuperPiplineFactory(this.superController);
            bootstrap.setPipelineFactory(pipelineFactory);
            channelGroup = new DefaultChannelGroup();
            InetSocketAddress socketAddress = new InetSocketAddress(superController.getHCPSuperPort());
            channelGroup.add(bootstrap.bind(socketAddress));
            log.info("HCPSuperController Online,Listener for domain connections on {}", socketAddress);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private ServerBootstrap createServerBootStrap() {
        if (workerThreads == 0) {
            execfactory = new NioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(groupedThreads("onos/hcp", "boss-%d", log)),
                    Executors.newCachedThreadPool(groupedThreads("onos/hcp", "worker-%d", log)));
            return new ServerBootstrap(execfactory);
        } else {
            execfactory = new NioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(groupedThreads("onos/hcp", "boss-%d", log)),
                    Executors.newCachedThreadPool(groupedThreads("onos/hcp", "worker-%d", log)), workerThreads);
            return new ServerBootstrap(execfactory);
        }
    }

    public void start() {
        init();
        run();
    }

    public void stop() {
        log.info("Stopping HCPSuperController ");
        channelGroup.close();
        execfactory.shutdown();
        execfactory.releaseExternalResources();
    }
}
