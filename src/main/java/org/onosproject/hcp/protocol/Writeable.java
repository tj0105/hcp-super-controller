package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;

public interface Writeable {
    void writeTo(ChannelBuffer bb);
}
