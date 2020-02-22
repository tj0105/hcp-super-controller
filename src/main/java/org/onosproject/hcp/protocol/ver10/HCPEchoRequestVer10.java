package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.types.U16;
import org.onosproject.hcp.types.U32;
import org.onosproject.hcp.util.ChannelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @Author ldy
 * @Date: 20-2-22 下午2:21
 * @Version 1.0
 */
public class HCPEchoRequestVer10 implements HCPEchoRequest{
    public static final Logger logger= LoggerFactory.getLogger(HCPEchoRequestVer10.class);
    //hcp version:1.0
    public final byte WIRE_VERSION=1;
    public static int MINIMUM_LENGTH=8;

    private final static long DEFAULT_ID=0x0L;
    private final static byte [] DEFAULT_DATA=new byte[0];


    //hcp echo request message field
    private final long xid;
    private final byte[] data;

    HCPEchoRequestVer10(long xid,byte[] data){
        if(data==null){
            throw new NullPointerException("HCPEchoRequestVer10: property data must not be null");
        }
        this.xid=xid;
        this.data=data;
    }
    @Override
    public HCPVersion getVersion() {
        return HCPVersion.HCP_10;
    }

    @Override
    public HCPType getType() {
        return HCPType.HCP_ECHO_REQUEST;
    }

    @Override
    public long getXid() {
        return xid;
    }

    @Override
    public byte[] getData() {
        return data;
    }
    @Override
    public void writeTo(ChannelBuffer bb) {

    }

    final static Writer WRITER=new Writer();
    static class Writer implements HCPMessageWriter<HCPEchoRequestVer10> {

        @Override
        public void write(ChannelBuffer bb, HCPEchoRequestVer10 message) throws HCPParseError {
            int startIndex=bb.writerIndex();
            //version=1;
            bb.writeByte(1);
            //type=2;
            bb.writeByte(2);
            //temp length
            int lengthIndex=bb.writerIndex();
            //xid
            bb.writeInt(U32.t(message.xid));
            //data
            bb.writeBytes(message.data);
            //updata length
            int length=bb.writerIndex()-startIndex;
            bb.setShort(lengthIndex,length);
        }
    }
    final static Reader READER=new Reader();
    static class Reader implements HCPMessageReader<HCPEchoRequest>{
        @Override
        public HCPEchoRequest readFrom(ChannelBuffer bb) throws HCPParseError {
            int startIndex=bb.readerIndex();
            //version
            byte version=bb.readByte();
            if(version!=(byte)0x1)
                throw new HCPParseError("Wrong version:Expected=HCPVersion.HCP_10(1), got="+version);
            //type
            byte type=bb.readByte();
            if (type!=(byte)0x2)
                throw new HCPParseError("Wrong type:Expected=HCPType.HCP_ECHo_REQUEST(2),got="+type);
            int length= U16.f(bb.readShort());
            if(length<MINIMUM_LENGTH)
                throw new HCPParseError("Wrong length: Expected to be >= " + MINIMUM_LENGTH
                        + ", was: " + length);
            if (bb.readableBytes()+(bb.readerIndex()-startIndex)<length){
                bb.readerIndex(startIndex);
                return null;
            }
            //xid
            long xid= U32.f(bb.readInt());
            byte []data= ChannelUtils.readBytes(bb,length-(bb.readerIndex()-startIndex));
            return new HCPEchoRequestVer10(xid,data);
        }
    }

    static class Builder implements HCPEchoRequest.Builder{
        //HCP echo request message field
        private boolean xidSet;
        private long xid;
        private boolean dataSet;
        private byte[] data;

        @Override
        public HCPEchoRequest build() {
            long xid=this.xidSet ? this.xid:DEFAULT_ID;
            byte []date=this.dataSet ? this.data:DEFAULT_DATA;
            if (data==null){
                throw new NullPointerException("HCPEchoRequestVer10:property data must not be null");
            }
            return new HCPEchoRequestVer10(xid,data);
        }

        @Override
        public HCPVersion getVersion() {
            return HCPVersion.HCP_10;
        }

        @Override
        public HCPType getType() {
            return HCPType.HCP_ECHO_REQUEST;
        }

        @Override
        public long getXid() {
            return xid;
        }

        @Override
        public HCPEchoRequest.Builder setXid(long xid) {
            this.xidSet=true;
            this.xid=xid;
            return this;
        }

        @Override
        public byte[] getData() {
            return data;
        }

        @Override
        public HCPEchoRequest.Builder setData(byte[] data) {
            this.dataSet=true;
            this.data=data;
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
    public boolean equals(Object obj) {
        if (this==obj)
            return true;
        if (obj==null)
            return false;
        if (getClass()!=obj.getClass())
            return false;
        HCPEchoRequestVer10 other=(HCPEchoRequestVer10)obj;
        if (xid!=other.xid)
            return false;
        if (!Arrays.equals(data,other.data))
            return false;
        return true;
    }
}
