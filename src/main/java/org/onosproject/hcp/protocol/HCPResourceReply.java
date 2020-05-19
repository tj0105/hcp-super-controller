package org.onosproject.hcp.protocol;

import org.onosproject.hcp.types.HCPVportHop;
import org.onosproject.hcp.types.IPv4Address;

import java.util.List;

public interface HCPResourceReply extends HCPSbpCmpData{
    IPv4Address getDstIpAddress();
    List<HCPVportHop> getvportHopList();
}
