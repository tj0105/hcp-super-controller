package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;

public interface HCPGetConfigRequest extends HCPObject,HCPMessage {
  HCPVersion getVersion();
  HCPType getType();
  long getXid();

  void writeTo(ChannelBuffer bb);

  Builder createBuilder();

  public interface Builder extends HCPMessage.Builder{
      HCPGetConfigRequest build();
      HCPVersion getVersion();
      HCPType getType();
      long getXid();
      Builder setXid(long xid);

  }
}
