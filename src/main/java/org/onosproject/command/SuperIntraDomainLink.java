package org.onosproject.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sun.org.apache.bcel.internal.generic.FNEG;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onlab.util.Tools;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.api.Super.HCPSuperTopoServices;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.hcp.types.HCPHost;
import org.onosproject.hcp.types.HCPInternalLink;
import org.onosproject.net.Link;
import org.onosproject.net.LinkKey;

import java.util.Comparator;

/**
 * @Author ldy
 * @Date: 20-3-1 下午12:16
 * @Version 1.0
 */
@Command(scope = "onos", name = "idp-intra-domain-link",
        description = "Sample Apache Karaf CLI command")
public class SuperIntraDomainLink extends AbstractShellCommand {
    private static final String FMT = "src=%s srcVport=%s, dst=%s " +
            "dstVport=%s, type=%s, state=%s, expected=%s Bandwidthcapability=%s, Delay=%s, Hop=%s";
    private static final String COMPACT = "%s/%s-%s/%s";

    private HCPSuperTopoServices topoServices;
    private HCPSuperController controller;
    @Argument(index = 0, name = "domainId", description = "HCPDomain domainID",
            required = false, multiValued = false)

    String domainId=null;
    @Override
    protected void execute() {
        topoServices=AbstractShellCommand.get(HCPSuperTopoServices.class);
        controller=AbstractShellCommand.get(HCPSuperController.class);
        if(domainId==null){
            for (HCPDomain hcpDomain:controller.getDomains()){
                for (Link link:topoServices.getIntraDomainLink(hcpDomain.getDomainId())){
                    printlink(link,topoServices.getinternalLinkDesc(link));
                }
            }
        }else{
            HCPDomain domain=controller.getHCPDomain(domainId);
            for (Link link :topoServices.getIntraDomainLink(domain.getDomainId())){
                printlink(link,topoServices.getinternalLinkDesc(link));
            }
        }
    }
    public void printlink(Link link, HCPInternalLink hcpInternalLink) {
        print(FMT,link.src().deviceId(),link.src().port()
                ,link.dst().deviceId(),link.dst().port(),
                link.type(),link.state(),link.isExpected()
                ,hcpInternalLink.getBandwidthCapability(),
                hcpInternalLink.getDelayCapability(),
                hcpInternalLink.getHopCapability());
    }


}
