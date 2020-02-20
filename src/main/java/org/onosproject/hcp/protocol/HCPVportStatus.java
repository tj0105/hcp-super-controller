package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * @Author ldy
 * @Date: 20-2-14 下午10:11
 * @Version 1.0
 */
public interface HCPVportStatus extends HCPObject,HCPMessage  {

    HCPVersion getVersion();
    HCPType getType();
    long getXid();
    HCPVportDescribtion getVportDescribtion();
    HCPVportReason getReason();

    void writeTo(ChannelBuffer bb);
    Builder createBuilder();

    public interface Builder extends HCPMessage.Builder{
        HCPVportStatus build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);
        HCPVportReason getReason();
        Builder setReson(HCPVportReason reason) ;
        HCPVportDescribtion getVportDescribtion();
        Builder setVportDescribtion(HCPVportDescribtion vportDescribtion);

    }

}
