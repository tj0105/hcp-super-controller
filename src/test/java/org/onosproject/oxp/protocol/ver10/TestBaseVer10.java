package org.onosproject.oxp.protocol.ver10;

import org.onosproject.hcp.protocol.*;

/**
 * @Author ldy
 * @Date: 20-2-27 下午10:57
 * @Version 1.0
 */
public class TestBaseVer10 {

    final static HCPFactory hcpmessageFactory= HCPFactories.getFactory(HCPVersion.HCP_10);
    final static HCPMessageReader<HCPMessage> hcpmessageReader= HCPFactories.getGenericReader();
    public static HCPFactory getMessageFactry(){
        return hcpmessageFactory;
    }

    public static HCPMessageReader<HCPMessage> getMessageReader(){
        return hcpmessageReader;
    }
}
