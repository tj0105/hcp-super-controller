package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.types.U8;

/**
 * @Author ldy
 * @Date: 20-2-14 下午8:26
 * @Version 1.0
 */
public final class HCPFactories {
    private static final GenericReader GENERIC_READER=new GenericReader();

    private HCPFactories(){

    }

    public static HCPFactory getFactory(HCPVersion version){
        switch (version){
            case HCP_10:
                return HCPFactoryVer10.INSTANCE;
            default:
                throw new IllegalArgumentException("Unknown version: " + version);
        }
    }
    private static class GenericReader implements HCPMessageReader<HCPMessage> {
        public HCPMessage readFrom(ChannelBuffer bb) throws HCPParseError {
            if (!bb.readable()) {
                return null;
            }
            short wireVersion = U8.f(bb.getByte(bb.readerIndex()));
            HCPFactory factory;
            switch (wireVersion) {
                case 1:
                    factory = HCPFactoryVer10.INSTANCE;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown wire version: " + wireVersion);
            }
            return factory.getReader().readFrom(bb);
        }
    }
    public static HCPMessageReader<HCPMessage> getGenericReader() {
        return GENERIC_READER;
    }
}
