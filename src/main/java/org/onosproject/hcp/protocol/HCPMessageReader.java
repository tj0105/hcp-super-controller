package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;

public interface HCPMessageReader<T> {
    T readFrom(ChannelBuffer bb) throws HCPParseError;
}
