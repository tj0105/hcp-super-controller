package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.ipfilter.IpFilteringHandler;
import org.onosproject.hcp.protocol.HCPForwardingRequest;
import org.onosproject.hcp.types.IPVersion;
import org.onosproject.hcp.types.IPv4Address;
import org.onosproject.hcp.types.U16;
import org.onosproject.hcp.types.U8;

import java.util.Arrays;

/**
 * @Author ldy
 * @Date: 20-2-26 下午4:18
 * @Version 1.0
 */
public class HCPForwardingRequestVer10 implements HCPForwardingRequest{

   private IPv4Address srcIpAddress;
   private IPv4Address dstIpAddress;
   private int inPort;
   private short ethType;
   private byte qos;
   private byte[] data;

   private HCPForwardingRequestVer10(IPv4Address srcIpAddress,IPv4Address dstIpAddress,int port,short ethType,byte qos,byte[]data){
       this.srcIpAddress=srcIpAddress;
       this.dstIpAddress=dstIpAddress;
       this.inPort=port;
       this.ethType=ethType;
       this.qos=qos;
       this.data= Arrays.copyOf(data,data.length);
   }

   public static HCPForwardingRequestVer10 of(IPv4Address srcIpAddress,IPv4Address dstIpAddress,int port,short ethType,byte qos,byte[]data){
       return new HCPForwardingRequestVer10(srcIpAddress,
                                            dstIpAddress,
                                            port,
                                            ethType,
                                            qos,
                                            data);
   }

   public static HCPForwardingRequest read(ChannelBuffer bb,int dataLength){
       IPv4Address srcIpAddress=IPv4Address.read4Bytes(bb);
       IPv4Address dstIpAddress=IPv4Address.read4Bytes(bb);
       int inPort=bb.readInt();
       short ethType=bb.readShort();
       byte qos=bb.readByte();
       byte []data=new byte[dataLength];
       bb.readBytes(data,0,dataLength);
       return of(srcIpAddress,dstIpAddress,
                 inPort,ethType,qos,data);
   }
    @Override
    public IPv4Address getSrcIpAddress() {
        return srcIpAddress;
    }

    @Override
    public IPv4Address getDstIpAddress() {
        return dstIpAddress;
    }

    @Override
    public int getInport() {
        return inPort;
    }

    @Override
    public IPv4Address getMask() {
        return null;
    }

    @Override
    public short getEthType() {
        return ethType;
    }

    @Override
    public byte getQos() {
        return qos;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public void writeTo(ChannelBuffer bb) {
        srcIpAddress.writeTo(bb);
        dstIpAddress.writeTo(bb);
        bb.writeInt(inPort);
        bb.writeShort(ethType);
        bb.writeByte(qos);
        bb.writeBytes(data);
    }

    @Override
    public void putTo(PrimitiveSink sink) {

    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj) return true;
        if (obj==null||getClass()!=obj.getClass())
            return false;
        HCPForwardingRequestVer10 other=(HCPForwardingRequestVer10) obj;
        if (inPort != other.inPort) return false;
        if (ethType != other.ethType) return false;
        if (qos != other.qos) return false;
        if (srcIpAddress != null ? !srcIpAddress.equals(other.srcIpAddress) : other.srcIpAddress != null) return false;
        if (dstIpAddress != null ? !dstIpAddress.equals(other.dstIpAddress) : other.dstIpAddress != null) return false;
        return Arrays.equals(data,other.data);
   }

    @Override
    public int hashCode() {
       final int prime = 31;
       int result=1;
       result=result*prime+(srcIpAddress!=null?srcIpAddress.hashCode():0);
       result=result*prime+(dstIpAddress!=null?dstIpAddress.hashCode():0);
       result=result*prime+inPort;
       result=result*prime+ U16.f(ethType);
       result=result*prime+ U8.f(qos);
       result=result*prime+Arrays.hashCode(data);
       return result;
    }
}
