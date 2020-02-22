package org.onosproject.hcp.protocol.ver10;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.HCPErrorMessage;
import org.onosproject.hcp.protocol.HCPMessageReader;
import org.onosproject.hcp.types.U16;
import org.onosproject.hcp.types.U32;

/**
 * @Author ldy
 * @Date: 20-2-22 下午3:28
 * @Version 1.0
 */
public abstract class HCPErrorMessageVer10 {
    //version 1.0
    public final static byte WIRE_VERSION=1;
    public final static int MINIMUM_LENGTH=10;

    public final static Reader READER=new Reader();
    static class Reader implements HCPMessageReader<HCPErrorMessage>{

        @Override
        public HCPErrorMessage readFrom(ChannelBuffer bb) throws HCPParseError {
            if (bb.readableBytes()<MINIMUM_LENGTH){
                return null;
            }
            int startIndex=bb.readerIndex();
            byte version=bb.readByte();
            byte type=bb.readByte();
            int length= U16.f(bb.readShort());
            long xid= U32.f(bb.readInt());
            short errorType=bb.readShort();
            bb.readerIndex(startIndex);
            switch (errorType){
                case (short)0x0:
                    return HCPHelloFailedCodeErrorMessageVer10.READER.readFrom(bb);
                case (short)0x1:
                    return HCPBadRequestErrorMessgeVer10.READER.readFrom(bb);
                case (short)0x2:
                    return HCPDomainConfigFailedErrorMessageVer10.READER.readFrom(bb);
                default:
                    throw new HCPParseError("Unknow value for error message type in hcp protocol version 1.0:"+errorType);
            }
        }
    }
}
