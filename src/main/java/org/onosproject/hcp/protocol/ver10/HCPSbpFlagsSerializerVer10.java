package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.HCPSbpFlags;

import java.util.EnumSet;
import java.util.Set;

/**
 * @Author ldy
 * @Date: 20-2-19 下午9:25
 * @Version 1.0
 */
public class HCPSbpFlagsSerializerVer10 {
    public final static byte DATA_EXISTS_VAL=1;

    public static Set<HCPSbpFlags> readFrom(ChannelBuffer bb) throws HCPParseError{
        try{
            return ofWireValue(bb.readByte());
        }catch(IllegalArgumentException e){
            throw new HCPParseError(e);
        }
    }

    public static void writeTo(ChannelBuffer bb,Set<HCPSbpFlags> set){
        bb.writeByte(toWireValue(set));
    }

    public static void putTo(PrimitiveSink sink,Set<HCPSbpFlags> set){
        sink.putByte(toWireValue(set));
    }

    public static Set<HCPSbpFlags> ofWireValue(byte value){
        EnumSet<HCPSbpFlags> set=EnumSet.noneOf(HCPSbpFlags.class);
        switch (value){
            case DATA_EXISTS_VAL:
                set.add(HCPSbpFlags.DATA_EXITS);
                break;
            default:
                throw new IllegalArgumentException("Illegal wire value for type HCPSbpFlags in HCP protocol version 1.0:"+value);

        }
        return set;
    }

    public static byte toWireValue(Set<HCPSbpFlags> set){
        byte wireValue=0;
        for (HCPSbpFlags flags:
             set) {
            switch (flags){
                case DATA_EXITS:
                    wireValue |=DATA_EXISTS_VAL;
                    break;
                default:
                    throw new  IllegalArgumentException("Illegal enum value for type HCPSbpFlags in HCP protocol version 1.0:"+flags);
            }
        }
        return wireValue;
    }

}

