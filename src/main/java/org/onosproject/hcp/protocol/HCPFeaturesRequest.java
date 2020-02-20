package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;

public interface HCPFeaturesRequest extends HCPObject,HCPMessage {
    HCPVersion getVersion();
    HCPType getType();
    long getXid();

    void writeTo(ChannelBuffer bb);

    Builder createBuilder();

    public interface Builder extends HCPMessage.Builder{
        HCPFeaturesRequest build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);
    }
}
