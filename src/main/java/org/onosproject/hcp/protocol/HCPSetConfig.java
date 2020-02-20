package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;

import java.util.Set;

/**
 * @Author ldy
 * @Date: 20-2-13 下午7:59
 * @Version 1.0
 */
public interface HCPSetConfig extends HCPObject,HCPMessage {
    HCPVersion getVersion();
    HCPType getType();
    long getXid();
    Set<HCPConfigFlags> getFlags();
    byte getPeriod();
    short getMissSendLength();

    void writeTo(ChannelBuffer bb);

    Builder createBuilder();

    public interface Builder extends HCPMessage.Builder{
        HCPSetConfig build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);
        Set<HCPConfigFlags> getFlags();
        Builder setFlags(Set<HCPConfigFlags> flags);
        byte getPeriod();
        Builder setPeriod(byte period);
        short getMissSendLength();
        Builder setMissSendLength(short missSendLength);
    }
}
