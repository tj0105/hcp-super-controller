package org.onosproject.oxp.protocol.ver10;

import com.google.common.collect.ImmutableList;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.junit.Test;
import org.onlab.packet.ChassisId;
import org.onosproject.common.DefaultTopology;
import org.onosproject.common.DefaultTopologyGraph;
import org.onosproject.net.*;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.topology.*;

import java.util.*;
import java.util.stream.Stream;

public class newTopologyTest {
    private List<Device> deviceSet;
    private List<DeviceId> deviceIdSet;
    private Set<Link> linkSet;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected TopologyService topologyService;

    public static final ClusterId C0 = ClusterId.clusterId(0);
    public static final ClusterId C1 = ClusterId.clusterId(1);
    @Test
    public void newTopologyTest(){
        init();
        GraphDescription description=new DefaultGraphDescription(System.nanoTime(),
                            System.currentTimeMillis(),deviceSet,linkSet);
        DefaultTopology defaultTopology=new DefaultTopology(ProviderId.NONE,description);
        Set<List<TopologyEdge>> result=new HashSet<>();
        dfsFindAllRoutes(new DefaultTopologyVertex(deviceIdSet.get(0)),
                new DefaultTopologyVertex(deviceIdSet.get(3))
                ,new ArrayList<>(),new ArrayList<>(),defaultTopology.getGraph(),result);
        result.forEach(linkSet->{
            System.out.println(linkSet.toString());
        });
        System.out.println("==============================================");
        BFSFindAllPath(new DefaultTopologyVertex(deviceIdSet.get(0)),
                        new DefaultTopologyVertex(deviceIdSet.get(3))
                        ,defaultTopology.getGraph());
        System.out.println("==============================================");
        BFSFindAllPath1(new DefaultTopologyVertex(deviceIdSet.get(0)),
                new DefaultTopologyVertex(deviceIdSet.get(3))
                ,defaultTopology.getGraph());
        //
//   System.out.println(defaultTopology.getGraph().getVertexes().toString());
//        Iterator<TopologyVertex> iterator=defaultTopology.getGraph().getVertexes().iterator();
//        while(iterator.hasNext()){
//            TopologyVertex topologyVertex=iterator.next();
//            System.out.println(defaultTopology.getGraph().getEdgesFrom(topologyVertex));
//        }
//        System.out.println("deviceCount:"+defaultTopology.deviceCount());
//        Set<DisjointPath> disjointPaths=defaultTopology.getDisjointPaths(deviceIdSet.get(0),deviceIdSet.get(3));
//        for (DisjointPath path:disjointPaths){
//            System.out.println(path.toString());
//        }
////        System.out.println("path:"+defaultTopology.getDisjointPaths(deviceIdSet.get(1),deviceIdSet.get(3)));
////        System.out.println("path:"+defaultTopology.getPaths(deviceIdSet.get(1),deviceIdSet.get(3)).toString());
//        Stream<Path> pathList=defaultTopology.getKShortestPaths(deviceIdSet.get(0),deviceIdSet.get(3));
//        pathList.forEach(path -> {
//            System.out.println(path.toString());
//        });
//        System.out.println("==============================");
//        Set<Path> paths=defaultTopology.getPaths(deviceIdSet.get(0),deviceIdSet.get(3),null,4);
//        for (Path path:paths){
//            System.out.println(path.toString());
//        }
////        System.out.println(defaultTopology.getCluster(deviceIdSet.get(1)));
////        System.out.println(defaultTopology.isBroadcastPoint(new ConnectPoint(deviceIdSet.get(1),PortNumber.portNumber(3))));
//        System.out.println(defaultTopology.getClusters().toString());
//        System.out.println(defaultTopology.isInfrastructure(new ConnectPoint(deviceIdSet.get(1),PortNumber.portNumber(10))));
//        TopologyGraph graph=  defaultTopology.getGraph();
//        System.out.println(graph.toString());
//        Set<TopologyEdge> topologyVertices=defaultTopology.getGraph().getEdgesFrom(new DefaultTopologyVertex(deviceIdSet.get(0)));
//        System.out.println(topologyVertices.toString());
    }

    public void init(){
       deviceIdSet=new ArrayList<>();
       deviceSet=new ArrayList<>();
       linkSet=new HashSet<>();
       DeviceIdSet();
       DeviceSet(deviceIdSet);
       InterLink();
    }

    public List<Device> DeviceSet(List<DeviceId> deviceIds){
        for (DeviceId deviceId:deviceIds){
            Device device=new DefaultDevice(ProviderId.NONE,deviceId, Device.Type.CONTROLLER
                            ,"USTC","1.0","1.0","001",new ChassisId(deviceId.toString().substring("hcp:".length())));
            deviceSet.add(device);
        }
        return deviceSet;
    }

    public void DeviceIdSet(){
        for (int i = 1; i <=7; i++) {
            DeviceId deviceId=DeviceId.deviceId("hcp:"+String.format("%016x",i));
            deviceIdSet.add(deviceId);
        }

    }

