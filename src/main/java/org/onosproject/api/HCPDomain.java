package org.onosproject.api;

import org.jboss.netty.channel.Channel;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.hcp.types.HCPVport;
import org.onosproject.net.DeviceId;

import java.util.List;
import java.util.Set;

/**
 * @Author ldy
 * @Date: 20-3-4 下午9:22
 * @Version 1.0
 */
public interface HCPDomain {
    void sendMsg(HCPMessage message);
    void sendMsg(List<HCPMessage> messageList);

    void handleMessage(HCPMessage message);

    HCPFactory factory();

    void setConnected(boolean isConnected);

    boolean isConnected();

    String channleId();

    void setChannel(Channel channel);

    DeviceId getDeviceId();
    void setDeviceId(DeviceId deviceId);

    Set<HCPConfigFlags> getFlags();
    void setFlags(Set<HCPConfigFlags> flags);

    int getPeriod();
    void setPeriod(int period);

    long getMissSendLength();
    void setMissSendLength(long missSendLength);

    Set<HCPCapabilities> getCapabilities();
    void setCapabilities(Set<HCPCapabilities> capabilities);

    DomainId getDomainId();
    void setDomainId(DomainId domainId);

    HCPSbpType getHCPSbpType();
    void setHCPSbpType(HCPSbpType sbpType);

    HCPSbpVersion getHCPSbpVersion();
    void setHCPSbpVersion(HCPSbpVersion hcpSbpVersion);

    int getDomainPort();
    void setDomainPort(int hcpDomainPort);

    String getDomainIp();
    void setDomainIp(String hcpDomainIp);

    HCPVersion getHCPVersion();
    void setHCPVersion(HCPVersion hcpVersion);
}
