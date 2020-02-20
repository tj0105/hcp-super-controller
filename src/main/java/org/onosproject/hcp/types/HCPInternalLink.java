package org.onosproject.hcp.types;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.omg.PortableInterceptor.INACTIVE;
import org.onosproject.hcp.protocol.Writeable;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author ldy
 * @Date: 20-2-15 下午8:34
 * @Version 1.0
 */
public class HCPInternalLink implements Writeable,PrimitiveSinkable {
    private final static Logger logger= LoggerFactory.getLogger(HCPInternalLink.class);


    private final HCPVport srcVPort;
    private final HCPVport dstVport;
    private final long capability;

    HCPInternalLink(HCPVport srcVPort,HCPVport dstVport,long capability){
        this.srcVPort=srcVPort;
        this.dstVport=dstVport;
        this.capability=capability;
    }

    public HCPVport getDstVport() {
        return dstVport;
    }

    public HCPVport getSrcVPort() {
        return srcVPort;
    }

    public long getCapability() {
        return capability;
    }

    @Override
    public void writeTo(ChannelBuffer bb) {
        srcVPort.writeTo(bb);
        dstVport.writeTo(bb);
        bb.writeLong(capability);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        srcVPort.putTo(sink);
        dstVport.putTo(sink);
        sink.putLong(capability);
    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj){
            return true;
        }
        if (obj==null)
            return false;
        if (getClass()!=obj.getClass())
            return false;
        HCPInternalLink other=(HCPInternalLink)obj ;
        if (this.srcVPort==null){
            if(other.srcVPort!=null)
                return false;
        }else if (!this.srcVPort.equals(other.srcVPort))
            return false;
        if (this.dstVport==null){
            if (other.dstVport!=null)
                return false;
        }
        else if (!this.srcVPort.equals(other.srcVPort)){
            return false;
        }
        if (this.capability!=other.capability)
            return false;
        return true;
    }
}
