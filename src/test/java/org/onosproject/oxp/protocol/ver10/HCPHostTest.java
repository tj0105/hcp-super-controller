package org.onosproject.oxp.protocol.ver10;

import org.junit.Test;
import org.onosproject.hcp.protocol.HCPHostState;
import org.onosproject.hcp.types.HCPHost;
import org.onosproject.hcp.types.IPv4Address;
import org.onosproject.hcp.types.MacAddress;

import java.util.HashSet;
import java.util.Set;

public class HCPHostTest {
    @Test
    public void hcphostTest(){
        Set<HCPHost> hcpHosts=new HashSet<>();
        IPv4Address iPv4Address=IPv4Address.of("192.168.109.112");
        MacAddress macAddress=MacAddress.of(org.onlab.packet.MacAddress.ONOS_LLDP.toString());
        HCPHostState hcpHostState=HCPHostState.ACTIVE;
        hcpHosts.add(HCPHost.of(iPv4Address,macAddress,hcpHostState));

        HCPHost hcpHost=(HCPHost) hcpHosts.toArray()[0];
        System.out.println("HCPHost Ipaddress:"+hcpHost.getiPv4Address());
        System.out.println("HCPHost Macddress:"+hcpHost.getMacAddress());
        System.out.println("HCPHost HostState:"+hcpHost.getHostState());

    }
}
