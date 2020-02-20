package org.onosproject.hcp.types;

import com.google.common.base.Optional;
import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.util.ChannelUtils;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import java.util.Arrays;

public class HCPSbpData implements Writeable,PrimitiveSinkable {
    private final static Logger logger= LoggerFactory.getLogger(HCPErrorCauseData.class);

    public final static HCPSbpData NONE=new HCPSbpData(new byte[0], HCPVersion.HCP_10);

    private final byte[] data;
    private final HCPVersion version;

    private HCPSbpData(byte [] data,HCPVersion hcpVersion){
        this.data=data;
        this.version=hcpVersion;
    }

    public byte[] getData(){
        return Arrays.copyOf(data,data.length);
    }
    public int getLength(){
        return data==null?0:data.length ;
    }
    public static HCPSbpData of(byte [] data,HCPVersion hcpVersion){
        return new HCPSbpData(data,hcpVersion);
    }

    public static HCPSbpData read(ChannelBuffer bb,int length,HCPVersion hcpVersion){
        byte [] data=new byte[length];
        data= ChannelUtils.readBytes(bb,length);
        return of(data,hcpVersion);
    }

    public Optional<HCPMessage> getParseMessage(){
        HCPFactory factory= HCPFactories.getFactory(version);
        try {
            HCPMessage msg=factory.getReader().readFrom(ChannelBuffers.wrappedBuffer(data));
            if (msg!=null)
                return Optional.of(msg);
            else return Optional.absent();
        }
        catch (HCPParseError error){
            logger.debug("Error parsing error cause data as HCPMessage:{}",error.getMessage(),error);
            return Optional.absent();
        }
    }
    @Override
    public void writeTo(ChannelBuffer bb) {
        bb.writeBytes(data);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putBytes(data);
    }
}
