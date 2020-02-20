package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.HCPVportReason;

/**
 * @Author ldy
 * @Date: 20-2-19 下午10:07
 * @Version 1.0
 */
public class HCPVportReasonSerializerVer10 {
    public final static byte ADD_VAL=0;
    public final static byte DELETE_VAL=1;
    public final static byte MODIFY_VAL=2;

    public static HCPVportReason readFrom(ChannelBuffer bb)throws HCPParseError{
        try {
            return ofWireValue(bb.readByte());
        }catch (IllegalArgumentException e){
            throw new  HCPParseError(e);
        }
    }

    public static void writeTo(ChannelBuffer bb,HCPVportReason reason){
        bb.writeByte(toWireValue(reason));
    }

    public static void putTo(PrimitiveSink sink, HCPVportReason reason){
        sink.putByte(toWireValue(reason));
    }

    public static HCPVportReason ofWireValue(byte value){
        switch (value){
            case ADD_VAL:
                return HCPVportReason.ADD;
            case DELETE_VAL:
                return HCPVportReason.DELETE;
            case MODIFY_VAL:
                return HCPVportReason.MODIFY;
            default:
                throw new IllegalArgumentException("Illegal wire value for type HCPVportReason in HCP protocol version 1.0: "+value);
        }
    }

    public static byte toWireValue(HCPVportReason reason){
        switch (reason){
            case ADD:
                return ADD_VAL;
            case DELETE:
                return DELETE_VAL;
            case MODIFY:
                return MODIFY_VAL;
            default:
                throw new IllegalArgumentException("Illegal enum value for type HCPVportReason in HCP protocol verison 1.0: "+reason);
        }
    }
}