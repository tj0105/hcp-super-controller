package org.onosproject.oxp.protocol.ver10;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Test;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.protocol.ver10.HCPVportDescriptionVer10;
import org.onosproject.hcp.protocol.ver10.HCPVportStatusVer10;
import org.onosproject.hcp.types.HCPVport;
import org.onosproject.net.PortNumber;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.oxp.protocol.ver10.TestBaseVer10.getMessageFactry;
import static org.onosproject.oxp.protocol.ver10.TestBaseVer10.getMessageReader;
import static org.onosproject.oxp.protocol.ver10.TestBaseVer10.hcpmessageFactory;

/**
 * @Author ldy
 * @Date: 20-3-7 下午4:46
 * @Version 1.0
 */
public class HCPVportStatusTest {
    @Test
    public void testVPortStatus() throws HCPParseError {
        long domaini=0;
        PortNumber portNumber=PortNumber.portNumber(1);

        System.out.println(portNumber);
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        HCPVport vport = HCPVport.ofShort((short) 1);
        Set<HCPVportState> state = new HashSet<>();
        state.add(HCPVportState.LINK_UP);
//        for (HCPVportState hcpVportState:state){
//            System.out.println(hcpVportState);
//        }
        HCPVportDescribtion vportDesc = new HCPVportDescriptionVer10.Builder()
                .setPortNo(vport)
                .setState(state)
                .build();
//        vportDesc.writeTo(buffer);

//      HCPMessageReader<HCPVportDescribtion> w=HCPVportDescriptionVer10.READER;
//      HCPVportDescribtion describtion=w.readFrom(buffer);
//        System.out.println(describtion.getPortNo());
//        System.out.println(describtion.getState());
        HCPVportStatus vportStatus = getMessageFactry()
                .buildVportStatus()
                .setReson(HCPVportReason.ADD)
                .setVportDescribtion(vportDesc)
                .build();
        vportStatus.writeTo(buffer);
        assertThat(vportStatus, instanceOf(HCPVportStatusVer10.class));

        HCPMessage message = getMessageReader().readFrom(buffer);
        assertThat(message, instanceOf(vportStatus.getClass()));

        HCPVportStatus messageRev =  (HCPVportStatus)message;
        System.out.println(messageRev.getType());
        System.out.println(messageRev.getXid());
        System.out.println(messageRev.getReason());
        System.out.println(messageRev.getVportDescribtion().getState()+""
                +messageRev.getVportDescribtion().getPortNo().getPortNumber());
        System.out.println(messageRev.getXid());
        assertThat(vportStatus, is(messageRev));
    }
}
