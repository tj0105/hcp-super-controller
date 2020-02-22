package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.protocol.errormsg.HCPHelloFailedErrorMessage;
import org.onosproject.hcp.types.HCPErrorCauseData;
import org.onosproject.hcp.types.U16;
import org.onosproject.hcp.types.U32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.attribute.standard.NumberUp;

/**
 * @Author ldy
 * @Date: 20-2-22 上午10:34
 * @Version 1.0
 */
public class HCPHelloFailedCodeErrorMessageVer10 implements HCPHelloFailedErrorMessage {
    public final static Logger logger= LoggerFactory.getLogger(HCPHelloFailedCodeErrorMessageVer10.class);

    final static byte WIRE_VERSION=1;
    final static int MINIMUM_LENGTH=12;

    private final static long DEFAULT_ID=0x0L;
    private final static HCPErrorCauseData DAFAULT_DATA=HCPErrorCauseData.NONE;

    //hcp error hello message field
    private final long xid;
    private final HCPHelloFailedCode code;
    private final HCPErrorCauseData data;

    HCPHelloFailedCodeErrorMessageVer10(long xid,HCPHelloFailedCode code,HCPErrorCauseData data){
        if (code==null)
            throw new NullPointerException("HCPHelloFailedCodeErrorMessageVer10: property code must not be null");
        if (data==null)
            throw new NullPointerException("HCPHelloFailedCodeErrorMessageVer10: property data must not be null");
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
        return HCPErrorType.HELLO_FAILED;
    }

    @Override
    public HCPHelloFailedCode getCode() {
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
    static class Writer implements HCPMessageWriter<HCPHelloFailedCodeErrorMessageVer10>{
        @Override
        public void write(ChannelBuffer bb, HCPHelloFailedCodeErrorMessageVer10 message)  {
            int startIndex=bb.writerIndex();
            //version
            bb.writeByte(WIRE_VERSION);
            //type=error
            bb.writeByte((byte)0x1);
            int lengthIndex=bb.writerIndex();
            bb.writeShort(U16.t(lengthIndex));

            //xid;
            bb.writeInt(U32.t(message.xid));
            //err type
            bb.writeShort(U16.t(0));
            //err code
            HCPHelloFailedCodeSerializerVer10.writeTo(bb,message.code);
            //error data
            message.data.writeTo(bb);

            //update the length of data field
            int length=bb.writerIndex()-startIndex;
            bb.setShort(lengthIndex,length);

        }
    }
    final static Reader READER=new Reader();
    static class Reader implements HCPMessageReader<HCPHelloFailedErrorMessage>{

        @Override
        public HCPHelloFailedErrorMessage readFrom(ChannelBuffer bb) throws HCPParseError {
            int start=bb.readerIndex();

            //version
            byte version=bb.readByte();
            if (version!=(byte)0x1)
                throw new HCPParseError("Wrong version:Expected=HCPVersion.HCP_10(1), got="+version);
            //type
            byte type=bb.readByte();
            if (type!=(byte)0x1)
                throw new HCPParseError("Wrong type:Expected=HCPType.HCP_ERROR,got="+type);
            int length= U16.f(bb.readShort());
            if (length<MINIMUM_LENGTH)
                throw new HCPParseError("Wrong length: Expected to be >= " + MINIMUM_LENGTH + ", was: " + length);
            if (bb.readableBytes()+(bb.readerIndex()-start)<length){
                bb.readerIndex(start);
                return null;
            }
            //xid
            long xid= U32.f(bb.readInt());
            //error type
            short errType=bb.readShort();
            if(errType!=(short)0x0){
                throw new HCPParseError("Wrong errType: Expected=HCPErrorType.HELLO_FAILED(0), got="+errType);
            }
            //get error code
            HCPHelloFailedCode code=HCPHelloFailedCodeSerializerVer10.readFrom(bb);
            //get error data
            HCPErrorCauseData data=HCPErrorCauseData.readFrom(bb,length-(bb.readerIndex()-start),HCPVersion.HCP_10);
            HCPHelloFailedErrorMessage message=new HCPHelloFailedCodeErrorMessageVer10(xid,code,data);
            return message;
        }
    }

    @Override
    public HCPErrorMessage.Builder createBuilder() {
        return null;
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        FUNNEL.funnel(this,sink);
    }

    final static HCPHelloFailedCodeFunnel FUNNEL=new HCPHelloFailedCodeFunnel();
    static class HCPHelloFailedCodeFunnel implements Funnel<HCPHelloFailedCodeErrorMessageVer10>{

        @Override
        public void funnel(HCPHelloFailedCodeErrorMessageVer10 messageVer10, PrimitiveSink primitiveSink) {
            //version
            primitiveSink.putByte((byte)0x1);
            //message type
            primitiveSink.putByte((byte)0x1);

            primitiveSink.putLong(messageVer10.xid);
            //error type
            primitiveSink.putShort((short)0x0);
            //code
            HCPHelloFailedCodeSerializerVer10.putTo(primitiveSink,messageVer10.code);
            //data
            messageVer10.data.putTo(primitiveSink);

        }
    }

    static class Builder implements HCPHelloFailedErrorMessage.Builder{
        private boolean xidSet;
        private long xid;
        private boolean codeSet;
        private HCPHelloFailedCode code;
        private boolean dataSet;
        private HCPErrorCauseData data;
        @Override
        public HCPHelloFailedErrorMessage build() {
            long xid = this.xidSet ? this.xid : DEFAULT_ID;
            if(!this.codeSet)
            throw new IllegalStateException("Property code doesn't have default value -- must be set");
            if(code == null)
                throw new NullPointerException("Property code must not be null");
            HCPErrorCauseData data = this.dataSet ? this.data : DAFAULT_DATA;
            if(data == null)
                throw new NullPointerException("Property data must not be null");
            return new HCPHelloFailedCodeErrorMessageVer10(xid,code,data);
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
        public HCPHelloFailedErrorMessage.Builder setXid(long xid) {
            this.xidSet=true;
            this.xid=xid;
            return this;
        }

        @Override
        public HCPErrorType getErrorType() {
            return HCPErrorType.HELLO_FAILED;
        }

        @Override
        public HCPHelloFailedCode getCode() {
            return code;
        }

        @Override
        public HCPHelloFailedErrorMessage.Builder setCode(HCPHelloFailedCode code) {
            this.codeSet=true;
            this.code=code;
            return this;
        }

        @Override
        public HCPErrorCauseData getData() {
            return data;
        }

        @Override
        public HCPHelloFailedErrorMessage.Builder setData(HCPErrorCauseData data) {
            this.dataSet=true;
            this.data=data;
            return this;
        }
    }
}
