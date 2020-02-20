package org.onosproject.hcp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.types.HCPVport;

import java.util.Set;

/**
 * @Author ldy
 * @Date: 20-2-14 上午12:02
 * @Version 1.0
 */
public interface HCPVportDescribtion extends HCPObject {
    HCPVport getPortNo();
    Set<HCPVportState> getState();
    HCPVersion getVersion();

    void writeTo(ChannelBuffer bb);

    public interface Builder{
        HCPVportDescribtion build();
        HCPVport getPortNo();
        Builder setPortNo(HCPVport portNo);
        Set<HCPVportState> getStage();
        Builder setState(Set<HCPVportState> state);
        HCPVersion getVersion();
    }
}
