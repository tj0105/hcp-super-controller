package org.onosproject.hcp.protocol.ver10;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.HCPHostState;
import org.onosproject.hcp.protocol.HCPMessageReader;
import org.onosproject.hcp.types.HCPHost;
import org.onosproject.hcp.types.IPVersion;
import org.onosproject.hcp.types.IPv4Address;
import org.onosproject.hcp.types.MacAddress;

import javax.crypto.Mac;

/**
 * @Author ldy
 * @Date: 20-2-22 下午11:24
 * @Version 1.0
 */
abstract  class HCPHostVer10 {
    static final int LENGTH=11;

    static final Reader READER=new Reader();
    static class Reader implements HCPMessageReader<HCPHost>{

        @Override
        public HCPHost readFrom(ChannelBuffer bb) throws HCPParseError {
            IPv4Address iPv4Address= IPv4Address.read4Bytes(bb);
            MacAddress macAddress=MacAddress.read6Bytes(bb);
            HCPHostState state=HCPHostStateSerializerVer10.readFrom(bb);
            return HCPHost.of(iPv4Address,macAddress,state);

        }
    }
}
