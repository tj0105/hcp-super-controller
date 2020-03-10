package org.onosproject.command;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.api.Super.HCPSuperTopoServices;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.hcp.types.HCPHost;

/**
 * @Author ldy
 * @Date: 20-3-1 下午12:16
 * @Version 1.0
 */
@Command(scope = "onos", name = "hcp-domain",
        description = "Sample Apache Karaf CLI command")
public class DomainListCommand extends AbstractShellCommand {

    private HCPSuperTopoServices topoServices;
    private HCPSuperController controller;
    private static final String FMT="DomainId=%s DeviceId=%s IpAddress=%s Port=%s";
    @Override
    protected void execute() {
        topoServices=AbstractShellCommand.get(HCPSuperTopoServices.class);
        controller= AbstractShellCommand.get(HCPSuperController.class);
        for (HCPDomain hcpDomain:controller.getDomains()){
                printDomain(hcpDomain);
            }

    }

    private void printDomain(HCPDomain domain){
        print(FMT,domain.getDomainId(),
                domain.getDeviceId(),
                domain.getDomainIp(),
                domain.getDomainPort());
    }

}
