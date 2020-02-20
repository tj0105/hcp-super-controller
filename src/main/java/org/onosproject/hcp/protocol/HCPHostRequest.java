package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * @Author ldy
 * @Date: 20-2-13 下午7:11
 * @Version 1.0
 */
public interface HCPHostRequest extends HCPMessage,HCPObject {
    HCPVersion getVersion();
    HCPType getType();
    long getXid();

    void writeTo(ChannelBuffer bb);

    Builder createBuilder();

    public interface Builder extends HCPMessage.Builder{
        HCPHostRequest build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);

    }
}
