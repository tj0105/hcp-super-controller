package org.onosproject.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.karaf.shell.commands.Command;
import org.onlab.util.Tools;
import org.onosproject.api.Super.HCPSuperTopoServices;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.Link;
import org.onosproject.net.LinkKey;

import java.util.Comparator;

/**
 * @Author ldy
 * @Date: 20-3-1 下午12:16
 * @Version 1.0
 */
@Command(scope = "onos", name = "idp-inter-domain-link",
        description = "Sample Apache Karaf CLI command")
public class SuperInterDomainLink extends AbstractShellCommand {
    private static final String FMT = "src=%s srcVport=%s, dst=%s dstVport=%s, type=%s, state=%s, expected=%s";
    private static final String COMPACT = "%s/%s-%s/%s";

    private HCPSuperTopoServices topoServices;
    @Override
    protected void execute() {
        topoServices=AbstractShellCommand.get(HCPSuperTopoServices.class);
        Iterable<Link> interDomainLink=topoServices.getInterDomainLink();
        if (outputJson()){
            print("%s",json(this,interDomainLink));
        }else{
            Tools.stream(interDomainLink)
                    .sorted(Comparator.comparing(link -> LinkKey.linkKey(link).toString()))
                    .forEach(link -> {
                        print(linkString(link));
                    });
        }
    }
    public static String linkString(Link link) {
        return String.format(FMT, link.src().deviceId(), link.src().port(),
                link.dst().deviceId(), link.dst().port(),
                link.type(), link.state(),
                link.isExpected());
    }

    public static JsonNode json(AbstractShellCommand context,Iterable<Link> links){
        ObjectMapper mapper=new ObjectMapper();
        ArrayNode result=mapper.createArrayNode();
        links.forEach(link -> result.add(context.jsonForEntity(link,Link.class)));
        return result;
    }

}
