package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;

import java.util.List;

public interface HCPHello extends HCPObject,HCPMessage {
    HCPVersion getVersion();
    HCPType getType();
    long getXid();
    List<HCPHelloElem> getElements() throws UnsupportedOperationException;

    void writeTo(ChannelBuffer bb);

    Builder createBuilder();
    public interface Builder extends HCPMessage.Builder{
        HCPHello build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);
        List<HCPHelloElem>  getElements()  throws UnsupportedOperationException;
        Builder setElemntes(List<HCPHelloElem> elemntes) throws UnsupportedOperationException;
    }
}
