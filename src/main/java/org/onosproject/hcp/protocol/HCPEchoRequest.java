package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;

public interface HCPEchoRequest extends HCPObject,HCPMessage{
    HCPVersion getVersion();
    HCPType getType();
    long getXid();
    byte [] getData();

    void writeTo(ChannelBuffer bb) throws HCPParseError;

    Builder createBuilder();
    public interface Builder extends HCPMessage.Builder{
        HCPEchoRequest build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);
        byte[] getData();
        Builder setData(byte [] data);
    }
}
