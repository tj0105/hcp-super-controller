package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.HCPBadRequestCode;
import org.onosproject.hcp.protocol.Writeable;
import org.onosproject.hcp.types.PrimitiveSinkable;

/**
 * @Author ldy
 * @Date: 20-2-19 上午11:46
 * @Version 1.0
 */
public class HCPBadRequestCodeSerialiserVer10 {
    public final static short BAD_VERSION_VAL=(short)0x0;
    public final static short BAD_TYPE_VAL=(short)0x1;
    public final static short BAD_EXPERIMENTER_VAL=(short)0x2;
    public final static short BAD_EXP_TYPE_VAL=(short)0x3;
    public final static short EPERM_VAL=(short)0x4;
    public final static short BAD_LEN_VAL=(short)0x5;


    public static HCPBadRequestCode readFrom(ChannelBuffer bb) throws HCPParseError{
        try {
            return ofWireValue(bb.readShort());
        }catch (IllegalArgumentException e){
            throw new HCPParseError(e);
        }
    }

    public static void writeTo(ChannelBuffer bb,HCPBadRequestCode e) {
        bb.writeShort(toWrieValue(e));
    }


    public static void putTo(PrimitiveSink sink,HCPBadRequestCode e) {
        sink.putShort(toWrieValue(e));
    }

    public static HCPBadRequestCode ofWireValue(short val){
        switch (val){
            case BAD_VERSION_VAL:
                return HCPBadRequestCode.BAD_VERSION;
            case BAD_TYPE_VAL:
                return HCPBadRequestCode.BAD_TYPE;
            case BAD_EXPERIMENTER_VAL:
                return HCPBadRequestCode.BAD_EXPERIMENTER;
            case BAD_EXP_TYPE_VAL:
                return HCPBadRequestCode.BAD_EXP_TYPE;
            case EPERM_VAL:
                return HCPBadRequestCode.EPERM;
            case BAD_LEN_VAL:
                return HCPBadRequestCode.BAD_LEN;
            default:
                throw new IllegalArgumentException("Illegal wire value for type HCPBadRequestCode in Version 1.0:"+val);
        }

    }

    public static short toWrieValue(HCPBadRequestCode e){
        switch (e){
            case BAD_VERSION:
                return BAD_VERSION_VAL;
            case BAD_TYPE:
                return BAD_TYPE_VAL;
            case BAD_EXPERIMENTER:
                return BAD_EXPERIMENTER_VAL;
            case BAD_EXP_TYPE:
                return BAD_EXP_TYPE_VAL;
            case EPERM:
                return EPERM_VAL;
            case BAD_LEN:
                return BAD_LEN_VAL;
            default:
                throw new IllegalArgumentException("Illegal enum value for type HCPBadRequestCode in Version 1.0:"+e);
        }
    }
}
