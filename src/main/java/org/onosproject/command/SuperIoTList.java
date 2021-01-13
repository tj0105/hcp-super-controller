package org.onosproject.command;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.api.Super.HCPSuperTopoServices;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.hcp.types.HCPHost;
import org.onosproject.hcp.types.HCPIOT;

import java.util.Set;

/**
 * @Author ldy
 * @Date 1/12/21 11:05 PM
 * @Version 1.0
 */

@Command(scope = "onos", name = "hcp-iot",
        description = "Sample Apache Karaf CLI command")
public class SuperIoTList extends AbstractShellCommand {

    private HCPSuperTopoServices topoServices;
    private HCPSuperController controller;
    private static final String FMT = "domainId=%s IoT_Ipv4=%s IoT_Id=%s IoT_Type=%s State=%s";
    @Argument(index = 0, name = "domainId", description = "HCPDomain domainID",
            required = false, multiValued = false)

    String domainId = null;

    @Override
    protected void execute() {
        topoServices = AbstractShellCommand.get(HCPSuperTopoServices.class);
        controller = AbstractShellCommand.get(HCPSuperController.class);
        if (domainId == null){
            for (HCPDomain hcpDomain: controller.getDomains()){
                Set<HCPIOT> hcpiots = topoServices.getIoTByDomainId(hcpDomain.getDomainId());
                if (hcpiots.size() > 0){
                    for (HCPIOT hcpiot:hcpiots){
                        printHost(hcpDomain.getDomainId(),hcpiot);
                    }
                }
            }
        }
        else{
            HCPDomain domain = controller.getHCPDomain(domainId);
            for (HCPIOT hcpiot: topoServices.getIoTByDomainId(domain.getDomainId())){
                printHost(domain.getDomainId(),hcpiot);
            }
        }
    }

    private void printHost(DomainId domainId, HCPIOT hcpiot) {
        print(FMT, domainId,
                hcpiot.getiPv4Address(),
                hcpiot.getHcpiotid().toString(),
                hcpiot.getIoTType(),
                hcpiot.getHCPIoTState());

    }
}
