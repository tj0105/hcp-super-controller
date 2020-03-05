package org.onosproject.api;

import org.onosproject.hcp.protocol.HCPMessage;
import org.onosproject.hcp.types.DomainId;

import java.util.List;

/**
 * @Author ldy
 * @Date: 20-3-4 下午9:32
 * @Version 1.0
 */
public interface HCPDomainMessageListener {
    void handleIncomingMessaget(DomainId domainId, HCPMessage message);

    void hanldeOutGoingMessage(DomainId domainId, List<HCPMessage>messages);
}
