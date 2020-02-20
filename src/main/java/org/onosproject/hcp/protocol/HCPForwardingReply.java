package org.onosproject.hcp.protocol;

import org.onosproject.hcp.types.DomainId;
import org.onosproject.hcp.types.IPv4Address;

public interface HCPForwardingReply extends HCPSbpCmpData {
    IPv4Address getSrcIpAddress();
    IPv4Address getDstIpAddress();
    int getSrcVport();
    int getDstVport();
    short getEthType();
    DomainId getDomainId();
}
