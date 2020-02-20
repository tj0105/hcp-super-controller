package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;

public interface HCPEchoReply extends HCPObject,HCPMessage {
    HCPVersion getVersion();
    HCPType getType();
    long getXid();
    byte [] getData();

    void writeTo(ChannelBuffer bb);
    Builder createBuilder();

    public interface Builder extends HCPMessage.Builder{
        HCPEchoReply build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);
        byte[] getData();
        Builder setData(byte[] data);

    }
}
