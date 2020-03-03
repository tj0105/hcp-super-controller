package org.onosproject.command;

import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.system.OvsManageService;

//import org.onosproject.cli.AbstractShellCommand;

/**
 * CLI to create an OVS switch.
 */
@Command(scope = "onos", name = "test-command",
        description = "Create a bridge on specific OVS")
public class TestCommand extends AbstractShellCommand {

    private static final String CREATE_BRIDGE_FORMAT = "Create Bridge: %s";

    //    @Argument(index = 0, name = "bridge-name", description = "name of Bridge",
//            required = true, multiValued = false)
//    private String bridgeName;
//
//    @Argument(index = 1, name = "bridge-type", description = "type of Bridge",
//            required = true, multiValued = false)
    private String bridgeType;

    @Override
    protected void execute() {

//        if (bridgeName == null || bridgeType == null) {
//            return;
//        }
//
//        OvsManageService.OvsDeviceType deviceType;
//        if (bridgeType.toLowerCase().equals("core")) {
//            deviceType = OvsManageService.OvsDeviceType.CORE;
//        } else if (bridgeType.toLowerCase().equals("access")) {
//            deviceType = OvsManageService.OvsDeviceType.ACCESS;
//        } else {
//            print("usage:  create-bridge bridgename 'core'/'access'");
//            return;
//        }

        OvsManageService ovsService = AbstractShellCommand.get(OvsManageService.class);

//        if (ovsService.createOvs(bridgeName, deviceType)) {
//            print(CREATE_BRIDGE_FORMAT, bridgeName);
//        } else {
//            print(CREATE_BRIDGE_FORMAT, "fail");
//        }
        print(ovsService.getIP());
    }
}