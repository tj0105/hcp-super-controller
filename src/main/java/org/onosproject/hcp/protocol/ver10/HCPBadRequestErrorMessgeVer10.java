package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.protocol.errormsg.HCPBadRequestErrorMsg;
import org.onosproject.hcp.types.HCPErrorCauseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * @Author ldy
 * @Date: 20-2-21 下午4:08
 * @Version 1.0
 */
public class HCPBadRequestErrorMessgeVer10 implements HCPBadRequestErrorMsg {

    public final static Logger logger= LoggerFactory.getLogger(HCPBadRequestErrorMessgeVer10.class);

    //version
    private final static byte WIRE_VERSION=1;
    //length
    private final static int MINIMUM_LENGTH=12;

    //static id
    private final static long DEFAULT_XID=0x01;
    private final static HCPErrorCauseData DEFAULT_DATA=HCPErrorCauseData.NONE;

    //HCPBadRequest msg field;
    private final long xid;
    private final HCPBadRequestCode BadCode;
    private final HCPErrorCauseData ErrorData;

    HCPBadRequestErrorMessgeVer10(long xid,HCPBadRequestCode code,HCPErrorCauseData data){
        if (code == null)
            throw new NullPointerException("HCPBadRequestErrorMessgeVer10: property code cannot be null");
        if(data == null) {
            throw new NullPointerException("HCPBadRequestErrorMessgeVer10: property data cannot be null");
        }
        this.xid=xid;
        this.BadCode=code;
        this.ErrorData=data;
    }


    @Override
    public HCPVersion getVersion() {
        return HCPVersion.HCP_10;
    }

    @Override
    public HCPType getType() {
        return  HCPType.HCP_ERROR;
    }

    @Override
    public long getXid() {
        return xid;
    }

    @Override
    public HCPErrorType getErrorType() {
        return HCPErrorType.BAD_REQUEST;
    }

    @Override
    public HCPBadRequestCode getCode() {
        return null;
    }

    @Override
    public HCPErrorCauseData getData() {
        return ErrorData;
    }

    @Override
    public void writeTo(ChannelBuffer bb) {
        WRITER.write(bb,this);
    }

    @Override
    public HCPErrorMessage.Builder createBuilder() {
        return null;
    }

    @Override
    public void putTo(PrimitiveSink sink) {

    }
    final static Writer WRITER=new Writer();
    static class Writer implements HCPMessageWriter<HCPBadRequestErrorMessgeVer10>{

        @Override
        public void write(ChannelBuffer bb, HCPBadRequestErrorMessgeVer10 message) {
            int startInder=bb.writerIndex();
            //version
            bb.writeByte(0x1);
            //type
            bb.writeByte(0x1);
            //temp legth
            int lengthIndex=bb.writerIndex();
            bb.writeShort(1);
            //xid
            bb.writeInt((int)message.xid);
            //error Type
            bb.writeShort(0x1);
            //error Code
            HCPBadRequestCodeSerialiserVer10.writeTo(bb,message.BadCode);
            //error data
            message.ErrorData.writeTo(bb);
            int length=bb.writerIndex()-startInder;
            bb.setShort(lengthIndex,length);
        }
    }
    final static Reader READER=new Reader();
    static class Reader implements HCPMessageReader<HCPBadRequestErrorMsg>{

        @Override
        public HCPBadRequestErrorMsg readFrom(ChannelBuffer bb) throws HCPParseError {
            // record current read index;
            int start =bb.readerIndex();
            //version
            byte version=bb.readByte();
            //HCP type
            byte type=bb.readByte();
            //length
            short length=bb.readShort();
            if (length<MINIMUM_LENGTH)
                throw new HCPParseError("Wrong length: Excepted to be>="+MINIMUM_LENGTH+", was:"+length);
            if (bb.readableBytes()+(bb.readerIndex()-start)<length){
                bb.readerIndex(start);
                return null;
            }
            //xid
            long xid=bb.readInt();
            //ERROR Type
            short errorType=bb.readShort();
            //errCode
            HCPBadRequestCode code=HCPBadRequestCodeSerialiserVer10.readFrom(bb);
            //errData
            HCPErrorCauseData data=HCPErrorCauseData.readFrom(bb,length-(bb.readerIndex()-start),HCPVersion.HCP_10);
            //requestError message;
            HCPBadRequestErrorMsg badRequestErrorMsg=new HCPBadRequestErrorMessgeVer10(xid,code,data);
            return badRequestErrorMsg;
        }
    }

    static class Builder implements HCPBadRequestErrorMsg.Builder{
        private boolean xidSet;
        private long xid;
        private boolean codeSet;
        private HCPBadRequestCode code;
        private boolean dataSet;
        private HCPErrorCauseData data;
        @Override
        public HCPBadRequestErrorMsg build() {
            long xid=this.xidSet?this.xid:DEFAULT_XID;
            if (!this.codeSet)
                throw new IllegalStateException("Property code doesn't have default value -- must be set");
            if (code == null)
                throw new NullPointerException("Property code must not be null");
             HCPErrorCauseData data=this.dataSet?this.data:DEFAULT_DATA;
            if (data == null)
                throw new NullPointerException("Property data must not be null");
            return new HCPBadRequestErrorMessgeVer10(xid,code,data);
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
        public HCPBadRequestErrorMsg.Builder setXid(long xid) {
            this.xidSet=true;
            this.xid=xid;
            return this;

        }

        @Override
        public HCPErrorType getErrorType() {
            return HCPErrorType.BAD_REQUEST;
        }

        @Override
        public HCPBadRequestCode getCode() {
            return code;
        }

        @Override
        public HCPBadRequestErrorMsg.Builder setCode(HCPBadRequestCode code) {
            this.codeSet=true;
            this.code=code;
            return this;
        }

        @Override
        public HCPErrorCauseData getData() {
            return data;
        }

        @Override
        public HCPBadRequestErrorMsg.Builder setData(HCPErrorCauseData data) {
            this.dataSet=true;
            this.data=data;
            return this;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj)
            return true;
        if (obj==null)
            return false;
        if (getClass()!=obj.getClass())
            return false;
        HCPBadRequestErrorMessgeVer10 other=(HCPBadRequestErrorMessgeVer10) obj;
        if (xid!=other.xid)
            return false;
        if (BadCode==null){
            if (other.BadCode!=null)
                return false;
        }else if(!BadCode.equals(other.BadCode))
            return false;
        if(ErrorData==null){
            if(other.ErrorData!=null)
                return false;
        }else if(!ErrorData.equals(other.ErrorData))
            return false;
        return true;
    }
}
