package org.onosproject.hcp.util;

import com.google.common.collect.ImmutableList;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.HCPMessageReader;
import org.onosproject.hcp.protocol.Writeable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ChannelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ChannelUtils.class);

    public static byte[] readBytes(final ChannelBuffer bb,final int length){
        byte[] data=new byte[length];
        bb.readBytes(data);
        return data;
    }

    public static <T> List<T> readList(ChannelBuffer bb, int length, HCPMessageReader<T> reader) throws HCPParseError {
        int end = bb.readerIndex() + length;
        ImmutableList.Builder<T> builder = ImmutableList.<T>builder();
        if(logger.isTraceEnabled())
            logger.trace("readList(length={}, reader={})", length, reader.getClass());
        while(bb.readerIndex() < end) {
            T read = reader.readFrom(bb);
            if(logger.isTraceEnabled())
                logger.trace("readList: read={}, left={}", read, end - bb.readerIndex());
            builder.add(read);
        }
        if(bb.readerIndex() != end) {
            throw new IllegalStateException("Overread length: length="+length + " overread by "+ (bb.readerIndex() - end) + " reader: "+reader);
        }
        return builder.build();
    }

    public static void writeList(ChannelBuffer bb, List<? extends Writeable> writeables) {
        for(Writeable w: writeables)
            w.writeTo(bb);
    }
}
