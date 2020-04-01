package org.onosproject.api.Super;

import org.onlab.packet.IpAddress;
import org.onosproject.hcp.protocol.HCPVportDescribtion;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.hcp.types.HCPHost;
import org.onosproject.hcp.types.HCPInternalLink;
import org.onosproject.net.*;
import org.onosproject.net.topology.*;

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

    long getInterLinkDelayCapability(Link link);
    long getInterLinkRestBandwidthCapability(Link link);

    Set<TopologyVertex> getTopologyVertx();

    Set<TopologyEdge> getTopologyEdge(TopologyVertex topologyVertex);

    Set<Path> getLoadBlancePath(ElementId src, ElementId dst, Topology topology);
    Set<Path> getLoadBlancePath(ElementId src, ElementId dst, Topology topology, LinkWeigher weigher);
    Set<Path> getLoadBlancePath(ElementId src, ElementId dst);
}
