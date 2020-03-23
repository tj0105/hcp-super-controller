package org.onosproject.api.Super;

import org.onlab.packet.IpAddress;
import org.onosproject.hcp.protocol.HCPVportDescribtion;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.hcp.types.HCPHost;
import org.onosproject.hcp.types.HCPInternalLink;
import org.onosproject.net.*;

import java.util.List;
import java.util.Set;

public interface HCPSuperTopoServices {
    List<PortNumber> getVports(DomainId domainId);

    HCPVportDescribtion getVportDes(DomainId domainId,PortNumber portNumber);

    List<Link>  getInterDomainLink();

    DeviceId getDeviceId(DomainId domainId);

    Set<HCPHost> getHostByDomainId(DomainId domainId);

    Set<Link>  getIntraDomainLink(DomainId domainId);

    HCPInternalLink getinternalLinkDesc(Link link);

    Set<HCPHost> getHostByIp(IpAddress ipAddress);

    DomainId getHostLocation(HostId hostId);

    long getVportLoadCapability(ConnectPoint connectPoint);

    long getVportMaxCapability(ConnectPoint connectPoint);

    long getVportRestCapability(ConnectPoint connectPoint);
}