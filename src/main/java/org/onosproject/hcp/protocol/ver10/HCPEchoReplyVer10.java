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
 * @Date: 20-2-22 下午1:17
 * @Version 1.0
 */
public class HCPEchoReplyVer10 implements HCPEchoReply{
   public static final Logger logger= LoggerFactory.getLogger(HCPEchoReply.class);

   //HCP version:1.0
   public final static byte WIRE_VERSION=(byte)0x1;
   public final static int MINIMUN_LENGTH=8;

   private final static long DEFAULT_ID=0x0L;
   private final static byte[] DEFAULT_DATA=new byte[0];

   //HCP echoReply message field
    private final long xid;
    private final byte[] data;

    HCPEchoReplyVer10(long xid,byte [] data){
        if (data==null){
            throw new NullPointerException("HCPEchoReplyVer10: property data must not be null");
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
        return HCPType.HCP_ECHO_REPLY;
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
        WRITER.write(bb,this);
    }
    final static Writer WRITER=new Writer();
    static class Writer implements HCPMessageWriter<HCPEchoReplyVer10>{
        @Override
        public void write(ChannelBuffer bb, HCPEchoReplyVer10 message) {
            int startIndex=bb.writerIndex();
            //version
            bb.writeByte(1);
            //type=3
            bb.writeByte(3);
            //temp length
            int lengthIndex=bb.writerIndex();
            bb.writeShort(lengthIndex);
            //xid
            bb.writeInt(U32.t(message.xid));
            //data
            bb.writeBytes(message.data);
            //update length
            int length=bb.writerIndex()-startIndex;
            bb.setShort(lengthIndex,length);
        }
    }

    final static Reader READER =new Reader();
    static class Reader implements HCPMessageReader<HCPEchoReply>{

        @Override
        public HCPEchoReply readFrom(ChannelBuffer bb) throws HCPParseError {
            int startIndex=bb.readerIndex();
            //version
            byte version=bb.readByte();
            //check version
            if(version!=(byte)0x1)
                throw new HCPParseError("Wrong version:Expected=HCPVersion.HCP_10(1), got="+version);
            //type
            byte type=bb.readByte();
            if (type!=(byte)0x3)
                throw new HCPParseError("Wrong type:Expected=HCPType.HCP_ECHO_REPLY(3),got="+type);
            int length= U16.f(bb.readShort());
            if(length<MINIMUN_LENGTH)
                throw new HCPParseError("Wrong length: Expected to be >= " + MINIMUN_LENGTH
                        + ", was: " + length);
            if (bb.readableBytes()+(bb.readerIndex()-startIndex)<length){
                bb.readerIndex(startIndex);
                return null;
            }
            //xid
            long xid= U32.f(bb.readInt());
            //data
            byte [] data= ChannelUtils.readBytes(bb,length-(bb.readerIndex()-startIndex));
            return new HCPEchoReplyVer10(xid,data);
        }
    }

    @Override
    public Builder createBuilder() {
        return null;
    }

    static class Builder implements HCPEchoReply.Builder{
        //HCP echoreply message field

        private boolean xidSet;
        private long xid;
        private boolean dataSet;
        private byte[] data;
        @Override
        public HCPEchoReply build() {
            long xid=this.xidSet ? this.xid:DEFAULT_ID;
            byte [] data=this.dataSet ? this.data:DEFAULT_DATA;
            if (data==null)
                throw new NullPointerException("HCPEchoReply property data must not be null");
            return new HCPEchoReplyVer10(xid,data);
        }

        @Override
        public HCPVersion getVersion() {
            return HCPVersion.HCP_10;
        }

        @Override
        public HCPType getType() {
            return HCPType.HCP_ECHO_REPLY;
        }

        @Override
        public long getXid() {
            return xid;
        }

        @Override
        public HCPEchoReply.Builder setXid(long xid) {
            this.xidSet=true;
            this.xid=xid;
            return this;
        }

        @Override
        public byte[] getData() {
            return data;
        }

        @Override
        public HCPEchoReply.Builder setData(byte[] data) {
            this.dataSet=true;
            this.data=data;
            return this;
        }
    }

    @Override
    public void putTo(PrimitiveSink sink) {

    }
    @Override
    public boolean equals(Object obj){
        if (this==obj)
            return true;
        if (obj==null)
            return false;
        if (getClass()!=obj.getClass())
            return false;
        HCPEchoReplyVer10 other=(HCPEchoReplyVer10) obj;
        if (xid!=other.xid)
            return false;
        if (!Arrays.equals(data,other.data))
            return false;
        return true;
    }
}
