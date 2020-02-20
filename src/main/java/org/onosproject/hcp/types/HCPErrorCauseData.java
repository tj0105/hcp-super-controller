package org.onosproject.hcp.types;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.execution.ChannelUpstreamEventRunnable;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.util.ChannelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.common.base.Optional;

public class HCPErrorCauseData implements Writeable,PrimitiveSinkable {
    private final static Logger logger=LoggerFactory.getLogger(HCPErrorCauseData.class);


    public final static HCPErrorCauseData NONE=new HCPErrorCauseData(new byte[0], HCPVersion.HCP_10);

    private final byte[] data;
    private final HCPVersion hcpVersion;

    public HCPErrorCauseData(byte [] data,HCPVersion hcpVersion){
        this.data=data;
        this.hcpVersion=hcpVersion;
    }

    public byte[] getData(){
        return Arrays.copyOf(data,data.length);
    }

    public Optional<HCPMessage> getParsedMessage(){
        HCPFactory factory= HCPFactories.getFactory(hcpVersion);
        try {
            HCPMessage msg=factory.getReader().readFrom(ChannelBuffers.wrappedBuffer(data));
            if (msg!=null){
                return Optional.of(msg);
            }
            else return Optional.absent();
        }catch (HCPParseError error){
            logger.debug("Error parsing error cause data as HCPMessage:{}",error.getMessage(),error);
            return Optional.absent();
        }
    }

    public static HCPErrorCauseData of(byte[] data,HCPVersion hcpVersion){
        return new HCPErrorCauseData(data,hcpVersion);
    }

    public static HCPErrorCauseData read(ChannelBuffer bb,int length,HCPVersion hcpVersion){
        byte[] data= ChannelUtils.readBytes(bb,length);
        return of(data,hcpVersion);
    }
    @Override
    public void writeTo(ChannelBuffer bb) {
        bb.writeBytes(data);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putBytes(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj)
            return true;
        if (obj==null)
            return false;
        if(getClass()!=obj.getClass())
            return false;
        HCPErrorCauseData hcpErrorCauseData=(HCPErrorCauseData)obj;
        if (!Arrays.equals(data,hcpErrorCauseData.data))
            return false;
        if (hcpVersion!=hcpErrorCauseData.hcpVersion)
            return false;
        return true;
    }
}
