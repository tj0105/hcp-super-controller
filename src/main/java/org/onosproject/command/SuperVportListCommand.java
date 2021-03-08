package org.onosproject.command;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.api.Super.HCPSuperTopoServices;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.hcp.types.HCPHost;
import org.onosproject.net.PortNumber;

import javax.sound.sampled.Port;


@Command(scope = "onos", name = "idp-vport",
        description = "Sample Apache Karaf CLI command")
public class SuperVportListCommand extends AbstractShellCommand{
    private HCPSuperTopoServices topoServices;
    private HCPSuperController controller;
    private static final String FMT="domainId=%s PortNumber=%s";
    @Argument(index = 0, name = "domainId", description = "HCPDomain domainID",
            required = false, multiValued = false)
    String domainId=null;
    @Override
    protected void execute() {
        topoServices=AbstractShellCommand.get(HCPSuperTopoServices.class);
        controller= AbstractShellCommand.get(HCPSuperController.class);
        if (domainId==null){
            for (HCPDomain hcpDomain:controller.getDomains()){
                for (PortNumber portNumber:topoServices.getVports(hcpDomain.getDomainId())){
                    printVport(hcpDomain.getDomainId(),portNumber);
                }
            }
        }
        else{
            HCPDomain domain=controller.getHCPDomain(domainId);
            for (PortNumber Port:topoServices.getVports(domain.getDomainId()))
                printVport(domain.getDomainId(),Port);
        }


    }

    private void printVport(DomainId domainId, PortNumber portNumber){
        print(FMT,domainId,portNumber);

    }
}
