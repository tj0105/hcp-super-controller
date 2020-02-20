package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;

/**
 * @Author ldy
 * @Date: 20-2-12 下午9:27
 * @Version 1.0
 */
public interface HCPMessageWriter<T> {
    void write(ChannelBuffer bb,T message) throws HCPParseError;
}
