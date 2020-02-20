package org.onosproject.hcp.protocol;

/**
 * @Author ldy
 * @Date: 20-2-12 下午9:28
 * @Version 1.0
 */
public interface HCPPPacketOut extends HCPSbpCmpData{
    long getOutPort();
    byte[] getData();
}

