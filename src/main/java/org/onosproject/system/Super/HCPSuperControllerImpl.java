package org.onosproject.system.Super;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.HCPDomainMessageListener;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.api.domain.HCPDomainListener;
import org.onosproject.hcp.protocol.HCPMessage;
import org.onosproject.hcp.protocol.HCPVersion;
import org.onosproject.hcp.types.DomainId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author ldy
 * @Date: 20-3-5 上午1:02
 * @Version 1.0
 */
@Component(immediate = true)
@Service
public class HCPSuperControllerImpl implements HCPSuperController {

    private static final Logger log= LoggerFactory.getLogger(HCPSuperControllerImpl.class);

    private Map<DomainId,HCPDomain> domainMap;

    private HCPSuperConnector connector=new HCPSuperConnector(this);
    private Set<HCPDomainMessageListener> hcpDomainMessageListeners=new CopyOnWriteArraySet<>();
    private Set<HCPDomainListener> hcpDomainListeners=new CopyOnWriteArraySet<>();

    protected String HCPSuperIP="127.0.0.1";
    protected int HCPSuperPort=8899;
    private HCPVersion hcpVersion;


    @Activate
    public void acticate(){
        hcpVersion=HCPVersion.HCP_10;
        domainMap=new HashMap<>();
        connector.start();
        log.info("====================HCPSuperController Started=================");
    }
    @Deactivate
    public void deactivate(){
        log.info("Domain controller size:{} Domain Controller Channel {}",domainMap.size(),domainMap.get(DomainId.of(1111)).channleId());
        connector.stop();
        domainMap.clear();
        log.info("========================HCPSuperController stopped================");
    }


    @Override
    public HCPVersion getVersion() {
        return this.hcpVersion;
    }

    @Override
    public void setHCPVersion(HCPVersion hcpVersion) {
        this.hcpVersion=hcpVersion;
    }

    @Override
    public int getHCPSuperPort() {
        return this.HCPSuperPort;
    }

    @Override
    public void setHCPSuperPort(int hcpSuperPort) {
        this.HCPSuperPort=hcpSuperPort;
    }

    @Override
    public String getHCPSuperIp() {
        return HCPSuperIP;
    }

    @Override
    public void setHCPSuperIp(String hcpSuperIp) {
        this.HCPSuperIP=hcpSuperIp;
    }

    @Override
    public void addMessageListener(HCPDomainMessageListener listener) {
        this.hcpDomainMessageListeners.add(listener);
    }

    @Override
    public void removeMessageListener(HCPDomainMessageListener listener) {
        this.hcpDomainMessageListeners.remove(listener);
    }

    @Override
    public void addHCPDomainListener(HCPDomainListener listener) {
        this.hcpDomainListeners.add(listener);
    }

    @Override
    public void removeHCPDomainListener(HCPDomainListener listener) {
        this.hcpDomainListeners.remove(listener);
    }

    @Override
    public void sendHCPMessge(DomainId domainId, HCPMessage message) {
        HCPDomain domain=getHCPDomain(domainId);
        if (null!=domain&&domain.isConnected()){
            domain.sendMsg(message);
        }
    }

    @Override
    public void addDomain(DomainId domainId, HCPDomain domain) {
        domainMap.put(domainId,domain);
        for (HCPDomainListener listener:hcpDomainListeners){
            listener.domainConnected(domain);
        }
    }

    @Override
    public void removeDomain(DomainId domainId) {
        HCPDomain hcpDomain=getHCPDomain(domainId);
        if (null!=hcpDomain){
            domainMap.remove(domainId);
            for (HCPDomainListener listener: hcpDomainListeners){
                listener.domainDisConnected(hcpDomain);
            }
        }
    }

    @Override
    public void processDownstremMessage(DomainId domainId,List<HCPMessage> messages) {
        for (HCPDomainMessageListener messageListener:hcpDomainMessageListeners){
            messageListener.hanldeOutGoingMessage(domainId,messages);
        }
    }

    @Override
    public void processMessage(DomainId domainId,HCPMessage message) {
        for (HCPDomainMessageListener listener:hcpDomainMessageListeners){
            listener.handleIncomingMessaget(domainId,message);
        }
    }

    @Override
    public HCPDomain getHCPDomain(DomainId domainId) {
        return domainMap.get(domainId);
    }
}
