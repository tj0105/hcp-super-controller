package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.protocol.errormsg.HCPDomainConfigFailedErrorMessage;
import org.onosproject.hcp.types.HCPErrorCauseData;
import org.onosproject.hcp.types.U32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author ldy
 * @Date: 20-2-21 下午10:40
 * @Version 1.0
 */
public class HCPDomainConfigFailedErrorMessageVer10 implements HCPDomainConfigFailedErrorMessage {

    private static final Logger logger= LoggerFactory.getLogger(HCPDomainConfigFailedErrorMessageVer10.class);

    public final static byte WIRE_VERSION=1;
    public final static int MINIMUM_LENGTH=12;

    private final static long DEFAULT_XID=0x0L;
    private final static HCPErrorCauseData DEFAULT_DATA=HCPErrorCauseData.NONE;

    //HCP Error message of Domain Config failed
    private final long xid;
    private final HCPDomainConfigFaliedCode code;
    private final HCPErrorCauseData data;

    HCPDomainConfigFailedErrorMessageVer10(long xid,HCPDomainConfigFaliedCode code,HCPErrorCauseData data){
        if (code == null)
            throw new NullPointerException("HCPDomainConfigFailedErrorMessageVer10: property code cannot be null");
        if(data == null) {
            throw new NullPointerException("HCPDomainConfigFailedErrorMessageVer10: property data cannot be null");
        }
        this.xid=xid;
        this.code=code;
        this.data=data;
    }
    @Override
    public HCPVersion getVersion() {
        return HCPVersion.HCP_10;
    }

    @Override
    public HCPType getType() {
        return HCPType.HCP_ERROR;
    }

    @Override
    public long getXid() {
        return xid;
    }

    @Override
    public HCPErrorType getErrorType() {
        return HCPErrorType.DOMAIN_CONFIG_FAILED;
    }

    @Override
    public HCPDomainConfigFaliedCode getCode() {
        return code;
    }

    @Override
    public HCPErrorCauseData getData() {
        return data;
    }

    @Override
    public void writeTo(ChannelBuffer bb) {
        WRITER.write(bb,this);
    }
    final static Writer WRITER=new Writer();
    static class Writer implements HCPMessageWriter<HCPDomainConfigFailedErrorMessageVer10>{

        @Override
        public void write(ChannelBuffer bb, HCPDomainConfigFailedErrorMessageVer10 message) {
            int startIndex = bb.writerIndex();
            //version = 1
            bb.writeByte(0x1);
            //type = 1;
            bb.writeByte(0x1);
            //tmp length
            int lengthIndex = bb.writerIndex();
            bb.writeShort(0);
            //xid
            bb.writeInt((int) message.xid);

            //errType = 2
            bb.writeShort(0x2);
            //errCode
            HCPDomainConfigFailedCodeSerializerVer10.writeTo(bb,message.code);
            //errData
            message.data.writeTo(bb);
            //update length
            int length = bb.writerIndex() - startIndex;
            bb.setShort(lengthIndex, length);
        }
    }
    final static Reader READER=new Reader();
    static final class Reader implements HCPMessageReader<HCPDomainConfigFailedErrorMessage> {

        @Override
        public HCPDomainConfigFailedErrorMessage readFrom(ChannelBuffer bb) throws HCPParseError {
            int startIndex=bb.readerIndex();
            //version
            byte version = bb.readByte();
            if (version!=(byte)0x1)
                throw new HCPParseError("Wrong version:Expected=HCPVersion.HCP_10(1), got="+version);
            //type
            byte type=bb.readByte();
            if (type!=(byte)0x1)
                throw new HCPParseError("Wrong type:Expected=HCPType.HCP_ERROR,got="+type);
            //length
            int length = bb.readShort();
            if (length < MINIMUM_LENGTH)
                throw new HCPParseError("Wrong length: Expected to be >= " + MINIMUM_LENGTH + ", was: " + length);
            if (bb.readableBytes() + (bb.readerIndex() - startIndex) < length) {
                bb.readerIndex(startIndex);
                return null;
            }
            //xid
            long xid = U32.f(bb.readInt());
            //errType
            short errType = bb.readShort();
            //errCode
            HCPDomainConfigFaliedCode code=HCPDomainConfigFailedCodeSerializerVer10.readFrom(bb);
            //errData
            HCPErrorCauseData data = HCPErrorCauseData.readFrom(bb, length - (bb.readerIndex() - startIndex), HCPVersion.HCP_10);
            HCPDomainConfigFailedErrorMessage message=new HCPDomainConfigFailedErrorMessageVer10(xid,code,data);
            return message;
        }
    }

    static class Builder implements HCPDomainConfigFailedErrorMessage.Builder{
        private boolean xidSet;
        private long xid;
        private boolean codeSet;
        private HCPDomainConfigFaliedCode code;
        private boolean dataSet;
        private HCPErrorCauseData data;
        @Override
        public HCPDomainConfigFailedErrorMessage build() {
            long xid=this.xidSet? this.xid:DEFAULT_XID;
            if (!this.codeSet)
                throw new IllegalStateException("Property code doesn't have default value -- must be set");
            if(code == null)
                throw new NullPointerException("Property code must not be null");
            HCPErrorCauseData data=this.dataSet?this.data:DEFAULT_DATA;
            if (data==null)
                throw new NullPointerException("Property data must not be null");
            return new HCPDomainConfigFailedErrorMessageVer10(xid,code,data);
        }

        @Override
        public HCPVersion getVersion() {
            return HCPVersion.HCP_10;

        }

        @Override
        public HCPType getType() {
            return HCPType.HCP_ERROR;
        }

        @Override
        public long getXid() {
            return xid;
        }

        @Override
        public HCPDomainConfigFailedErrorMessage.Builder setXid(long xid) {
            this.xidSet=true;
            this.xid=xid;
            return this;
        }

        @Override
        public HCPErrorType getErrorType() {
            return HCPErrorType.DOMAIN_CONFIG_FAILED;
        }

        @Override
        public HCPDomainConfigFaliedCode getCode() {
            return code;
        }

        @Override
        public HCPDomainConfigFailedErrorMessage.Builder setCode(HCPDomainConfigFaliedCode faildecode) {
             this.codeSet=true;
             this.code=code;
             return this;
        }

        @Override
        public HCPErrorCauseData getData() {
            return data;
        }

        @Override
        public HCPDomainConfigFailedErrorMessage.Builder setData(HCPErrorCauseData data) {
            this.dataSet=true;
            this.data=data;
            return this;
        }
    }
    @Override
    public HCPErrorMessage.Builder createBuilder() {
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
        HCPDomainConfigFailedErrorMessageVer10 other=(HCPDomainConfigFailedErrorMessageVer10) obj;
        if (xid!=other.xid)
            return false;
        if (code==null){
            if (other.code!=null)
                return false;
        }else if(!code.equals(other.code))
            return false;
        if(data==null){
            if(other.data!=null)
                return false;
        }else if(!data.equals(other.data))
            return false;
        return true;
    }
}
