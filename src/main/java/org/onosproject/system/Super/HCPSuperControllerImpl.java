package org.onosproject.system.Super;

import org.onosproject.api.HCPDomain;
import org.onosproject.api.HCPDomainMessageListener;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.api.domain.HCPDomainListener;
import org.onosproject.hcp.protocol.HCPMessage;
import org.onosproject.hcp.protocol.HCPVersion;
import org.onosproject.hcp.types.DomainId;

import java.util.List;

/**
 * @Author ldy
 * @Date: 20-3-5 上午1:02
 * @Version 1.0
 */
public class HCPSuperControllerImpl implements HCPSuperController {

    @Override
    public HCPVersion getVersion() {
        return null;
    }

    @Override
    public void setHCPVersion(HCPVersion hcpVersion) {

    }

    @Override
    public int getHCPSuperPort() {
        return 0;
    }

    @Override
    public void setHCPSuperPort(int hcpSuperPort) {

    }

    @Override
    public String getHCPSuperIp() {
        return null;
    }

    @Override
    public void setHCPSuperIp(String hcpSuperIp) {

    }

    @Override
    public void addMessageListener(HCPDomainMessageListener listener) {

    }

    @Override
    public void removeMessageListener(HCPDomainMessageListener listener) {

    }

    @Override
    public void addHCPDomainListener(HCPDomainListener listener) {

    }

    @Override
    public void removeHCPDomainListener(HCPDomainListener listener) {

    }

    @Override
    public void sendHCPMessge(DomainId domainId, HCPMessage message) {

    }

    @Override
    public void addDomain(DomainId domainId, HCPDomain domain) {

    }

    @Override
    public void removeDomain(DomainId domainId) {

    }

    @Override
    public void processDownstremMessage(List<HCPMessage> messages) {

    }

    @Override
    public void processMessage(HCPMessage message) {

    }

    @Override
    public HCPDomain getHCPDomain(DomainId domainId) {
        return null;
    }
}
