package org.onosproject.command;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.api.Super.HCPSuperTopoServices;
import org.onosproject.cli.AbstractShellCommand;

/**
 * @Author ldy
 * @Date 3/6/21 2:50 AM
 * @Version 1.0
 */
@Command(scope = "onos", name = "idp-network",
        description = "Sample Apache Karaf CLI command")
public class SuperNetworkCommand  extends AbstractShellCommand {
    @Override
    protected void execute() {
        ObjectNode root = mapper().createObjectNode();
        long domainCount = get(HCPSuperController.class).getDomainCount();
        long linkCount = get(HCPSuperTopoServices.class).getInterLinkCount();
        long hostCount = get(HCPSuperTopoServices.class).getHostCount();
        root.put("DomainCount", domainCount)
                .put("LinkCount", linkCount)
                .put("HostCount", hostCount);
        root.put("LoadBalance","True");
        root.put("pathComputeParam", "Bandwidth");
        ArrayNode domainArray = root.putArray("domains");
        for (HCPDomain domain : get(HCPSuperController.class).getDomains()) {
            ObjectNode domainNode = mapper().createObjectNode();
            domainNode.put("id", domain.getDomainId().toString());
            domainNode.put("Ipaddress",domain.getDomainIp());
            domainNode.put("Port",domain.getDomainPort());
            domainNode.put("capabilityType", "bandwidth");
            domainNode.put("Period",domain.getPeriod());
            domainArray.add(domainNode);
        }
        print("%s", root);
    }
}
