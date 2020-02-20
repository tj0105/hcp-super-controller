package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.HCPHelloFailedCode;

/**
 * @Author ldy
 * @Date: 20-2-19 下午8:01
 * @Version 1.0
 */
public class HCPHelloFailedCodeSerializerVer10  {
    public final static short INCOMPATIBLE_VAL=(short)0x0;
    public final static short EPERM_VAL=(short)0x1;

    public static HCPHelloFailedCode readFrom(ChannelBuffer bb) throws HCPParseError{
        try{
            return ofWireValue(bb.readShort())  ;
        }catch(IllegalArgumentException e){
            throw new HCPParseError(e);
        }
    }

    public static void writeTo(ChannelBuffer bb,HCPHelloFailedCode code){
        bb.writeShort(toWireValue(code));
    }

    public static void putTo(PrimitiveSink sink, HCPHelloFailedCode code){
        sink.putShort(toWireValue(code));
    }

    public static HCPHelloFailedCode ofWireValue(short value){
        switch (value){
            case INCOMPATIBLE_VAL:
                return HCPHelloFailedCode.INCOMPATIBLE;
            case EPERM_VAL:
                return HCPHelloFailedCode.EPERM;
            default:
                throw new IllegalArgumentException("Illegal value for type of HCPHelloFailedCode in HCP protocol Version 1.0:"+value);
        }
    }

    public static short toWireValue(HCPHelloFailedCode code){
        switch (code){
            case INCOMPATIBLE:
                return INCOMPATIBLE_VAL;
            case EPERM:
                return EPERM_VAL;
            default:
                throw new IllegalArgumentException("Illegal enum value for type HCPHelloFailedCode in HCP protocol Version 1.0:"+code);

        }
    }
}
