package org.onosproject.system.Super;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.felix.scr.annotations.*;
import org.onlab.packet.Ethernet;
import org.onlab.packet.HCPLLDP;
import org.onlab.packet.MacAddress;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.HCPDomainMessageListener;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.api.Super.HCPSuperTopoServices;
import org.onosproject.api.domain.HCPDomainListener;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.hcp.types.HCPHost;
import org.onosproject.hcp.types.HCPInternalLink;
import org.onosproject.hcp.types.HCPVport;
import org.onosproject.net.*;
import org.onosproject.net.provider.ProviderId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.Channel;
import java.nio.channels.ConnectionPendingException;
import java.util.*;

@Component(immediate = true)
@Service
public class HCPSuperTopologyManager implements HCPSuperTopoServices {
    private static final Logger log= LoggerFactory.getLogger(HCPSuperTopologyManager.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private HCPSuperController superController;

    //监听DomainController发送过来的VPortStatus,
    // Topo,Host,Sbp等消息，完成拓扑的构建
    private HCPDomainMessageListener domainMessageListener=new InternalDomainMessageListener();
    //监听DomainController上线
    private HCPDomainListener domainListener=new InternalHCPDomainListener();

    //记录VPort和Domain
    private Map<DomainId,Set<PortNumber>> vportMap;
    private Map<ConnectPoint,HCPVportDescribtion> vportDescribtionMap;
    private Map<ConnectPoint,Long> vportMaxCapability;
    private Map<ConnectPoint,Long> vportLoadCapability;

    //记录域间link
    private Set<Link> InterDomainLink;
    private ProviderId interDomainLinkProviderId=ProviderId.NONE;

    //记录域内link
    private Map<DomainId,Set<Link>> IntraDomainLink;
    private Map<Link,HCPInternalLink>IntraDomainLinkDescription;

    //记录hostId and Localcation
    private Map<DomainId,Map<HostId,HCPHost>> hostMap;

    @Activate
    public void activate(){
        vportMap=new HashMap<>();
        vportDescribtionMap=new HashMap<>();
        InterDomainLink=new HashSet<>();
        hostMap=new HashMap<>();
        vportLoadCapability=new HashMap<>();
        vportMaxCapability=new HashMap<>();
        IntraDomainLink=new HashMap<>();
        IntraDomainLinkDescription=new HashMap<>();
        superController.addMessageListener(domainMessageListener);
        superController.addHCPDomainListener(domainListener);
        log.info("============HCPSuperController Topology Manager started==========");
    }

    @Deactivate
    public void deactive(){
        superController.removeHCPDomainListener(domainListener);
        superController.removeMessageListener(domainMessageListener);
        vportDescribtionMap.clear();
        vportMap.clear();
        InterDomainLink.clear();
        hostMap.clear();
        vportMaxCapability.clear();
        vportLoadCapability.clear();
        IntraDomainLinkDescription.clear();
        IntraDomainLink.clear();
        log.info("============HCPSuperController Topology Manager stopped==========");
    }

    @Override
    public List<PortNumber> getVports(DomainId domainId) {
        if (!vportMap.containsKey(domainId))
            return Collections.EMPTY_LIST;
        return ImmutableList.copyOf(vportMap.get(domainId));
    }

    @Override
    public HCPVportDescribtion getVportDes(DomainId domainId, PortNumber portNumber) {
        ConnectPoint connectPoint=new ConnectPoint(getDeviceId(domainId),portNumber);
        if (!vportDescribtionMap.containsKey(connectPoint)){
            return null;
        }
        return null;
    }

    @Override
    public List<Link> getInterDomainLink() {
        return ImmutableList.copyOf(InterDomainLink);
    }

    @Override
    public DeviceId getDeviceId(DomainId domainId) {
        HCPDomain hcpDomain=superController.getHCPDomain(domainId);
        return hcpDomain.getDeviceId();
    }

    @Override
    public Set<HCPHost> getHostByDomainId(DomainId domainId) {
        return ImmutableSet.copyOf(hostMap.get(domainId).values());
    }

    private void removeVport(DomainId domainId,HCPVportDescribtion hcpVportDescribtion){
        PortNumber vportNumber=PortNumber.portNumber(hcpVportDescribtion.getPortNo().getPortNumber());
        DeviceId deviceId=getDeviceId(domainId);
        Set<PortNumber> vportSet=vportMap.get(domainId);
        if (vportSet!=null){
            vportSet.remove(vportNumber);
        }
        vportDescribtionMap.remove(new ConnectPoint(deviceId,vportNumber));
        Set<Link> removeLink=new HashSet<>();
        for (Link link:InterDomainLink){
            if (link.src().deviceId().equals(deviceId)&&link.src().port().equals(vportNumber)){
                removeLink.add(link);
            }
            if (link.dst().deviceId().equals(deviceId)&&link.dst().port().equals(vportNumber)){
                removeLink.add(link);
            }
        }
        for (Link link:removeLink){
            InterDomainLink.remove(link);
        }
    }

    private void addOrUpdateVport(DomainId domainId,HCPVportDescribtion Vportdescribtion){
        Set<PortNumber> vportSet=vportMap.get(domainId);
        if (vportSet==null){
            vportSet=new HashSet<>();
            vportMap.put(domainId,vportSet);
        }
        PortNumber VportNumber=PortNumber.portNumber(Vportdescribtion.getPortNo().getPortNumber());
        ConnectPoint connectPoint=new ConnectPoint(getDeviceId(domainId),VportNumber);
        if (Vportdescribtion.getState().equals(HCPVportState.BLOCKED)||
                Vportdescribtion.getState().equals(HCPVportState.LINK_DOWN)){
            vportSet.remove(VportNumber);
            vportDescribtionMap.remove(connectPoint);
        }else{
            vportSet.add(VportNumber);
            vportDescribtionMap.put(connectPoint,Vportdescribtion);
        }
        log.info("=============Vport========{}====",vportMap.get(domainId).toString());
    }
    private void processHostUpdateandReply(DomainId domainId, List<HCPHost> hcpHosts){
        log.info("=================hcpHost=========={}",hcpHosts.size());
        Map<HostId,HCPHost> map=hostMap.get(domainId);
        if (map==null){
            map=new HashMap<>();
            hostMap.put(domainId,map);
        }
        for (HCPHost hcpHost:hcpHosts){
            HostId hostId=HostId.hostId(MacAddress.valueOf(hcpHost.getMacAddress().getLong()));
            if (hcpHost.getHostState().equals(HCPHostState.ACTIVE)){
                map.put(hostId,hcpHost);
            }else{
                log.info("1111111111111111111111111111111111111111111111");
                map.remove(hostId);
            }
        }
        Set<HCPHost> hosts=getHostByDomainId(domainId);
        log.info("===================Host======={}=======",hosts.size());

    }
    private void processVportStatusMessage(DomainId domainId,HCPVportStatus hcpVportStatus){
        switch (hcpVportStatus.getReason()){
            case ADD:
                addOrUpdateVport(domainId,hcpVportStatus.getVportDescribtion());
                break;
            case MODIFY:
                addOrUpdateVport(domainId,hcpVportStatus.getVportDescribtion());
                break;
            case DELETE:
                removeVport(domainId,hcpVportStatus.getVportDescribtion());
                break;
            default:
                throw new IllegalArgumentException("Illegal vport status for reason type ="+hcpVportStatus.getReason());
        }
    }

    private void processTopologyReply(DomainId domainId, List<HCPInternalLink> internalLinkList){
        Set<Link> linkSet=new HashSet<>();
        DeviceId deviceId=getDeviceId(domainId);
        for (HCPInternalLink internalLink:internalLinkList){
            PortNumber srcPortNumber=PortNumber.portNumber(internalLink.getSrcVPort().getPortNumber());
            PortNumber dstPortNumber=PortNumber.portNumber(internalLink.getDstVport().getPortNumber());
            ConnectPoint srcConn=new ConnectPoint(deviceId,srcPortNumber);
            ConnectPoint dstConn=new ConnectPoint(deviceId,dstPortNumber);
            if (srcPortNumber.equals(dstPortNumber)){
                vportMaxCapability.put(srcConn,internalLink.getCapability());
                continue;
            }
            if (internalLink.getDstVport().equals(HCPVport.LOCAL)){
                vportLoadCapability.put(srcConn,internalLink.getCapability());
                continue;
            }
            Link link=DefaultLink.builder()
                    .src(srcConn)
                    .dst(dstConn)
                    .providerId(interDomainLinkProviderId)
                    .type(Link.Type.DIRECT)
                    .state(Link.State.ACTIVE)
                    .build();
            linkSet.add(link);
            IntraDomainLinkDescription.put(link,internalLink);
        }
        IntraDomainLink.put(domainId,linkSet);
    }

    private void processHCPLLDP(DomainId domainId,Ethernet ethernet,PortNumber portNumber){
        HCPLLDP hcplldp=HCPLLDP.parseHCPLLDP(ethernet);
        if (null==hcplldp){
            return ;
        }
        PortNumber srcPort=PortNumber.portNumber(hcplldp.getVportNum());
        PortNumber dstPort=portNumber;
        DomainId srcDomainId=DomainId.of(hcplldp.getDomianId());
        DeviceId srcDeviceId=DeviceId.deviceId("hcp:"+srcDomainId.toString());
        DeviceId dstDeviceId=getDeviceId(domainId);
        ConnectPoint srcConnection=new ConnectPoint(srcDeviceId,srcPort);
        ConnectPoint dstConnection=new ConnectPoint(dstDeviceId,dstPort);
        Link link=DefaultLink.builder()
                .src(srcConnection)
                .dst(dstConnection)
                .type(Link.Type.DIRECT)
                .providerId(interDomainLinkProviderId)
                .build();
        if (InterDomainLink.contains(link)){
            return;
        }
        InterDomainLink.add(link);


    }

    private class InternalDomainMessageListener implements HCPDomainMessageListener{

        @Override
        public void handleIncomingMessaget(DomainId domainId, HCPMessage message) {
                if (message.getType()== HCPType.HCP_VPORT_STATUS){
                    HCPVportStatus hcpVportStatus=(HCPVportStatus) message;
                    processVportStatusMessage(domainId,hcpVportStatus);
                    return;
                }
                if (message.getType()==HCPType.HCP_HOST_REPLY){
                    HCPHostReply hostReply=(HCPHostReply) message;
                    processHostUpdateandReply(domainId,hostReply.getHosts());
                    return;
                }
                if (message.getType()==HCPType.HCP_HOST_UPDATE){
                    HCPHostUpdate hostUpdate=(HCPHostUpdate) message;
                    processHostUpdateandReply(domainId,hostUpdate.getHosts());
                }
                if (message.getType()==HCPType.HCP_TOPO_REPLY){
                    HCPTopologyReply topologyReply=(HCPTopologyReply) message;
                    processTopologyReply(domainId,topologyReply.getInternalLink());
                }
                if (message.getType()==HCPType.HCP_SBP){
                    HCPSbp hcpSbp=(HCPSbp)message ;
                    Ethernet eth=null;
                    PortNumber inVport=null;
                    if (hcpSbp.getSbpCmpType().equals(HCPSbpCmpType.PACKET_IN)){
                        HCPPacketIn packetIn=(HCPPacketIn) hcpSbp.getSbpCmpData();
                        inVport=PortNumber.portNumber(packetIn.getInport());
                        eth=superController.parseEthernet(packetIn.getData());
                    }
                    if (null==eth){
                        return ;
                    }
                    if (eth.getEtherType()==Ethernet.TYPE_LLDP){
                        processHCPLLDP(domainId,eth,inVport);
                        return;
                    }

                }

        }

        @Override
        public void hanldeOutGoingMessage(DomainId domainId, List<HCPMessage> messages) {

        }
    }

    private class InternalHCPDomainListener implements HCPDomainListener{

        @Override
        public void domainConnected(HCPDomain domain) {
            return ;
        }

        @Override
        public void domainDisConnected(HCPDomain domain) {
            for (PortNumber portNumber:vportMap.get(domain.getDomainId())){
                ConnectPoint vportLocation=new ConnectPoint(domain.getDeviceId(),portNumber);
                vportDescribtionMap.remove(vportLocation);
                vportMaxCapability.remove(vportLocation);
                vportLoadCapability.remove(vportLocation);
            }
            vportMap.remove(domain.getDomainId());
            for (Link link:getInterDomainLink()){
                if (link.dst().deviceId().equals(domain.getDeviceId())
                        ||link.src().deviceId().equals(domain.getDeviceId())){
                    InterDomainLink.remove(link);
                }
            }
            hostMap.remove(domain.getDomainId());
        }
    }
}
