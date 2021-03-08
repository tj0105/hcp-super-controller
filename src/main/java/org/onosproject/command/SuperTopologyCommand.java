package org.onosproject.command;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.api.Super.HCPSuperTopoServices;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.net.PortNumber;
import org.onosproject.net.topology.TopologyEdge;
import org.onosproject.net.topology.TopologyVertex;


@Command(scope = "onos", name = "idp-topology",
        description = "Sample Apache Karaf CLI command")
public class SuperTopologyCommand extends AbstractShellCommand{
    private HCPSuperTopoServices topoServices;
    private HCPSuperController controller;
    private static final String FMT="deviceId=%s";
    private static final String LINK_FMT="Link: src=%s, dst=%s, type=%s, state=%s, expected=%s";
    @Override
    protected void execute() {
        topoServices=AbstractShellCommand.get(HCPSuperTopoServices.class);
        controller= AbstractShellCommand.get(HCPSuperController.class);

        for (TopologyVertex topologyVertex:topoServices.getTopologyVertx()){
            printDeviced(topologyVertex);
            for (TopologyEdge topologyEdge:topoServices.getTopologyEdge(topologyVertex)){
                printLink(topologyEdge);
            }
        }



    }

    private void printDeviced(TopologyVertex topologyVertex ){
        print(FMT,topologyVertex.deviceId().toString());
    }

    private void printLink(TopologyEdge topologyEdge){
        print(LINK_FMT,topologyEdge.link().src(),topologyEdge.link().dst()
                    ,topologyEdge.link().type(),topologyEdge.link().state()
                    ,topologyEdge.link().isExpected());
    }
}
