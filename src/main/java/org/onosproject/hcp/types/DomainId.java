package org.onosproject.hcp.types;

import com.google.common.hash.PrimitiveSink;
import com.google.common.primitives.Longs;
import com.google.common.primitives.UnsignedLongs;
import org.onosproject.hcp.util.HexString;

public class DomainId implements PrimitiveSinkable,Comparable<DomainId> {

    public static final DomainId None=new DomainId(0);

    private final long rawVaule;
    private DomainId(long rawVaule){
        this.rawVaule=rawVaule;
    }

    public static DomainId of(long rawVaule){
        return new DomainId(rawVaule);
    }

    public static DomainId of(String s){
        return new DomainId(HexString.toLong(s));
    }
    public long getLong(){
        return rawVaule;
    }
    public byte[] getBytes(){
        return Longs.toByteArray(rawVaule);
    }
    @Override
    public String toString(){
        return HexString.toHexString(rawVaule);
    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj)
            return true;
        if (obj==null)
            return false;
        if (getClass()!=obj.getClass())
            return false;
        DomainId other=(DomainId)obj;
        if (rawVaule!=other.rawVaule)
            return false;
        return true;
    }

    @Override
    public int compareTo(DomainId o) {
        return UnsignedLongs.compare(rawVaule,o.rawVaule);
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putLong(rawVaule);
    }
}
