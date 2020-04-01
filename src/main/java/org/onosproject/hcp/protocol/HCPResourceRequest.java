package org.onosproject.hcp.protocol;

import org.onosproject.hcp.types.IPv4Address;

import java.util.Set;

public interface HCPResourceRequest extends HCPSbpCmpData{
    IPv4Address getSrcIpAddress();
    IPv4Address getDstIpAddress();
    Set<HCPConfigFlags> getFlags();

}
