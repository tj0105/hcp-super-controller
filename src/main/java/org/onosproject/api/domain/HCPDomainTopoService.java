package org.onosproject.api.domain;

import org.onosproject.net.ConnectPoint;
import org.onosproject.net.PortNumber;

/**
 * @Author ldy
 * @Date: 20-3-3 下午11:32
 * @Version 1.0
 */
public interface HCPDomainTopoService {
    PortNumber getLogicalVportNumber(ConnectPoint connectPoint);

    boolean isOuterPort(ConnectPoint connectPoint);

    ConnectPoint getLocationByVport(PortNumber portNumber);
}
