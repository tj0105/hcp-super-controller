package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.types.HCPErrorCauseData;

public interface HCPErrorMessage extends HCPObject,HCPMessage {
    HCPVersion getVersion();
    HCPType getType();
    long getXid();
    HCPErrorType getErrorType();
    HCPErrorCauseData getData();


    void writeTo(ChannelBuffer bb);

    Builder createBuilder();

    public interface Builder extends HCPMessage.Builder{
        HCPErrorMessage build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);
        HCPErrorType getErrorType();
        HCPErrorCauseData getData();
    }
}
