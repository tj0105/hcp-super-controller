package org.onosproject.api.Super;

import org.onosproject.hcp.protocol.HCPVportDescribtion;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.hcp.types.HCPHost;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;
import org.onosproject.net.PortNumber;

import java.util.List;
import java.util.Set;

public interface HCPSuperTopoServices {
    List<PortNumber> getVports(DomainId domainId);

    HCPVportDescribtion getVportDes(DomainId domainId,PortNumber portNumber);

    List<Link>  getInterDomainLink();

    DeviceId getDeviceId(DomainId domainId);

    Set<HCPHost> getHostByDomainId(DomainId domainId);


}
