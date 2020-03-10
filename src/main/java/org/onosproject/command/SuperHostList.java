package org.onosproject.command;

import org.apache.commons.math3.analysis.function.Abs;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.api.Super.HCPSuperTopoServices;
import org.onosproject.api.domain.HCPDomainController;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.hcp.types.HCPHost;
import org.onosproject.system.domain.HCPDomainControllerImp;

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
    private static final String FMT="domainId=%s MacAddress=%s Ipv4=%s State=%s";
    @Argument(index = 0, name = "domainId", description = "HCPDomain domainID",
            required = false, multiValued = false)

    String domainId=null;
    @Override
    protected void execute() {
        topoServices=AbstractShellCommand.get(HCPSuperTopoServices.class);
        controller= AbstractShellCommand.get(HCPSuperController.class);
        if (domainId==null){
            for (HCPDomain hcpDomain:controller.getDomains()){
                for (HCPHost hcpHost:topoServices.getHostByDomainId(hcpDomain.getDomainId())){
                    printHost(hcpDomain.getDomainId(),hcpHost);
                }
            }
        }else {
            HCPDomain domain=controller.getHCPDomain(domainId);
            for (HCPHost hcpHost:topoServices.getHostByDomainId(domain.getDomainId())){
                printHost(domain.getDomainId(),hcpHost);
            }
        }
    }

    private void printHost(DomainId domainId,HCPHost host){
        print(FMT,domainId,
                host.getMacAddress(),
                host.getiPv4Address(),
                host.getHostState());
    }

}