    public void InterLink(){
        for (int i = 0; i <6 ; i++) {
            if (i!=5){
                ConnectPoint srcConnec=new ConnectPoint(deviceIdSet.get(i),PortNumber.portNumber(0));
                ConnectPoint dstConnec=new ConnectPoint(deviceIdSet.get(i+1),PortNumber.portNumber(1));
                Link link=getLink(srcConnec,dstConnec);
                linkSet.add(link);
                Link link1=getLink(dstConnec,srcConnec);
                linkSet.add(link1);
            }else{
                ConnectPoint srcConnec=new ConnectPoint(deviceIdSet.get(i),PortNumber.portNumber(0));
                ConnectPoint dstConnec=new ConnectPoint(deviceIdSet.get(0),PortNumber.portNumber(1));
                Link link=getLink(srcConnec,dstConnec);
                linkSet.add(link);
                Link link1=getLink(dstConnec,srcConnec);
                linkSet.add(link1);
            }
        }
        // add a link to deviceId2 and deviceId6
        ConnectPoint srcConnec=new ConnectPoint(deviceIdSet.get(1),PortNumber.portNumber(2));
        ConnectPoint dstConnec=new ConnectPoint(deviceIdSet.get(5),PortNumber.portNumber(2));
        Link link=getLink(srcConnec,dstConnec);
        linkSet.add(link);
        Link link1=getLink(dstConnec,srcConnec);
        linkSet.add(link1);

        //add  a link to deviceId3 and deviceId5
        ConnectPoint src=new ConnectPoint(deviceIdSet.get(2),PortNumber.portNumber(2));
        ConnectPoint dst=new ConnectPoint(deviceIdSet.get(4),PortNumber.portNumber(2));
        Link link2=getLink(src,dst);
        Link link3=getLink(dst,src);
        linkSet.add(link2);
        linkSet.add(link3);

    }

    public Link getLink(ConnectPoint srcConn,ConnectPoint dstConn){
        Link link=DefaultLink.builder()
                .src(srcConn)
                .dst(dstConn)
                .state(Link.State.ACTIVE)
                .type(Link.Type.DIRECT)
                .providerId(ProviderId.NONE)
                .build();
        return link;

    }
    @Test
    public void StringTest(){
        DeviceId deviceId=DeviceId.deviceId("hcp:"+String.format("%016x",1));
        System.out.printf(deviceId.toString());
    }

    private void BFSFindAllPath(TopologyVertex src,TopologyVertex dst,TopologyGraph graph){
        if (src.equals(dst)){
            return;
        }
        Queue<List<TopologyVertex>> path=new LinkedList<>();
        Set<List<TopologyVertex>> result=new HashSet<>();
        List<TopologyVertex> list=new ArrayList<>();
        list.add(src);
        path.offer(list);
        while(!path.isEmpty()){
            List<TopologyVertex> vertexList=path.poll();
            TopologyVertex vertex=vertexList.get(vertexList.size()-1);
            if (vertex.equals(dst)){
                result.add(ImmutableList.copyOf(vertexList));
                continue;
            }
//            visitMap.put(vertex,true);
            Iterator<TopologyEdge> iterator=graph.getEdgesFrom(vertex).iterator();
            while(iterator.hasNext()){
                List<TopologyVertex> vertexList1=new ArrayList<>(vertexList);
                TopologyVertex vertex1=iterator.next().dst();
                if (!vertexList1.contains(vertex1)){
                    vertexList1.add(vertex1);
                    path.add(vertexList1);
                }
            }

        }
        result.forEach(ll->{
            System.out.println(ll.toString());
        });

    }
    private void BFSFindAllPath1(TopologyVertex src,TopologyVertex dst,TopologyGraph graph){
        if (src.equals(dst)){
            return;
        }
        Queue<List<TopologyEdge>> path=new LinkedList<>();
        Set<List<TopologyEdge>> result=new HashSet<>();
        List<TopologyEdge> list;
        Iterator<TopologyEdge> iterator=graph.getEdgesFrom(src).iterator();
        while(iterator.hasNext()){
            list=new ArrayList<>();
            list.add(iterator.next());
            path.add(list);
        }
        while(!path.isEmpty()){
            List<TopologyEdge> edgeList=path.poll();
            TopologyEdge edge=edgeList.get(edgeList.size()-1);
            TopologyVertex vertex=edge.dst();
            if (edge.dst().equals(dst)){
                result.add(ImmutableList.copyOf(edgeList));
                continue;
            }
            Iterator<TopologyEdge> iterator1=graph.getEdgesFrom(vertex).iterator();
            while(iterator1.hasNext()){
                List<TopologyEdge> edgeList1=new ArrayList<>(edgeList);
                TopologyEdge edge1=iterator1.next();
                TopologyVertex vertex1=edge1.dst();
                edgeList1.add(edge1);
                path.add(edgeList1);
                for (TopologyEdge topologyEdge:edgeList1){
                    if (topologyEdge.src().equals(vertex1)){
                        path.remove(edgeList1);
                        break;
                    }
                }
            }

        }
        result.forEach(ll->{
            System.out.println(ll.toString());
        });

    }


    private void dfsFindAllRoutes(TopologyVertex src,
                                  TopologyVertex dst,
                                  List<TopologyEdge> passedLink,
                                  List<TopologyVertex> passedDevice,
                                  TopologyGraph topoGraph,
                                  Set<List<TopologyEdge>> result) {
        if (src.equals(dst))
            return;

        passedDevice.add(src);

        Set<TopologyEdge> egressSrc = topoGraph.getEdgesFrom(src);
        egressSrc.forEach(egress -> {
            TopologyVertex vertexDst = egress.dst();
            if (vertexDst.equals(dst)) {
                passedLink.add(egress);
                result.add(ImmutableList.copyOf(passedLink.iterator()));
                passedLink.remove(egress);

            } else if (!passedDevice.contains(vertexDst)) {
                passedLink.add(egress);
                dfsFindAllRoutes(vertexDst, dst, passedLink, passedDevice, topoGraph, result);
                passedLink.remove(egress);

            } else {

            }
        });

        passedDevice.remove(src);
    }
}
