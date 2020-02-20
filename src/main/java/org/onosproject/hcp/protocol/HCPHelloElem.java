package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;

public interface HCPHelloElem extends HCPObject{
    int getType();
    HCPVersion getVersion();

    void writeTo(ChannelBuffer bb);

    Builder createBuilder();
    public interface Builder {
        HCPHelloElem build();
        int getType();
        HCPVersion getVersion();
    }
}
