package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.HCPHostState;
import org.onosproject.hcp.types.HCPHost;

/**
 * @Author ldy
 * @Date: 20-2-19 下午8:30
 * @Version 1.0
 */
public class HCPHostStateSerializerVer10 {
    public final static byte ACTIVE_VAL=0x0;
    public final static byte INACTIVE_VAL=0x1;

    public static HCPHostState readFrom(ChannelBuffer bb) throws HCPParseError{
        try{
            return ofWireValue(bb.readByte());
        }catch (IllegalArgumentException e){
            throw new HCPParseError(e);
        }
    }

    public static void writeTo(ChannelBuffer bb,HCPHostState state){
        bb.writeByte(toWireValue(state));
    }

    public static void putTo(PrimitiveSink sink,HCPHostState state){
        sink.putByte(toWireValue(state));
    }

    public static HCPHostState ofWireValue(byte value){
        switch (value){
            case ACTIVE_VAL:
                return  HCPHostState.ACTIVE;
            case INACTIVE_VAL:
                return HCPHostState.INACTIVE;
            default:
                throw new IllegalArgumentException("Illegal enum value for HCPHostState in HCP protocol Version 1.0 :"+value);
        }
    }

    public static byte toWireValue(HCPHostState state){
        if (state==null){
            throw new NullPointerException("HCPHostState must cannot be null");
        }
        switch (state){
            case ACTIVE:
                return ACTIVE_VAL;
            case INACTIVE:
                return INACTIVE_VAL;
            default:
                throw new IllegalArgumentException("Illegal enum value for type HCPHostState in HCP protocol Version 1.0: "+state);
        }
    }

}
