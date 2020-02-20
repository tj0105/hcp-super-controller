package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.HCPDomainConfigFaliedCode;

/**
 * @Author ldy
 * @Date: 20-2-19 下午4:58
 * @Version 1.0
 */
public class HCPDomainConfigFailedCodeSerializerVer10 {
    public final static short BAD_FLAS_VAL=(short)0x0;
    public final static short BAD_LEN_VAL=(short)0x1;
    public final static short EPERM_VAL=(short)0x2;


    public static HCPDomainConfigFaliedCode readFrom(ChannelBuffer bb) throws HCPParseError{
        try{
            return ofWireValue(bb.readShort());
        }catch(IllegalArgumentException e){
            throw new HCPParseError(e);
        }
    }

    public static void writeTo(ChannelBuffer bb,HCPDomainConfigFaliedCode code){
        bb.writeShort(toWireValue(code));
    }

    public static void putTo(PrimitiveSink sink,HCPDomainConfigFaliedCode code){
        sink.putShort(toWireValue(code));
    }

    public static HCPDomainConfigFaliedCode ofWireValue(short value){
        switch(value){
            case BAD_FLAS_VAL:
                return HCPDomainConfigFaliedCode.BAD_FLAGS;
            case BAD_LEN_VAL:
                return HCPDomainConfigFaliedCode.BAD_LEN;
            case EPERM_VAL:
                return HCPDomainConfigFaliedCode.EPERM;
            default:
                throw new IllegalArgumentException("Illegal wire value for type HCPDomainConfigFailed in HCP protocol Version1.0:"+value);
        }
    }

    public static short toWireValue(HCPDomainConfigFaliedCode code){
        switch (code){
            case BAD_FLAGS:
                return BAD_FLAS_VAL;
            case BAD_LEN:
                return BAD_LEN_VAL;
            case EPERM:
                return EPERM_VAL;
            default:
                throw new IllegalArgumentException("Illegal enum valve for type DomianConfigFailedCode in HCP protocol Version 1.0:"+code);

        }
    }
}
