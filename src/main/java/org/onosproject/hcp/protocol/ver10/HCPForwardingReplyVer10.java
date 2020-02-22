package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.protocol.HCPForwardingReply;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.hcp.types.IPv4Address;

/**
 * @Author ldy
 * @Date: 20-2-22 下午9:18
 * @Version 1.0
 */
public class HCPForwardingReplyVer10 implements HCPForwardingReply{

    private IPv4Address srcIpAddress;
    private IPv4Address dstIpAddress;
    private int srcVport;
    private int dstVport;
    @Override
    public IPv4Address getSrcIpAddress() {
        return null;
    }

    @Override
    public IPv4Address getDstIpAddress() {
        return null;
    }

    @Override
    public int getSrcVport() {
        return 0;
    }

    @Override
    public int getDstVport() {
        return 0;
    }

    @Override
    public short getEthType() {
        return 0;
    }

    @Override
    public byte getQos() {
        return 0;
    }

    @Override
    public DomainId getDomainId() {
        return null;
    }

    @Override
    public byte[] getData() {
        return new byte[0];
    }

    @Override
    public void writeTo(ChannelBuffer bb) {

    }

    @Override
    public void putTo(PrimitiveSink sink) {

    }
}
