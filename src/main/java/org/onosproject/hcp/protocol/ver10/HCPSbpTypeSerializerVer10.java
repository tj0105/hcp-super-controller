package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.HCPSbpType;

/**
 * @Author ldy
 * @Date: 20-2-19 下午9:45
 * @Version 1.0
 */
public class HCPSbpTypeSerializerVer10 {
    public final static byte POF_VAL=1<<0;
    public final static byte OPENFLOW_VAL=1<<1;
    public final static byte NETCONF_VAL=1<<2;
    public final static byte XMPP_VAL=1<<3;

    public static HCPSbpType readFrom(ChannelBuffer bb) throws HCPParseError{
        try{
            return ofWireValue(bb.readByte());
        }catch (IllegalArgumentException e){
            throw new HCPParseError(e);
        }
    }

    public static void writeTo(ChannelBuffer bb,HCPSbpType sbpType){
        bb.writeByte(toWireValue(sbpType));
    }

    public static void putTo(PrimitiveSink sink, HCPSbpType sbpType){
        sink.putByte(toWireValue(sbpType));
    }

    public static HCPSbpType ofWireValue(byte value){
        switch (value){
            case POF_VAL:
                return HCPSbpType.POF;
            case OPENFLOW_VAL:
                return HCPSbpType.OPENFLOW;
            case NETCONF_VAL:
                return HCPSbpType.NETCONF;
            case XMPP_VAL:
                return HCPSbpType.XMPP;
            default:
                throw new IllegalArgumentException("Illegal wire value for type HCPSbpType in HCP protocol version 1.0: "+value);
        }
    }

    public static byte toWireValue(HCPSbpType sbpType){
        switch (sbpType){
            case POF:
                return POF_VAL;
            case OPENFLOW:
                return OPENFLOW_VAL;
            case NETCONF:
                return NETCONF_VAL;
            case XMPP:
                return XMPP_VAL;
            default:
                throw new IllegalArgumentException("Illegal enum value for type HCPSbpType in HCP protocol version 1.0:"+sbpType);
        }
    }
}
