package org.onosproject.system.Super;


import org.apache.felix.scr.annotations.*;
import org.onlab.packet.*;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.HCPDomainMessageListener;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.api.Super.HCPSuperTopoServices;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.protocol.ver10.HCPPacketOutVer10;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.hcp.types.HCPHost;
import org.onosproject.hcp.types.HCPVport;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.onosproject.net.PortNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component(immediate = true)
public class HCPSuperRouting {

    private final Logger log= LoggerFactory.getLogger(HCPSuperRouting.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private HCPSuperController superController;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private HCPSuperTopoServices superTopoServices;

    private HCPDomainMessageListener domainMessageListener=new InternalDomainMessageListener();

    @Activate
    public void activate(){
        superController.addMessageListener(domainMessageListener);
        log.info("============HCPSuperRouting Started===============");
    }

    @Deactivate
    public void deactivate(){
        superController.removeMessageListener(domainMessageListener);
        log.info("============HCPSuperRouting Stoped===============");
    }

    private void processArp(DomainId domainId,PortNumber portNumber,Ethernet eth){
        ARP arp = (ARP) eth.getPayload();
        IpAddress target = Ip4Address.valueOf(arp.getTargetProtocolAddress());
        IpAddress sender = Ip4Address.valueOf(arp.getSenderProtocolAddress());
        Set<HCPHost> hostSet = superTopoServices.getHostByIp(target);
        if (hostSet.isEmpty()){
            //如果找不到，就广播
            floodArp(eth);
            return ;
        }
        HCPHost hcpHost=(HCPHost) hostSet.toArray()[0];
        HostId hostId=HostId.hostId(MacAddress.valueOf(hcpHost.getMacAddress().getLong()));
        DomainId hostLocation=superTopoServices.getHostLocation(hostId);
        if (hostLocation==null){
            return ;
        }
        switch (arp.getOpCode()){
            //如果是arp请求，则直接找到对应的host的地址，构造ARP reply发送给相应的域
            case ARP.OP_REQUEST:
                Ethernet arpReply=ARP.buildArpReply(Ip4Address.valueOf(arp.getTargetProtocolAddress()),MacAddress.valueOf(hcpHost.getMacAddress().getLong()),eth);
                PacketOut(domainId,portNumber,PortNumber.portNumber(HCPVport.LOCAL.getPortNumber()),arpReply);
                break;
            //如果是arp reply，则直接找到对应的host的地址，发送给相应的域
            case ARP.OP_REPLY:
                PacketOut(hostLocation,portNumber,PortNumber.portNumber(HCPVport.LOCAL.getPortNumber()),eth);
                break;
        }

    }
    private void processIpv4(DomainId domainId,PortNumber portNumber,Ethernet eth){

    }
    private void floodArp(Ethernet ethernet){
        for (HCPDomain domain:superController.getDomains()){
            PacketOut(domain.getDomainId(),PortNumber.portNumber(HCPVport.LOCAL.getPortNumber())
                        ,PortNumber.portNumber(HCPVport.FLOOD.getPortNumber()),ethernet);
        }
    }

    private void PacketOut(DomainId domainId,PortNumber inPort, PortNumber outPort,Ethernet ethernet){
        HCPDomain domain=superController.getHCPDomain(domainId);
        byte [] frames=ethernet.serialize();
        HCPPacketOut hcpPacketOut= HCPPacketOutVer10.of((int)outPort.toLong(),frames);
        Set<HCPSbpFlags> flags= new HashSet<>();
        flags.add(HCPSbpFlags.DATA_EXITS);
        HCPSbp hcpSbp=domain.factory().buildSbp()
                .setSbpCmpType(HCPSbpCmpType.PACKET_OUT)
                .setFlags(flags)
                .setDataLength((short)hcpPacketOut.getData().length)
                .setXid(1)
                .setSbpCmpData(hcpPacketOut)
                .build();
        superController.sendHCPMessge(domainId,hcpSbp);

    }
    class InternalDomainMessageListener implements HCPDomainMessageListener{

        @Override
        public void handleIncomingMessaget(DomainId domainId, HCPMessage message) {
            if (message.getType()!= HCPType.HCP_SBP)
                return ;
            HCPSbp sbp=(HCPSbp)message;
            Ethernet ethernet=null;
            PortNumber portNumber=null;
            if (sbp.getSbpCmpType()== HCPSbpCmpType.PACKET_IN){
                HCPPacketIn hcpPacketIn=(HCPPacketIn)sbp.getSbpCmpData();
                portNumber=PortNumber.portNumber(hcpPacketIn.getInport());
                ethernet=superController.parseEthernet(hcpPacketIn.getData());
            }

            if (ethernet==null){
                return ;
            }

            if (ethernet.getEtherType()==Ethernet.TYPE_ARP){
//                log.info("============ARP========{}",(ARP)ethernet.getPayload());
                processArp(domainId,portNumber,ethernet);
                return ;
            }

            if (ethernet.getEtherType()==Ethernet.TYPE_IPV6){
                processIpv4(domainId,portNumber,ethernet);
                return;
            }

        }

        @Override
        public void hanldeOutGoingMessage(DomainId domainId, List<HCPMessage> messages) {

        }
    }
}
