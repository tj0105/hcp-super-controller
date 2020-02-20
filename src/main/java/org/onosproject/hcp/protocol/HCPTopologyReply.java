package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.types.HCPInternalLink;

import java.util.List;

/**
 * @Author ldy
 * @Date: 20-2-13 下午10:17
 * @Version 1.0
 */
public interface HCPTopologyReply  extends HCPObject,HCPMessage{
    HCPVersion getVersion();
    HCPType getType();
    long getXid();
    List<HCPInternalLink> getInternalLink();
    void writeTo(ChannelBuffer bb);

    Builder creteBuilder();
    public interface Builder extends HCPMessage.Builder{
        HCPTopologyReply build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);
        List<HCPInternalLink> getInternalLink();
        Builder setInternalLink(List<HCPInternalLink> InternalLinklist);

    }
}
