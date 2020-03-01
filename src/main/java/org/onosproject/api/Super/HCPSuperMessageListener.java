package org.onosproject.api.Super;

import org.onosproject.hcp.protocol.HCPMessage;

import java.util.List;

/**
 * @Author ldy
 * @Date: 20-2-28 下午10:28
 * @Version 1.0
 */
public interface HCPSuperMessageListener {
    void handleIncommingMessage(HCPMessage message);

    void handleOutGoingMessage(List<HCPMessage> messages);

}
