package org.onosproject.hcp.protocol;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.types.HCPErrorCauseData;
import org.onosproject.hcp.types.PrimitiveSinkable;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class HCPSbpVersion implements Writeable,PrimitiveSinkable {
    private final static Logger logger= LoggerFactory.getLogger(HCPErrorCauseData.class);
    private final byte sbpVersion;
    private final HCPVersion hcpVersion;

    private HCPSbpVersion(byte sbpVersion,HCPVersion hcpVersion){
        this.sbpVersion=sbpVersion;
        this.hcpVersion=hcpVersion;
    }
    public byte getSbpVersion(){
        return sbpVersion;
    }

    public static HCPSbpVersion of(byte data,HCPVersion hcpVersion){
        return new HCPSbpVersion(data,hcpVersion);
    }

    public static HCPSbpVersion read(ChannelBuffer bb,HCPVersion hcpVersion){
        byte sbpversion=bb.readByte();
        return of(sbpversion,hcpVersion);

    }
    @Override
    public void writeTo(ChannelBuffer bb) {
        bb.writeByte(sbpVersion);
    }


    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putByte(sbpVersion);
    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj)
            return true;
        if (obj==null)
            return false;
        if (getClass()!=obj.getClass())
            return false;
        HCPSbpVersion other=(HCPSbpVersion)obj;
        if (sbpVersion!=other.sbpVersion)
            return false;
        if (hcpVersion!=other.hcpVersion)
            return false;
        return true;
    }
}
