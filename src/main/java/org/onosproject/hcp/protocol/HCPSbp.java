package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.types.HCPSbpData;

import java.nio.channels.Channel;
import java.util.Set;

public interface HCPSbp extends HCPObject,HCPMessage {
    HCPVersion getVersion();
    HCPType getType();
    long getXid();
    HCPSbpCmpType getSbpCmpType();
    Set<HCPSbpFlags> getFlags();
    short getDataLength();
    long getSbpXid();
    HCPSbpCmpData getSbpCmpData();
    HCPSbpData getSbpData();

    void writeTo(ChannelBuffer bb);
    Builder createBuilder();
    public interface Builder extends HCPMessage.Builder{
        HCPSbp build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);
        HCPSbpCmpType getSbpCmpType();
        Builder setSbpCmpType(HCPSbpCmpType sbpCmpType);
        Set<HCPSbpFlags> getFlags();
        Builder setFlags(Set<HCPSbpFlags> flagsSet);
        short getDataLength();
        Builder setDataLength(short length);
        long getSbpXid();
        Builder setSbpXid(long xid);
        HCPSbpData getSbpData();
        Builder setSbpData(HCPSbpData hcpSbpData);
        HCPSbpCmpData getSbpCmpData();
        Builder setSbpCmpData(HCPSbpCmpData sbpCmpData);


    }
}
