package org.onosproject.hcp.util;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ChannelUtils.class);

    public static byte[] readBytes(final ChannelBuffer bb,final int length){
        byte[] data=new byte[length];
        bb.readBytes(data);
        return data;
    }
}
