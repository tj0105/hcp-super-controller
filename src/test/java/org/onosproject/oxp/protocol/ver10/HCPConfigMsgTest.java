package org.onosproject.oxp.protocol.ver10;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Test;
import org.onosproject.hcp.protocol.HCPGetConfigRequest;

/**
 * @Author ldy
 * @Date: 20-2-27 下午10:55
 * @Version 1.0
 */

public class HCPConfigMsgTest extends TestBaseVer10{
    @Test
    public void HCPGetConfigRequest()throws Exception{
        ChannelBuffer channelBuffer= ChannelBuffers.dynamicBuffer();
        HCPGetConfigRequest configRequest=getMessageFactry()
                .buildGetConfitRequest()
                .build();
    }
}
