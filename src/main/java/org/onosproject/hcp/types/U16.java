package org.onosproject.hcp.types;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.HCPMessageReader;
import org.onosproject.hcp.protocol.Writeable;

/**
 * @Author ldy
 * @Date: 20-2-12 下午8:39
 * @Version 1.0
 */
public class U16 implements Writeable,HCPValueType<U16> {
    private static final short ZERO_VAL=0;
    public static final U16 ZERO=new U16(ZERO_VAL);

    private static final short NO_MASK_VAL=(short)0xFFFF;
    public static final U16 NO_MASK=new U16(NO_MASK_VAL);
    public static final U16 FULL_MASK=ZERO;



    private final short rawValue;

    private U16(short rawValue){
        this.rawValue=rawValue;
    }

    public static int f(final short i){
        return i&0xFFFF;
    }

    public static short t(final int l){
        return (short)l;
    }

    private static final U16 of(int value){
        return ofRaw(t(value));
    }
    public static final U16 ofRaw(short rawValue ){
        if (rawValue==ZERO_VAL)
            return ZERO;
        return new U16(rawValue);
    }

    public int getValue(){
        return f(rawValue);
    }
    public short getRaw(){
        return rawValue;
    }

    @Override
    public String toString() {
        return String.format("0x%04x",rawValue);
    }

    @Override
    public int hashCode() {
        final int prime=31;
        int result=1;
        result=prime*result+rawValue;
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
        U16 u16=(U16)obj;
        if (rawValue!=u16.rawValue)
            return false;
        return true;
    }
    public static final Reader READER =new Reader();

    private static class Reader implements HCPMessageReader<U16>{
        @Override
        public U16 readFrom(ChannelBuffer bb) throws HCPParseError {
           return ofRaw(bb.readShort());
        }
    }
    @Override
    public int getLength() {
        return 2;
    }

    @Override
    public U16 applyMask(U16 mask) {
        return null;
    }

    @Override
    public int compareTo(U16 o) {
        return 0;
    }

    @Override
    public void writeTo(ChannelBuffer bb) {
        bb.writeShort(rawValue);
    }

    @Override
    public void putTo(PrimitiveSink sink) {

    }
}
