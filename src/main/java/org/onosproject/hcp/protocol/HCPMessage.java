package org.onosproject.hcp.protocol;

import javafx.util.Builder;
import org.jboss.netty.buffer.ChannelBuffer;

public interface HCPMessage extends HCPObject {
    HCPVersion getVersion();
    HCPType getType();
    long getXid();

    void writeTo(ChannelBuffer bb);

    Builder createBuilder();
    public interface Builder{
        HCPMessage build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);
    }
}
