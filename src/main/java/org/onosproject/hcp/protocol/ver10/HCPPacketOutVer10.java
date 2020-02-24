package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.HCPPacketOut;
import org.onosproject.hcp.util.ChannelUtils;

import java.util.Arrays;

/**
 * @Author ldy
 * @Date: 20-2-23 下午9:47
 * @Version 1.0
 */
public class HCPPacketOutVer10 implements HCPPacketOut {

    private int outPortNumber;
    private byte[] data;

    private HCPPacketOutVer10(int outPortNumber,byte[]data){
        this.outPortNumber=outPortNumber;
        this.data=data;
    }

    public static HCPPacketOut of(int outPortNumber,byte[]data){
        return new HCPPacketOutVer10(outPortNumber,data);
    }

    public static HCPPacketOut read(ChannelBuffer bb,int length){
        int outPort=bb.readInt();
        byte data[]=new byte[length];
        bb.readBytes(data);
        return of(outPort,data);
    }
    @Override
    public long getOutPort() {
        return outPortNumber;
    }

    @Override
    public byte[] getData() {
        return Arrays.copyOf(data,data.length);
    }

    @Override
    public void writeTo(ChannelBuffer bb) throws HCPParseError {
        bb.writeInt(outPortNumber);
        bb.writeBytes(data);
    }

    @Override
    public void putTo(PrimitiveSink sink) {

    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj)
            return true;
        if (obj==null)
            return false;
        if (getClass()!=obj.getClass())
            return false;
        HCPPacketOutVer10  other=(HCPPacketOutVer10)obj;
        if (this.outPortNumber!=other.outPortNumber)
            return false;
        if (!Arrays.equals(data,other.data))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int prime=31;
        int result=1;
        result=result*prime+outPortNumber;
        result=result*prime+Arrays.hashCode(data);
        return result;
    }
}
