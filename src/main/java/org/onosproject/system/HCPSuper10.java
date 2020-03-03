package org.onosproject.system;

import org.jboss.netty.channel.Channel;
import org.onlab.packet.IpAddress;
import org.onosproject.api.HCPSuper;
import org.onosproject.api.domain.HCPDomainController;
import org.onosproject.hcp.protocol.HCPFactories;
import org.onosproject.hcp.protocol.HCPFactory;
import org.onosproject.hcp.protocol.HCPMessage;
import org.onosproject.hcp.protocol.HCPVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author ldy
 * @Date: 20-2-29 下午1:26
 * @Version 1.0
 */
public class HCPSuper10 implements HCPSuper{
    private final static Logger log= LoggerFactory.getLogger(HCPSuper10.class);

    private Channel channel;
    protected  String channelId;

    private boolean connected;

    private HCPDomainController domainController;
    private HCPVersion hcpVersion;

    private final AtomicInteger xidCounter=new AtomicInteger(0);

    public HCPSuper10(HCPDomainController domainController){
        this.domainController=domainController;
    }

    @Override
    public void sendMsg(HCPMessage message) {
        this.sendMessageOnChannel(Collections.singletonList(message));
    }

    @Override
    public void sendMsg(List<HCPMessage> messages) {
        this.sendMessageOnChannel(messages);
    }

    @Override
    public void handleMessage(HCPMessage message) {
        this.domainController.processMessage(message);
    }

    @Override
    public HCPFactory factory() {
        return HCPFactories.getFactory(hcpVersion);
    }

    @Override
    public void setConnected(boolean isConnected) {
        this.connected=isConnected;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public String ChannelId() {
        return channelId;
    }

    @Override
    public void disConnectSuper() {
        setConnected(false);
        this.channel.close();
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel=channel;
        final SocketAddress address = channel.getRemoteAddress();
        if (address instanceof InetSocketAddress) {
            final InetSocketAddress inetAddress = (InetSocketAddress) address;
            final IpAddress ipAddress = IpAddress.valueOf(inetAddress.getAddress());
            if (ipAddress.isIp4()) {
                channelId = ipAddress.toString() + ':' + inetAddress.getPort();
            } else {
                channelId = '[' + ipAddress.toString() + "]:" + inetAddress.getPort();
            }
        }
    }

    @Override
    public int getNextTransactionId() {
        return this.xidCounter.getAndIncrement();
    }

    private void sendMessageOnChannel(List<HCPMessage> messages){
        if (channel.isConnected()){
            channel.write(messages);
            domainController.processDownStreamMessage(messages);
        }else{
            log.info("Drop message bucause hcpsuper channel is disconnected,msg:{}",messages);
        }
    }

    public void sendHandShakeMessage(HCPMessage message){
        sendMessageOnChannel(Collections.singletonList(message));
    }

    public void setHCPVersion(HCPVersion hcpVersion){
        this.hcpVersion=hcpVersion;
    }
}
