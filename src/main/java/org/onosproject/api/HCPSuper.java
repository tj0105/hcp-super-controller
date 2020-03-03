package org.onosproject.api;

import org.jboss.netty.channel.Channel;
import org.onosproject.hcp.protocol.HCPFactory;
import org.onosproject.hcp.protocol.HCPMessage;

import java.util.List;

/**
 * @Author ldy
 * @Date: 20-2-28 下午10:23
 * @Version 1.0
 */
public interface HCPSuper {
    void sendMsg(HCPMessage message);

    void sendMsg(List<HCPMessage> messages);

    void handleMessage(HCPMessage message);

    HCPFactory factory();

    void setConnected(boolean isConnected);

    boolean isConnected();

    String ChannelId();

    void disConnectSuper();

    void setChannel(Channel channel);

    int getNextTransactionId();
}

