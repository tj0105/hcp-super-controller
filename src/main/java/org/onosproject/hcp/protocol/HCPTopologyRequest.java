package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * @Author ldy
 * @Date: 20-2-13 下午10:17
 * @Version 1.0
 */
public interface HCPTopologyRequest extends HCPObject,HCPMessage{
    HCPVersion getVersion();
    HCPType getType();
    long getXid();

    void writeTo(ChannelBuffer bb);

    Builder createBuilder();

    public interface Builder extends HCPMessage.Builder{
        HCPTopologyRequest build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);
    }
}
