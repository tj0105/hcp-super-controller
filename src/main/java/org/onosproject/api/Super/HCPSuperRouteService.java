package org.onosproject.api.Super;

import org.onosproject.hcp.types.HCPHost;
import org.onosproject.net.PortNumber;

import java.util.HashMap;
import java.util.Map;

public interface HCPSuperRouteService {
   Map<PortNumber,Integer> getHostPortHop(HCPHost host);
}
