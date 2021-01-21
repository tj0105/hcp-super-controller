package org.onosproject.command;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.api.Super.HCPSuperRouteService;
import org.onosproject.api.Super.HCPSuperTopoServices;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.hcp.types.HCPHost;
import org.onosproject.net.PortNumber;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author ldy
 * @Date: 20-3-1 下午12:16
 * @Version 1.0
 */
@Command(scope = "onos", name = "hcp-host",
        description = "Sample Apache Karaf CLI command")
public class SuperHostList extends AbstractShellCommand {

    private HCPSuperTopoServices topoServices;
    private HCPSuperController controller;
    private HCPSuperRouteService routeService;
    private static final String FMT_HOP="domainId=%s MacAddress=%s Ipv4=%s State=%s portHop=%s";
    private static final String FMT = "domainId=%s MacAddress=%s Ipv4=%s State=%s";
    @Argument(index = 0, name = "domainId", description = "domain ID",
            required = false, multiValued = false)

    String domainId = null;

    @Override
    protected void execute() {
        topoServices = AbstractShellCommand.get(HCPSuperTopoServices.class);
        controller = AbstractShellCommand.get(HCPSuperController.class);
        routeService=AbstractShellCommand.get(HCPSuperRouteService.class);
        if (domainId == null) {
            for (HCPDomain hcpDomain : controller.getDomains()) {
                Set<HCPHost> hcpHosts = topoServices.getHostByDomainId(hcpDomain.getDomainId());
                if (hcpHosts.size() > 0) {
                    for (HCPHost hcpHost : hcpHosts) {
                        Map<PortNumber,Integer> map = routeService.getHostPortHop(hcpHost);
                        if (map == null){
                            printHost(hcpDomain.getDomainId(), hcpHost);
                        }
                        else{
                            MyHashMap<PortNumber,Integer> myHashMap = convertToMyHashMap(map);
                            printHost(hcpDomain.getDomainId(),hcpHost,myHashMap);
                        }

                    }
                }
            }
        } else {
            HCPDomain domain = controller.getHCPDomain(domainId);
            for (HCPHost hcpHost : topoServices.getHostByDomainId(domain.getDomainId())) {
                Map<PortNumber,Integer> map = routeService.getHostPortHop(hcpHost);
                if (map == null){
                    printHost(domain.getDomainId(), hcpHost);
                }else{
                    MyHashMap<PortNumber,Integer> myHashMap = convertToMyHashMap(map);
                    printHost(domain.getDomainId(), hcpHost, myHashMap);
                }
            }
        }
    }

    private void printHost(DomainId domainId, HCPHost host, Map<PortNumber, Integer> porthash) {
        print(FMT_HOP, domainId,
                host.getMacAddress(),
                host.getiPv4Address(),
                host.getHostState(),
                porthash.toString());
    }

    private void printHost(DomainId domainId, HCPHost host) {
        print(FMT, domainId,
                host.getMacAddress(),
                host.getiPv4Address(),
                host.getHostState());

    }

    private MyHashMap<PortNumber,Integer> convertToMyHashMap(Map<PortNumber,Integer> map){
        MyHashMap<PortNumber,Integer> myHashMap = new MyHashMap<>();
        for (PortNumber portNumber: map.keySet()) {
            myHashMap.put(portNumber,map.get(portNumber));
        }
        return myHashMap;
    }

    class MyHashMap<K,V> extends HashMap<K, V> {
        @Override
        public String toString() {
            String result = "";
            for(Map.Entry<K,V> entry: entrySet()){
                result += "{Vport=" + entry.getKey() + ",Hops=" + entry.getValue() + "},";
            }
            return result.substring(0,result.length()-1);
        }
    }
}
