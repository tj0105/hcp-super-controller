package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.HCPCapabilities;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * @Author ldy
 * @Date: 20-2-19 下午12:39
 * @Version 1.0
 */
public class HCPCapabilitiesSerializerVer10 {
    public final static int FLOW_STATS_VAL=1<<0;
    public final static int TABLE_STATS_VAL=1<<1;
    public final static int PORT_STATS_VAL=1<<2;
    public final static int GROUP_STATS_VAL=1<<3;
    public final static int IP_REASM_VAL=1<<4;
    public final static int QUEUE_STATS_VAL=1<<5;
    public final static int PORT_BLOCKED_VAL=1<<6;


    public static Set<HCPCapabilities> readFrom(ChannelBuffer bb) throws HCPParseError{
        try{
            return ofWireValue(bb.readInt());
        }catch(IllegalArgumentException e){
            throw new HCPParseError(e);
        }
    }

    public static void writeTo(ChannelBuffer bb,Set<HCPCapabilities> set){
        bb.writeInt(toWireValue(set));
    }

    public static void putTo(PrimitiveSink sink,Set<HCPCapabilities> set){
        sink.putInt(toWireValue(set));
    }

    public static Set<HCPCapabilities> ofWireValue(int value){
        EnumSet<HCPCapabilities> set=EnumSet.noneOf(HCPCapabilities.class);

        if ((value & FLOW_STATS_VAL)!=0)
            set.add(HCPCapabilities.FLOW_STATS);
        if ((value & TABLE_STATS_VAL)!=0)
            set.add(HCPCapabilities.TABLE_STATS);
        if ((value & PORT_STATS_VAL)!=0)
            set.add(HCPCapabilities.PORT_STATS);
        if ((value & GROUP_STATS_VAL)!=0)
            set.add(HCPCapabilities.GROUP_STATS);
        if ((value & IP_REASM_VAL)!=0)
            set.add(HCPCapabilities.IP_REASM);
        if ((value & QUEUE_STATS_VAL)!=0)
            set.add(HCPCapabilities.QUEUE_STATS);
        if ((value & PORT_BLOCKED_VAL)!=0)
            set.add(HCPCapabilities.PORT_BLOCKED);
        return Collections.unmodifiableSet(set);
    }

    public static int toWireValue(Set<HCPCapabilities> set){
        int wireValue=0;

        for (HCPCapabilities e:set) {
            switch (e){
                case FLOW_STATS:
                    wireValue |=FLOW_STATS_VAL;
                    break;
                case TABLE_STATS:
                    wireValue |=TABLE_STATS_VAL;
                    break;
                case PORT_STATS:
                    wireValue |=PORT_STATS_VAL;
                    break;
                case GROUP_STATS:
                    wireValue |=GROUP_STATS_VAL;
                    break;
                case IP_REASM:
                    wireValue |=IP_REASM_VAL;
                    break;
                case QUEUE_STATS:
                    wireValue |= QUEUE_STATS_VAL;
                    break;
                case PORT_BLOCKED:
                    wireValue |= PORT_BLOCKED_VAL;
                    break;
                default:
                    throw new IllegalArgumentException("Illegal enum value for type HCPCapabilities in Version 1.0: "+e);
            }
        }
        return wireValue;
    }
}
