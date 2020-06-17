package org.onosproject.system.Super;


import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onlab.packet.Ethernet;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.HCPDomainMessageListener;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.protocol.ver10.HCPForwardingReplyVer10;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.hcp.types.HCPVport;
import org.onosproject.hcp.types.IPv4Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component(immediate = true)
public class HCPSuperCbenchTest {
    private final Logger log = LoggerFactory.getLogger(HCPSuperCbenchTest.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private HCPSuperController superController;

    private HCPDomainMessageListener domainMessageListener=new InternalDomainMessageListener();
    private  Set<HCPSbpFlags> flagset= new HashSet<>();
    private HCPVersion hcpVersion;
    private HCPFactory hcpFactory;
    private HCPVport srcVport=null;
    private HCPVport dstVPort=null;
    @Activate
    public void activate(){
        superController.addMessageListener(domainMessageListener);
//        hcpVersion=superController.getVersion();
//        hcpFactory=HCPFactories.getFactory(hcpVersion);
//        hostVportHop=new ConcurrentHashMap<>();
//        processFlowTime=new ConcurrentHashMap<>();
        srcVport=HCPVport.IN_PORT;
        dstVPort=HCPVport.OUT_PORT;
        flagset.add(HCPSbpFlags.DATA_EXITS);
        log.info("============HCPSuperRouting Started===============");
    }
    private void sendForwardingReplyToDomain(DomainId domainId, IPv4Address src,IPv4Address dst){
        HCPForwardingReply hcpForwardingReply= HCPForwardingReplyVer10.of(src,dst,
                srcVport,dstVPort, Ethernet.TYPE_IPV4,(byte) 1);
        HCPDomain hcpDomain=superController.getHCPDomain(domainId);
        HCPSbp sbp=hcpDomain.factory().buildSbp()
                .setSbpCmpType(HCPSbpCmpType.FLOW_FORWARDING_REPLY)
                .setFlags(flagset)
                .setDataLength((short)hcpForwardingReply.getData().length)
                .setXid(1)
                .setSbpCmpData(hcpForwardingReply)
                .build();
        superController.sendHCPMessge(hcpDomain.getDomainId(),sbp);
    }
    private class InternalDomainMessageListener implements HCPDomainMessageListener{

        @Override
        public void handleIncomingMessaget(DomainId domainId, HCPMessage message) {
            if (message.getType()!= HCPType.HCP_SBP)
                return ;
            HCPSbp sbp=(HCPSbp) message;
            if(sbp.getSbpCmpType()!= HCPSbpCmpType.FLOW_FORWARDING_REQUEST){
                return ;
            }
            HCPForwardingRequest hcpForwardingRequest=(HCPForwardingRequest) sbp.getSbpCmpData();
            sendForwardingReplyToDomain(domainId,hcpForwardingRequest.getSrcIpAddress(),hcpForwardingRequest.getDstIpAddress());
        }

        @Override
        public void hanldeOutGoingMessage(DomainId domainId, List<HCPMessage> messages) {

        }
    }
}
