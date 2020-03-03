package org.onosproject.command;

import org.apache.karaf.shell.commands.Command;
import org.onosproject.api.domain.HCPDomainController;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.system.domain.HCPDomainControllerImp;

/**
 * @Author ldy
 * @Date: 20-3-1 下午12:16
 * @Version 1.0
 */
@Command(scope = "onos", name = "super-ip",
        description = "Sample Apache Karaf CLI command")
public class SuperHostList extends AbstractShellCommand {

    private HCPDomainController service;
    @Override
    protected void execute() {
        service=AbstractShellCommand.get(HCPDomainControllerImp.class);
        print("superIp: %s superPort: %s",service.getHCPSuperIp(),service.getHCPSuperPort());
    }

}
