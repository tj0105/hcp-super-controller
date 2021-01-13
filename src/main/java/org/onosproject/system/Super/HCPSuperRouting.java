package org.onosproject.system.Super;


import org.apache.felix.scr.annotations.*;
import org.jboss.netty.handler.execution.MemoryAwareThreadPoolExecutor;
import org.onlab.graph.ScalarWeight;
import org.onlab.packet.*;
import org.onlab.packet.MacAddress;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.HCPDomainMessageListener;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.api.Super.HCPSuperRouteService;
import org.onosproject.api.Super.HCPSuperTopoServices;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.protocol.ver10.HCPForwardingReplyVer10;
import org.onosproject.hcp.protocol.ver10.HCPPacketOutVer10;
import org.onosproject.hcp.protocol.ver10.HCPResourceRequestVer10;
import org.onosproject.hcp.types.*;
import org.onosproject.net.*;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.topology.TopologyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

@Component(immediate = true)
@Service
public class HCPSuperRouting implements HCPSuperRouteService {

    private final Logger log = LoggerFactory.getLogger(HCPSuperRouting.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private HCPSuperController superController;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private HCPSuperTopoServices superTopoServices;

    private HCPDomainMessageListener domainMessageListener = new InternalDomainMessageListener();


    public static volatile DomainId RequestdomainId = null;
    private ConcurrentHashMap<HCPHost, Map<PortNumber, Integer>> hostVportHop;
    private ConcurrentHashMap<HCPIOT, Map<PortNumber, Integer>> iotVportHop;
    private ConcurrentHashMap<String, Long> processFlowTime;
    //    private static ExecutorService executorService=new ThreadPoolExecutor(10,10,
//            60, TimeUnit.SECONDS,new ArrayBlockingQueue<>(10));
    private static final long FLOW_AGE = 5000;

    @Activate
    public void activate() {
        superController.addMessageListener(domainMessageListener);
        hostVportHop = new ConcurrentHashMap<>();
        iotVportHop = new ConcurrentHashMap<>();
        processFlowTime = new ConcurrentHashMap<>();
        log.info("============HCPSuperRouting Started===============");
    }

    @Deactivate
    public void deactivate() {
        superController.removeMessageListener(domainMessageListener);
        hostVportHop.clear();
        iotVportHop.clear();
        processFlowTime = new ConcurrentHashMap<>();
        log.info("============HCPSuperRouting Stoped===============");
    }

    @Override
    public Map<PortNumber, Integer> getHostPortHop(HCPHost host) {
        return hostVportHop.get(host);
    }

    /**
     * It is mainly to deal with inter-domain ARP request message
     *
     * @param domainId
     * @param portNumber
     * @param eth
     */
    private void processArp(DomainId domainId, PortNumber portNumber, Ethernet eth) {
        ARP arp = (ARP) eth.getPayload();
        IpAddress target = Ip4Address.valueOf(arp.getTargetProtocolAddress());
        IpAddress sender = Ip4Address.valueOf(arp.getSenderProtocolAddress());
        Set<HCPHost> hostSet = superTopoServices.getHostByIp(target);
        if (hostSet.isEmpty()) {
            //如果找不到，就广播
            floodArp(eth);
            return;
        }
        HCPHost hcpHost = (HCPHost) hostSet.toArray()[0];
        HostId hostId = HostId.hostId(MacAddress.valueOf(hcpHost.getMacAddress().getLong()));
        DomainId hostLocation = superTopoServices.getHostLocation(hostId);
        if (hostLocation == null) {
            return;
        }
        switch (arp.getOpCode()) {
            //如果是arp请求，则直接找到对应的host的地址，构造ARP reply发送给相应的域
            case ARP.OP_REQUEST:
                Ethernet arpReply = ARP.buildArpReply(Ip4Address.valueOf(arp.getTargetProtocolAddress()), MacAddress.valueOf(hcpHost.getMacAddress().getLong()), eth);
                PacketOut(domainId, portNumber, PortNumber.portNumber(HCPVport.LOCAL.getPortNumber()), arpReply);
                break;
            //如果是arp reply，则直接找到对应的host的地址，发送给相应的域
            case ARP.OP_REPLY:
                PacketOut(hostLocation, portNumber, PortNumber.portNumber(HCPVport.LOCAL.getPortNumber()), eth);
                break;
        }
    }

    /**
     * HCP HOST
     * Send the Forwarding Reply message to the corresponding domain controller
     *
     * @param deviceId
     * @param src
     * @param dst
     * @param inport
     * @param outport
     */
    private void sendForwardingReplyToDomain(DeviceId deviceId, IpAddress src, IpAddress dst, HCPVport inport, HCPVport outport) {
        log.info("===================sendForwardingReplyToDomain=============inport=={}====output==={}", inport.toString(), outport.toString());
        IPv4Address srcAddress = IPv4Address.of(src.toOctets());
        IPv4Address dstAddress = IPv4Address.of(dst.toOctets());
        HCPDomain domain = superController.getHCPDomain(deviceId.toString().substring("hcp:".length()));

        HCPForwardingReply hcpForwardingReply = HCPForwardingReplyVer10.of(HCPFlowType.HCP_HOST, srcAddress, dstAddress,
                inport, outport, Ethernet.TYPE_IPV4, (byte) 1);

        Set<HCPSbpFlags> flagset = new HashSet<>();
        flagset.add(HCPSbpFlags.DATA_EXITS);
        HCPSbp sbp = domain.factory().buildSbp()
                .setSbpCmpType(HCPSbpCmpType.FLOW_FORWARDING_REPLY)
                .setFlags(flagset)
                .setDataLength((short) hcpForwardingReply.getData().length)
                .setXid(1)
                .setSbpCmpData(hcpForwardingReply)
                .build();
        superController.sendHCPMessge(domain.getDomainId(), sbp);
    }

    /**
     * HCP IOT
     * Send the Forwarding Reply message to the corresponding domain controller
     *
     * @param deviceId
     * @param srcIoT
     * @param dstIoT
     * @param inport
     * @param outport
     */
    private void sendForwardingReplyToDomain(DeviceId deviceId, HCPIOT srcIoT, HCPIOT dstIoT, HCPVport inport, HCPVport outport) {
        log.info("===================sendForwardingReplyToDomain=============inport=={}====output==={}", inport.toString(), outport.toString());
        HCPDomain domain = superController.getHCPDomain(deviceId.toString().substring("hcp:".length()));

        HCPForwardingReply hcpForwardingReply = HCPForwardingReplyVer10.of(HCPFlowType.HCP_IOT, srcIoT, dstIoT,
                inport, outport, Ethernet.TYPE_IPV4, (byte) 1);

        Set<HCPSbpFlags> flagset = new HashSet<>();
        flagset.add(HCPSbpFlags.DATA_EXITS);
        HCPSbp sbp = domain.factory().buildSbp()
                .setSbpCmpType(HCPSbpCmpType.FLOW_FORWARDING_REPLY)
                .setFlags(flagset)
                .setDataLength((short) hcpForwardingReply.getData().length)
                .setXid(1)
                .setSbpCmpData(hcpForwardingReply)
                .build();
        superController.sendHCPMessge(domain.getDomainId(), sbp);
    }

    /**
     * Send the Resource Request message to the destination domain controller
     * It is mainly used to calculated the number of hops from the dst host to each Vport (boundary node)
     *
     * @param srcAddress
     * @param targetAddress
     * @param dstHost
     */
    private void sendRequestToDstDomain(IpAddress srcAddress, IpAddress targetAddress, HCPHost dstHost) {
        IPv4Address src = IPv4Address.of(srcAddress.toOctets());
        IPv4Address dst = IPv4Address.of(targetAddress.toOctets());
        HostId dsthostId = HostId.hostId(MacAddress.valueOf(dstHost.getMacAddress().getLong()));
        DomainId hostLocation = superTopoServices.getHostLocation(dsthostId);
        HCPDomain hcpDomain = superController.getHCPDomain(hostLocation);
        Set<HCPConfigFlags> flags = new HashSet<>();
        flags.add(HCPConfigFlags.CAPABILITIES_HOP);
        Set<HCPSbpFlags> flagset = new HashSet<>();
        flagset.add(HCPSbpFlags.DATA_EXITS);
        HCPResourceRequest hcpResourceRequest = HCPResourceRequestVer10.of(src, dst, flags);
        HCPSbp hcpSbp = hcpDomain.factory().buildSbp()
                .setSbpCmpType(HCPSbpCmpType.RESOURCE_REQUEST)
                .setFlags(flagset)
                .setDataLength((short) hcpResourceRequest.getData().length)
                .setXid(1)
                .setSbpCmpData(hcpResourceRequest)
                .build();
        log.info("=======================hostLocation============{}", hostLocation);
        superController.sendHCPMessge(hostLocation, hcpSbp);

    }

    /**
     * Handle inter-domain request (IOT)
     *
     * @param domainId
     * @param srcIOT
     * @param dstIOT
     * @param vportHops
     */
    private void processFlowRequest(DomainId domainId, HCPIOT srcIOT, HCPIOT dstIOT, List<HCPVportHop> vportHops) {
        Map<PortNumber, Integer> vportIntegerMap = iotVportHop.get(srcIOT);
        if (vportIntegerMap == null) {
            vportIntegerMap = new HashMap<>();
            iotVportHop.put(srcIOT, vportIntegerMap);
        }
        for (HCPVportHop hcpVportHop : vportHops) {
            vportIntegerMap.put(PortNumber.portNumber(hcpVportHop.getVport().getPortNumber()), hcpVportHop.getHop());
        }
        HCPIOTID dstIoTId = dstIOT.getHcpiotid();
        HCPIOT dstIOTnew = superTopoServices.getIoTById(dstIoTId);
        if (dstIOTnew == null) {
            return;
        }
        DeviceId srcDeviceId = superTopoServices.getDeviceId(domainId);
        DomainId hostLocation = superTopoServices.getIoTLocation(dstIoTId);
        DeviceId dstDeviceId = superTopoServices.getDeviceId(hostLocation);
        if (srcDeviceId.equals(dstDeviceId)) {
            return;
        }
        Set<Path> paths;
        Map<PortNumber, Integer> dstVportHops = hostVportHop.get(dstIOTnew);
        Path path = null;
        if (superController.isLoadBlance()) {
            paths = superTopoServices.getLoadBlancePath(srcDeviceId, dstDeviceId);
            if (dstVportHops == null) {
                path = selectPath(paths, vportIntegerMap, dstVportHops);
            } else {
                path = selectPath(paths, vportIntegerMap, dstVportHops);
            }

        }
        Link former = null;
        for (Link link : path.links()) {
            boolean flag = false;
            if (link.src().deviceId().equals(srcDeviceId) || link.dst().deviceId().equals(dstDeviceId)) {
                if (link.src().deviceId().equals(srcDeviceId)) {
                    former = link;
                    flag = true;
                    sendForwardingReplyToDomain(srcDeviceId, srcIOT,
                            dstIOT, HCPVport.OUT_PORT, HCPVport.ofShort((short) link.src().port().toLong()));
                }
                if (link.dst().deviceId().equals(dstDeviceId)) {
                    sendForwardingReplyToDomain(dstDeviceId, srcIOT, dstIOT, HCPVport.IN_PORT, HCPVport.ofShort((short) link.dst().port().toLong()));
                }
                if (flag)
                    continue;
            }
            sendForwardingReplyToDomain(link.src().deviceId(), srcIOT, dstIOT,
                    HCPVport.ofShort((short) former.dst().port().toLong()), HCPVport.ofShort((short) link.src().port().toLong()));
            former = link;
        }
    }

    /**
     * Handle inter-domain request (Host)
     *
     * @param domainId
     * @param srcAddress
     * @param targetAddress
     * @param vportHops
     */
    private void processFlowRequest(DomainId domainId, IpAddress srcAddress, IpAddress targetAddress, List<HCPVportHop> vportHops) {
        Set<HCPHost> srchosts = superTopoServices.getHostByIp(srcAddress);
        Set<HCPHost> dsthosts = superTopoServices.getHostByIp(targetAddress);
        HCPHost srcHost = (HCPHost) srchosts.toArray()[0];
        HCPHost dstHost = (HCPHost) dsthosts.toArray()[0];
        Map<PortNumber, Integer> vportIntegerMap = hostVportHop.get(srcHost);
        if (vportIntegerMap == null) {
            vportIntegerMap = new HashMap<>();
            hostVportHop.put(srcHost, vportIntegerMap);
        }
        for (HCPVportHop hcpVportHop : vportHops) {
            vportIntegerMap.put(PortNumber.portNumber(hcpVportHop.getVport().getPortNumber()), hcpVportHop.getHop());
        }
        if (dsthosts.isEmpty()) {
            return;
        }

        DeviceId srcDeviceId = superTopoServices.getDeviceId(domainId);
        HostId dsthostId = HostId.hostId(MacAddress.valueOf(dstHost.getMacAddress().getLong()));
        DomainId hostLocation = superTopoServices.getHostLocation(dsthostId);
        DeviceId dstDeviceId = superTopoServices.getDeviceId(hostLocation);
        if (srcDeviceId.equals(dstDeviceId)) {
            return;
        }
        Set<Path> paths;
        Map<PortNumber, Integer> dstVportHops = hostVportHop.get(dstHost);
        Path path = null;
        if (superController.isLoadBlance()) {
            paths = superTopoServices.getLoadBlancePath(srcDeviceId, dstDeviceId);
            if (dstVportHops == null) {
                path = selectPath(paths, vportIntegerMap, dstVportHops);
            } else {
                path = selectPath(paths, vportIntegerMap, dstVportHops);
            }

        }

        Link former = null;
        for (Link link : path.links()) {
            boolean flag = false;
            if (link.src().deviceId().equals(srcDeviceId) || link.dst().deviceId().equals(dstDeviceId)) {
                if (link.src().deviceId().equals(srcDeviceId)) {
                    former = link;
                    flag = true;
                    sendForwardingReplyToDomain(srcDeviceId, srcAddress,
                            targetAddress, HCPVport.OUT_PORT, HCPVport.ofShort((short) link.src().port().toLong()));
                }
                if (link.dst().deviceId().equals(dstDeviceId)) {
                    sendForwardingReplyToDomain(dstDeviceId, srcAddress, targetAddress, HCPVport.IN_PORT, HCPVport.ofShort((short) link.dst().port().toLong()));
                }
                if (flag)
                    continue;
            }
            sendForwardingReplyToDomain(link.src().deviceId(), srcAddress, targetAddress,
                    HCPVport.ofShort((short) former.dst().port().toLong()), HCPVport.ofShort((short) link.src().port().toLong()));
            former = link;
        }
        log.info("===========path={}", path.toString());
    }

    private Path selectPath(Set<Path> pathSet, Map<PortNumber, Integer> srcVportHops, Map<PortNumber, Integer> dstVportHops) {
        List<Path> pathList = new ArrayList(pathSet);
        List<Path> newPath = new ArrayList<>();
        Link former = null;
        for (Path path : pathList) {
            double cost = 0;
            for (Link link : path.links()) {
                boolean flag = false;
                if (link.src().equals(path.src()) || link.dst().equals(path.dst())) {
                    if (link.src().equals(path.src())) {
                        former = link;
                        flag = true;
                        cost += srcVportHops.get(link.src().port());
                    }
                    if (link.dst().equals(path.dst())) {
                        if (dstVportHops == null) {
                            cost += 2;
                        } else {
                            cost += dstVportHops.get(link.dst().port());
                        }
                    }
                    if (flag)
                        continue;
                }
                Link link1 = DefaultLink.builder()
                        .src(former.dst())
                        .dst(link.src())
                        .type(Link.Type.DIRECT)
                        .state(Link.State.ACTIVE)
                        .providerId(ProviderId.NONE)
                        .build();
                cost += superTopoServices.getinternalLinkDesc(link1).getHopCapability();
                former = link;
            }
            cost += ((ScalarWeight) path.weight()).value();
            if (cost < 1000) {
                Path path1 = new DefaultPath(HCPSuperTopologyManager.RouteproviderId, path.links(), new ScalarWeight(cost));
                newPath.add(path1);
            }
        }
        newPath.sort((p1, p2) -> ((ScalarWeight) p1.weight()).value() > ((ScalarWeight) p2.weight()).value()
                ? 1 : (((ScalarWeight) p1.weight()).value() < ((ScalarWeight) p2.weight()).value()) ? -1 : 0);
        return newPath.isEmpty() ? null : newPath.get(0);
    }


    private void floodArp(Ethernet ethernet) {
        for (HCPDomain domain : superController.getDomains()) {
            PacketOut(domain.getDomainId(), PortNumber.portNumber(HCPVport.LOCAL.getPortNumber())
                    , PortNumber.portNumber(HCPVport.FLOOD.getPortNumber()), ethernet);
        }
    }

    private void PacketOut(DomainId domainId, PortNumber inPort, PortNumber outPort, Ethernet ethernet) {

        HCPDomain domain = superController.getHCPDomain(domainId);
        byte[] frames = ethernet.serialize();
        HCPPacketOut hcpPacketOut = HCPPacketOutVer10.of((int) outPort.toLong(), frames);
        Set<HCPSbpFlags> flags = new HashSet<>();
        flags.add(HCPSbpFlags.DATA_EXITS);
        HCPSbp hcpSbp = domain.factory().buildSbp()
                .setSbpCmpType(HCPSbpCmpType.PACKET_OUT)
                .setFlags(flags)
                .setDataLength((short) hcpPacketOut.getData().length)
                .setXid(1)
                .setSbpCmpData(hcpPacketOut)
                .build();
        superController.sendHCPMessge(domainId, hcpSbp);

    }


    class InternalDomainMessageListener implements HCPDomainMessageListener {

        @Override
        public void handleIncomingMessaget(DomainId domainId, HCPMessage message) {
            if (message.getType() != HCPType.HCP_SBP)
                return;
            HCPSbp sbp = (HCPSbp) message;
            Ethernet ethernet = null;
            PortNumber portNumber = null;
            if (sbp.getSbpCmpType() == HCPSbpCmpType.PACKET_IN) {
                HCPPacketIn hcpPacketIn = (HCPPacketIn) sbp.getSbpCmpData();
                portNumber = PortNumber.portNumber(hcpPacketIn.getInport());
                ethernet = superController.parseEthernet(hcpPacketIn.getData());
            } else if (sbp.getSbpCmpType() == HCPSbpCmpType.FLOW_FORWARDING_REQUEST) {
                HCPForwardingRequest hcpForwardingRequest = (HCPForwardingRequest) sbp.getSbpCmpData();
                if (hcpForwardingRequest.getFLowType() == HCPFlowType.HCP_HOST) {
                    IpAddress srcAddrss = IpAddress.valueOf(hcpForwardingRequest.getSrcIpAddress().toString());
                    IpAddress targetAddress = IpAddress.valueOf(hcpForwardingRequest.getDstIpAddress().toString());
                    List<HCPVportHop> vportHops = hcpForwardingRequest.getvportHopList();
                    String srcip = srcAddrss.toString() + " " + targetAddress.toString();
                    long nowTime = System.currentTimeMillis();
                    if (!processFlowTime.contains(srcip)) {
                        processFlowTime.put(srcip, nowTime);
                        processFlowRequest(domainId, srcAddrss, targetAddress, vportHops);
                    } else {
                        long beforetime = processFlowTime.get(srcip);
                        if (nowTime - beforetime > FLOW_AGE) {
                            processFlowTime.put(srcip, nowTime);
                            processFlowRequest(domainId, srcAddrss, targetAddress, vportHops);
                        }
                    }
                }
                if (hcpForwardingRequest.getFLowType() == HCPFlowType.HCP_IOT) {
                    HCPIOT srcIOT = hcpForwardingRequest.getSrcIoT();
                    HCPIOT dstIOT = hcpForwardingRequest.getDstIoT();
                    List<HCPVportHop> vportHops = hcpForwardingRequest.getvportHopList();
                    String index = srcIOT.getHcpiotid().toString() + dstIOT.getHcpiotid().toString();
                    long nowTime = System.currentTimeMillis();
                    if (!processFlowTime.contains(index)) {
                        processFlowTime.put(index, nowTime);
                        processFlowRequest(domainId, srcIOT, dstIOT, vportHops);
                    } else {
                        long befortime = processFlowTime.get(index);
                        if (nowTime - befortime > FLOW_AGE) {
                            processFlowTime.put(index,nowTime);
                            processFlowRequest(domainId,srcIOT,dstIOT,vportHops);
                        }
                    }
                }
                return;
            } else if (sbp.getSbpCmpType() == HCPSbpCmpType.RESOURCE_REPLY) {
                HCPResourceReply hcpResourceReply = (HCPResourceReply) sbp.getSbpCmpData();
                IpAddress targetAddress = IpAddress.valueOf(hcpResourceReply.getDstIpAddress().toString());
                Set<HCPHost> dsthosts = superTopoServices.getHostByIp(targetAddress);
                HCPHost dstHost = (HCPHost) dsthosts.toArray()[0];
                List<HCPVportHop> vportHops = hcpResourceReply.getvportHopList();
                Map<PortNumber, Integer> vportHopmap = hostVportHop.get(dstHost);
                if (vportHopmap == null) {
                    vportHopmap = new HashMap<>();
                    hostVportHop.put(dstHost, vportHopmap);
                }
                for (HCPVportHop hcpVportHop : vportHops) {
                    vportHopmap.put(PortNumber.portNumber(hcpVportHop.getVport().getPortNumber()), hcpVportHop.getHop());
                }
                return;
            }

            if (ethernet == null) {
                return;
            }

            if (ethernet.getEtherType() == Ethernet.TYPE_ARP) {
                processArp(domainId, portNumber, ethernet);
                return;
            }


        }

        @Override
        public void hanldeOutGoingMessage(DomainId domainId, List<HCPMessage> messages) {

        }
    }
}
