package org.onosproject.api.Super;

import org.onosproject.api.HCPSuper;

/**
 * @Author ldy
 * @Date: 20-3-2 下午9:25
 * @Version 1.0
 */
public interface HCPSuperControllerListener {
    void connectToSuperController(HCPSuper hcpSuper);

    void disconnectSuperController(HCPSuper hcpSuper);
}
