package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.types.DomainId;

import java.util.Set;

public interface HCPFeaturesReply extends HCPObject,HCPMessage {
    HCPVersion getVersion();
    HCPType getType();
    long getXid();
    DomainId getDomainId();
    HCPSbpType getSbpType();
    HCPSbpVersion getSbpVersion();
    Set<HCPCapabilities> getCapabilities();

    void writeTo(ChannelBuffer bb);

    Builder createBuilder();

    public interface Builder extends HCPMessage.Builder{
        HCPFeaturesReply build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);
        DomainId getDomainId();
        Builder setDomainId(DomainId domainId);
        HCPSbpType getSbpType();
        Builder setSbpType(HCPSbpType hcpSbpType);
        HCPSbpVersion getSbpVersion();
        Builder setSbpVersion(HCPSbpVersion hcpSbpVersion);
        Set<HCPCapabilities> getCapabilities();
        Builder setCapabilities(Set<HCPCapabilities> hcpCapabilities);
    }
}
