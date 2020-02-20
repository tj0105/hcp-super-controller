package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.hcp.types.HCPHost;

import java.util.List;

/**
 * @Author ldy
 * @Date: 20-2-13 下午7:39
 * @Version 1.0
 */
public interface HCPHostUpdate extends HCPObject,HCPMessage {
   HCPVersion getVersion();
   HCPType getType();
   long getXid();
   DomainId getDomainId();
   List<HCPHost> getHosts();

   void writeTo(ChannelBuffer bb);

   Builder createBuilder();

   public interface Builder extends HCPMessage.Builder{
       HCPHostUpdate build();
       HCPVersion getVersion();
       HCPType getType();
       long getXid();
       Builder setXid(long xid);
       DomainId getDomainId();
       Builder setDomainId(DomainId domainId);
       List<HCPHost> getHosts();
       Builder setHosts(List<HCPHost> hosts);
   }
}
