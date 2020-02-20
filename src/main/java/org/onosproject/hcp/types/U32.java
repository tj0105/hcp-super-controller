package org.onosproject.hcp.types;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.UnsignedInts;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.HCPMessage;
import org.onosproject.hcp.protocol.HCPMessageReader;
import org.onosproject.hcp.protocol.Writeable;

import javax.swing.*;

/**
 * @Author ldy
 * @Date: 20-2-12 下午9:08
 * @Version 1.0
 */
public class U32 implements Writeable,HCPValueType<U32> {
    private static final int ZERO_VAL=0;
    public static final U32 ZERO=new U32(ZERO_VAL);

    private static final int NO_MASK_VAULE=0xFFFFFFFF;

    public static final U32 NO_MASK=new U32(NO_MASK_VAULE);
    private static final U32 FULL_MASK=ZERO;

    private final int rawValue;

    private U32(int rawValue){
        this.rawValue=rawValue;
    }
    private static U32 of(long value){
        return ofRaw(U32.t(value));
    }
    public static U32 ofRaw(int rawValue){
        if (rawValue==ZERO_VAL)
            return ZERO;
        if (rawValue==NO_MASK_VAULE)
            return NO_MASK;
        return new U32(rawValue);
    }

    public long getValue(){
        return f(rawValue);
    }

    public int getRaw(){
        return rawValue;
    }

    public static long f(final int i){
        return i&0xFFFFFFFFL;
    }
    public static int t(final long l){
        return (int)l;
    }

    public static final Reader READER =new Reader();
    private static class Reader implements HCPMessageReader<U32>{
        @Override
        public U32 readFrom(ChannelBuffer bb) {
            return new U32(bb.readInt());
        }
    }
    @Override
    public String toString() {
        return String.format("0x%08x",rawValue);
    }

    @Override
    public int hashCode() {
        final int prime=31;
        int result=1;
        result=result*prime+rawValue;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj)
            return true;
        if (obj==null)
            return false;
        if (getClass()!=obj.getClass())
            return false;
        U32 u32=(U32) obj;
        if (rawValue!=u32.rawValue)
            return false;
        return true;
    }

    @Override
    public void writeTo(ChannelBuffer bb) {
        bb.writeInt(rawValue);
    }

    @Override
    public int getLength() {
        return 4;
    }

    @Override
    public U32 applyMask(U32 mask) {
        return of(rawValue&mask.rawValue);
    }

    @Override
    public int compareTo(U32 o) {
        return UnsignedInts.compare(rawValue,o.rawValue);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putInt(rawValue);
    }
}
