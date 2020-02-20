package org.onosproject.hcp.types;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedBytes;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.HCPMessageReader;
import org.onosproject.hcp.protocol.Writeable;

/**
 * @Author ldy
 * @Date: 20-2-12 下午8:32
 * @Version 1.0
 */
public class U8 implements Writeable ,HCPValueType<U8> {
    private static final byte ZERO_VAL = 0;
    public static final U8 ZERO = new U8(ZERO_VAL);

    private static final byte NO_MASK_VAL = (byte) 0xFF;
    public static final U8 NO_MASK = new U8(NO_MASK_VAL);
    public static final U8 FULL_MASK = ZERO;

    private final byte raw;

    private U8(byte raw) {
        this.raw = raw;
    }

    public static final U8 of(short value) {
        if (value == ZERO_VAL) {
            return ZERO;
        }
        if (value == NO_MASK_VAL) {
            return NO_MASK;
        }

        return new U8(t(value));
    }
    public static final U8 ofRaw(byte value) {
        return new U8(value);
    }

    public short getValue() {
        return f(raw);
    }

    public byte getRaw() {
        return raw;
    }
    public static short f(final byte i) {
        return (short) (i & 0xff);
    }

    public static byte t(final short l) {
        return (byte) l;
    }


    public static final Reader READER = new Reader();

    private static class Reader implements HCPMessageReader<U8> {
        @Override
        public U8 readFrom(ChannelBuffer bb) throws HCPParseError {
            return new U8(bb.readByte());
        }
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public U8 applyMask(U8 mask) {
        return ofRaw((byte)(raw&mask.raw));
    }

    @Override
    public int compareTo(U8 o) {
        return UnsignedBytes.compare(raw,o.raw);
    }

    @Override
    public void writeTo(ChannelBuffer bb) {
        bb.writeByte(raw);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putByte(raw);
    }
}
