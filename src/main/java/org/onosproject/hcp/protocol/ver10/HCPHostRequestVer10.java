package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.types.U16;
import org.onosproject.hcp.types.U32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlType;

/**
 * @Author ldy
 * @Date: 20-2-23 上午12:00
 * @Version 1.0
 */
public class HCPHostRequestVer10 implements HCPHostRequest {

    public final static Logger logger= LoggerFactory.getLogger(HCPHostRequestVer10.class);

    //version
    private static final byte WIRE_VERSION=1;
    private static final int LENGTH=8;

    private static final long DAFAULT_XID=0x0L;

    //hcp host update message field
    private final long xid;

    HCPHostRequestVer10(long xid){
        this.xid=xid;
    }
    @Override
    public HCPVersion getVersion() {
        return HCPVersion.HCP_10;
    }

    @Override
    public HCPType getType() {
        return HCPType.HCP_HOST_REQUEST;
    }

    @Override
    public long getXid() {
        return xid;
    }

    @Override
    public void writeTo(ChannelBuffer bb) {
        WRITER.write(bb,this);
    }

    static final Writer WRITER=new Writer();
    static class Writer implements HCPMessageWriter<HCPHostRequestVer10>{

        @Override
        public void write(ChannelBuffer bb, HCPHostRequestVer10 message)  {
            //version
            bb.writeByte(WIRE_VERSION);
            //type
            bb.writeByte(HCPType.HCP_HOST_REQUEST.value());
            //length
            bb.writeShort(LENGTH);
            //xid
            bb.writeInt((int) message.xid);
        }
    }

    static final Reader READER=new Reader();
    static class Reader implements HCPMessageReader<HCPHostRequest>{

        @Override
        public HCPHostRequest readFrom(ChannelBuffer bb) throws HCPParseError {
            int startIndex=bb.readerIndex();
            //version
            byte version=bb.readByte();
            if(version!=(byte)0x1)
                throw new HCPParseError("Wrong version:Expected=HCPVersion.HCP_10(1), got="+version);
            //type
            byte type=bb.readByte();
            if (type!=(byte)HCPType.HCP_HOST_REQUEST.value())
                throw new HCPParseError("Wrong type:Expected=HCPType.HCP_HOST_REQUEST(11),got="+type);
            int length= U16.f(bb.readShort());
            if(length!=LENGTH)
                throw new HCPParseError("Wrong length: Expected to be = " + LENGTH
                        + ", was: " + length);
            if (bb.readableBytes()+(bb.readerIndex()-startIndex)<length){
                bb.readerIndex(startIndex);
                return null;
            }
            //xid
            long xid= U32.f(bb.readInt());
            return new HCPHostRequestVer10(xid);
        }
    }
    static class Builder implements HCPHostRequest.Builder{
        private boolean xidSet;
        private long xid;
        @Override
        public HCPHostRequest build() {
            long xid=this.xidSet? this.xid: DAFAULT_XID;
            return new HCPHostRequestVer10(xid);
        }

        @Override
        public HCPVersion getVersion() {
            return HCPVersion.HCP_10;
        }

        @Override
        public HCPType getType() {
            return HCPType.HCP_HOST_REQUEST;
        }

        @Override
        public long getXid() {
            return xid;
        }

        @Override
        public HCPHostRequest.Builder setXid(long xid) {
            this.xidSet=true;
            this.xid=xid;
            return this;
        }
    }
    @Override
    public Builder createBuilder() {
        return null;
    }

    @Override
    public void putTo(PrimitiveSink sink) {

    }


    @Override
    public String toString() {
            StringBuilder b=new StringBuilder("HCPHostRequest:");
            b.append("xid=").append(xid);
            return b.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj)
            return true;
        if (obj==null)
            return false;
        if (getClass()!=obj.getClass())
            return false;
        HCPHostRequestVer10 other=(HCPHostRequestVer10) obj;
        if (xid!=other.xid)
            return false;
        return true;
    }
}
