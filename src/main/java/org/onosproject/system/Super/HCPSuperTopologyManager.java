package org.onosproject.system.Super;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.felix.scr.annotations.*;
import org.onlab.graph.DefaultEdgeWeigher;
import org.onlab.graph.ScalarWeight;
import org.onlab.graph.Weight;
import org.onlab.packet.Ethernet;
import org.onlab.packet.HCPLLDP;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.HCPDomainMessageListener;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.api.Super.HCPSuperTopoServices;
import org.onosproject.api.domain.HCPDomainListener;
import org.onosproject.common.DefaultTopology;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.types.*;
import org.onosproject.net.*;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.topology.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.nio.channels.Channel;
import java.nio.channels.ConnectionPendingException;
import java.util.*;

@Component(immediate = true)
@Service
public class HCPSuperTopologyManager implements HCPSuperTopoServices {
    private static final Logger log = LoggerFactory.getLogger(HCPSuperTopologyManager.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private HCPSuperController superController;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private TopologyService topologyService;


    //监听DomainController发送过来的VPortStatus,
    // Topo,Host,Sbp等消息，完成拓扑的构建
    private HCPDomainMessageListener domainMessageListener = new InternalDomainMessageListener();
    //监听DomainController上线
    private HCPDomainListener domainListener = new InternalHCPDomainListener();

    //记录VPort和Domain
    private Map<DomainId, Set<PortNumber>> vportMap;
    private Map<ConnectPoint, HCPVportDescribtion> vportDescribtionMap;
    private Map<ConnectPoint, Long> vportMaxCapability;
    private Map<ConnectPoint, Long> vportLoadCapability;


    //记录域间link
    private Set<Link> InterDomainLink;
    public  static ProviderId interDomainLinkProviderId = ProviderId.NONE;
    public  static ProviderId RouteproviderId=new ProviderId("USTC","HCP");
    //记录域内link
    private Map<DomainId, Set<Link>> IntraDomainLink;
    private Map<Link, HCPInternalLink> IntraDomainLinkDescription;

    //记录hostId and Localcation
    private Map<DomainId, Map<HostId, HCPHost>> hostMap;

    //构造一个网络拓扑
    private volatile DefaultTopology SuperTopology=new DefaultTopology(
                        ProviderId.NONE,new DefaultGraphDescription(0L,System.currentTimeMillis(),
                                     Collections.<Device>emptyList(),Collections.<Link>emptyList()));
    private LinkWeigher linkWeigherTool=null;
    private final LinkWeigher linkWeigherHopTool=new GraphEdgeWeigth();


    final double MEASURE_TOLERANCE = 0.05;
    @Activate
    public void activate() {
        vportMap = new HashMap<>();
        vportDescribtionMap = new HashMap<>();
        InterDomainLink = new HashSet<>();
        hostMap = new HashMap<>();
        vportLoadCapability = new HashMap<>();
        vportMaxCapability = new HashMap<>();
        IntraDomainLink = new HashMap<>();
        IntraDomainLinkDescription = new HashMap<>();
        superController.addMessageListener(domainMessageListener);
        superController.addHCPDomainListener(domainListener);
        log.info("============HCPSuperController Topology Manager started==========");
    }

    @Deactivate
    public void deactive() {
        superController.removeHCPDomainListener(domainListener);
        superController.removeMessageListener(domainMessageListener);
        vportDescribtionMap.clear();
        vportMap.clear();
        InterDomainLink.clear();
        hostMap.clear();
        vportMaxCapability.clear();
        vportLoadCapability.clear();
        IntraDomainLinkDescription.clear();
        IntraDomainLink.clear();
        log.info("============HCPSuperController Topology Manager stopped==========");
    }

    @Override
    public List<PortNumber> getVports(DomainId domainId) {
        if (!vportMap.containsKey(domainId))
            return Collections.EMPTY_LIST;
        return ImmutableList.copyOf(vportMap.get(domainId));
    }

    @Override
    public HCPVportDescribtion getVportDes(DomainId domainId, PortNumber portNumber) {
        ConnectPoint connectPoint = new ConnectPoint(getDeviceId(domainId), portNumber);
        if (!vportDescribtionMap.containsKey(connectPoint)) {
            return null;
        }
        return null;
    }

    @Override
    public List<Link> getInterDomainLink() {
        return ImmutableList.copyOf(InterDomainLink);
    }

    @Override
    public DeviceId getDeviceId(DomainId domainId) {
        HCPDomain hcpDomain = superController.getHCPDomain(domainId);
        return hcpDomain.getDeviceId();
    }

    @Override
    public Set<HCPHost> getHostByDomainId(DomainId domainId) {
        return new HashSet<>(hostMap.get(domainId).values());
//        return ImmutableSet.copyOf(hostMap.get(domainId).values());
    }

    @Override
    public Set<Link> getIntraDomainLink(DomainId domainId) {
        return new HashSet<>(IntraDomainLink.get(domainId));
    }

    @Override
    public HCPInternalLink getinternalLinkDesc(Link link) {
        return IntraDomainLinkDescription.get(link);
    }

    @Override
    public Set<HCPHost> getHostByIp(IpAddress ipAddress) {
        IPv4Address iPv4Address=IPv4Address.of(ipAddress.toOctets());
        Set<HCPHost> hcpHosts=new HashSet<>();
        for (Map<HostId,HCPHost> hosts:hostMap.values()){
            for (HCPHost hcpHost:hosts.values()){
                if (hcpHost.getiPv4Address().equals(iPv4Address)){
                    hcpHosts.add(hcpHost);
                }
            }
        }
        return hcpHosts;

    }

    @Override
    public DomainId getHostLocation(HostId hostId) {
        for (DomainId domainId:hostMap.keySet()){
            Map<HostId,HCPHost> map=hostMap.get(domainId);
            if (map.get(hostId)!=null){
                return domainId;
            }
        }
        return null;
    }

    @Override
    public long getVportLoadCapability(ConnectPoint connectPoint) {
        if (!vportLoadCapability.containsKey(connectPoint))
            return -1;
        return vportLoadCapability.get(connectPoint);
    }

    @Override
    public long getVportMaxCapability(ConnectPoint connectPoint) {
        if (!vportMaxCapability.containsKey(connectPoint))
            return -1;
        return vportMaxCapability.get(connectPoint);
    }

    @Override
    public long getVportRestCapability(ConnectPoint connectPoint) {
        long load=vportLoadCapability.get(connectPoint);
        long max=vportMaxCapability.get(connectPoint);
        if (load!=-1&&max!=-1)
            return max-load;
        return -1;
    }

    @Override
    public long getInterLinkDelayCapability(Link link) {
        Preconditions.checkNotNull(link);
        long srcVPortDelayCapablity =getVportRestCapability(link.src());
        long dstVportDelayCapablity =getVportRestCapability(link.dst());
        return Long.max(srcVPortDelayCapablity,dstVportDelayCapablity);
    }

    @Override
    public long getInterLinkRestBandwidthCapability(Link link) {
        Preconditions.checkNotNull(link);
//        long srcVPortDelayCapablity =getVportRestCapability(link.src());
        long dstVportRestCapablity =getVportRestCapability(link.dst());
        return dstVportRestCapablity;
    }

    @Override
    public  Set<TopologyVertex> getTopologyVertx(){
        if (SuperTopology.deviceCount()!=0){
            return SuperTopology.getGraph().getVertexes();
        }
        return null;
    }

    @Override
    public  Set<TopologyEdge> getTopologyEdge(TopologyVertex topologyVertex){
        return SuperTopology.getGraph().getEdgesFrom(topologyVertex);
    }

    @Override
    public Set<Path> getLoadBlancePath(ElementId src, ElementId dst, Topology topology) {
        return LoadBlancePaths(src,dst,topology,null);

    }

    @Override
    public Set<Path> getLoadBlancePath(ElementId src, ElementId dst, Topology topology, LinkWeigher weigher) {
        return LoadBlancePaths(src,dst,topology,weigher);
    }

    @Override
    public Set<Path> getLoadBlancePath(ElementId src, ElementId dst) {
        return getLoadBlancePath(src,dst,SuperTopology);
    }

    @Override
    public ProviderId getinterDomainLinkProviderId() {
        return interDomainLinkProviderId;
    }

    /**
     * 获取跳数最少的路径
     * @param pathList
     * @return
     */
    private Path getMinHopPath(List<Path> pathList){
        Path result=pathList.get(0);
        for (int i = 1; i <pathList.size(); i++) {
            Path temp=pathList.get(i);
            result=result.links().size()>temp.links().size() ? temp:result;
        }
        return result;
    }
    /**
     * 计算符合负载均衡的路径
     * @param src 表示host所在的源域
     * @param dst 表示host所在的目的域
     * @param topology 由域间链路生成的拓扑
     * @param weigher   链路的权重
     * @return
     */
    private Set<Path> LoadBlancePaths(ElementId src,ElementId dst,Topology topology,LinkWeigher weigher) {
        linkWeigherTool = weigher == null ? new GraphEdgeWeigth() : weigher;
        if (src instanceof DeviceId && dst instanceof DeviceId) {
            Set<List<TopologyEdge>> allPaths =BFSFindAllPath(new DefaultTopologyVertex((DeviceId)src),
                                                             new DefaultTopologyVertex((DeviceId)dst),
                                                             ((DefaultTopology)topology).getGraph());
            Set<Path> pathSet = CalculatePathCost(allPaths);
            if (superController.getPathComputerParam().equals(HCPConfigFlags.CAPABILITIES_HOP)){
                return pathSet;
            }
            Path path=selectPath(pathSet);
            return path!=null ? ImmutableSet.of(path):ImmutableSet.of();
        }
        return ImmutableSet.of();
    }

    /***
     * 返回得到最优的path
     * @param paths
     * @return
     */
    private  Path selectPath(Set<Path> paths){
        if (paths.size()<1){
            return null;
        }
        return getMinCostPath(new ArrayList(paths) );
    }

    /**
     * 通过比较各条路径的带宽权值，从中选出一组具有较小权值的路径，作为优选路由路径。这
     * 组路径中至少有一条路径的权值为所有可选路由路径的权值中的最小值
     * @param pathList
     * @return
     */
    private Path getMinCostPath(List<Path> pathList) {
        if (superController.getPathComputerParam().equals(HCPConfigFlags.CAPABILITIES_BW)) {
            pathList.sort((p1, p2) -> ((ScalarWeight) p1.weight()).value() > ((ScalarWeight) p2.weight()).value()
                    ? 1 : (((ScalarWeight) p1.weight()).value() < ((ScalarWeight) p2.weight()).value()) ? -1 : 0);
            List<Path> minCost = new ArrayList<>();
            Path result = minCost.get(0);
            minCost.add(result);
            for (int i = 1; i < pathList.size(); i++) {
                Path temp = pathList.get(i);
                if (((ScalarWeight) temp.weight()).value() - ((ScalarWeight) result.weight()).value() < MEASURE_TOLERANCE) {
                    minCost.add(temp);
                }
            }
            return getMinHopPath(pathList);
        }
        return null;
    }

    /**
     * 获取每条路径的权重，首先，计算路径上的每一条链路的权值,其次，选择路径上所有链路的权值的最大值，作为该路径的带宽权值
     * 因为各条链路的负载不同，根据木桶效应，具有最大负载的链路将成为整条路径的短板.
     * @param pathSet
     * @return
     */
    private Set<Path> CalculatePathCost(Set<List<TopologyEdge>> pathSet){
        Set<Path> allResult=new HashSet<>();
        pathSet.forEach(path->{
            ScalarWeight weight=(ScalarWeight) maxPathWeight(path);
            allResult.add(parseEdgetoLink(path,weight));
        });

        return allResult;
    }

    /**
     *  如果是带宽:计算路径上的每一条链路的权值,选择路径上所有链路的权值的最大值
     *  如果是Hop:计算路径上的每一条链路的权值之和
     * @param edgeList
     * @return
     */
    private Weight maxPathWeight(List<TopologyEdge> edgeList){
        double weight=0;
       if (superController.getPathComputerParam().equals(HCPConfigFlags.CAPABILITIES_HOP)
               ||superController.getPathComputerParam().equals(HCPConfigFlags.CAPABILITIES_DELAY)){
            for (TopologyEdge edge:edgeList){
                weight+=((ScalarWeight)linkWeigherTool.weight(edge)).value();
            }
            return new ScalarWeight(weight);
        } else {
            for (TopologyEdge edge:edgeList){
                ScalarWeight scalarWeight=(ScalarWeight) linkWeigherTool.weight(edge);
                double linkWeight=scalarWeight.value();
                weight=Double.max(weight,linkWeight);
            }
            return new ScalarWeight(weight);
        }
    }

    /**
     * 将每条路径的Edge属性改成link，并添加Cost属性
     * @param edgeList
     * @param cost
     * @return
     */
    private Path parseEdgetoLink(List<TopologyEdge> edgeList,ScalarWeight cost){
        List<Link> links=new ArrayList<>();
        edgeList.forEach(edge -> {
            links.add(edge.link());
        });
        return new DefaultPath(RouteproviderId,links,cost);
    }
    /***
     * 获取从源到目的节点的所有路径
     * @param src
     * @param dst
     * @param graph
     * @return
     */
    private Set<List<TopologyEdge>> BFSFindAllPath(TopologyVertex src,TopologyVertex dst,TopologyGraph graph){
        if (src.equals(dst)){
            return null;
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
        return result;

    }
    // The class weigth for the graph TopologyEdge
    private class GraphEdgeWeigth extends DefaultEdgeWeigher<TopologyVertex,TopologyEdge> implements LinkWeigher{
        private static final long LINK_WEIGTH_FULL=100;

        /**
         * 根据不同的参数给边赋权重,如果需要采用自带的获取路径的方法,需要给边进行权重赋值
         * @param edge
         * @return
         */
        @Override
        public Weight weight(TopologyEdge edge) {
            if (superController.getPathComputerParam().equals(HCPConfigFlags.CAPABILITIES_HOP)){
                long linkRest=getInterLinkRestBandwidthCapability(edge.link());
                if (linkRest>2){
                    return new ScalarWeight(1);
                }
                else{
                    return new ScalarWeight(1000);
                }
            }else if (superController.getPathComputerParam().equals(HCPConfigFlags.CAPABILITIES_DELAY)){
                return new ScalarWeight(getInterLinkDelayCapability(edge.link()));
            }
            else{
                long linkSpeed=getLinkBandWidth(edge.link());
                long linkRest =getInterLinkRestBandwidthCapability(edge.link());
                if (linkRest<=0){
                    return new ScalarWeight(LINK_WEIGTH_FULL);
                }
                return new ScalarWeight( 10- linkRest*1.0/linkSpeed*100);
            }
        }
        public long getLinkBandWidth(Link link){
            long BandWidth=getVportMaxCapability(link.dst());
//            long dstBandWidth=getVportMaxCapability(link.dst());
            if (BandWidth!=-1){
                return BandWidth;
            }
            return -1;
        }

    }


    synchronized private void UpdateTopology(){
        GraphDescription graphDescription=new DefaultGraphDescription(System.nanoTime(),System.currentTimeMillis()
        ,superController.getDevices(),getInterDomainLink());
        DefaultTopology defaultTopology=new DefaultTopology(ProviderId.NONE,graphDescription);
        SuperTopology=defaultTopology;


    }


    private void removeVport(DomainId domainId, HCPVportDescribtion hcpVportDescribtion) {
        PortNumber vportNumber = PortNumber.portNumber(hcpVportDescribtion.getPortNo().getPortNumber());
        DeviceId deviceId = getDeviceId(domainId);
        ConnectPoint connectPoint=new ConnectPoint(deviceId,vportNumber);
        Set<PortNumber> vportSet = vportMap.get(domainId);
        if (vportSet != null) {
            vportSet.remove(vportNumber);
        }
        vportDescribtionMap.remove(connectPoint);
        vportMaxCapability.remove(connectPoint);
        Set<Link> removeLink = new HashSet<>();
        for (Link link : InterDomainLink) {
            if (link.src().deviceId().equals(deviceId) && link.src().port().equals(vportNumber)) {
                removeLink.add(link);
            }
            if (link.dst().deviceId().equals(deviceId) && link.dst().port().equals(vportNumber)) {
                removeLink.add(link);
            }
        }
        for (Link link : removeLink) {
            InterDomainLink.remove(link);
        }
        if (!removeLink.isEmpty()){
            UpdateTopology();
        }
    }

    private void addOrUpdateVport(DomainId domainId, HCPVportDescribtion Vportdescribtion) {
        Set<PortNumber> vportSet = vportMap.get(domainId);
        if (vportSet == null) {
            vportSet = new HashSet<>();
            vportMap.put(domainId, vportSet);
        }
        PortNumber VportNumber = PortNumber.portNumber(Vportdescribtion.getPortNo().getPortNumber());
        ConnectPoint connectPoint = new ConnectPoint(getDeviceId(domainId), VportNumber);
        if (Vportdescribtion.getState().equals(HCPVportState.BLOCKED) ||
                Vportdescribtion.getState().equals(HCPVportState.LINK_DOWN)) {
            vportSet.remove(VportNumber);
            vportDescribtionMap.remove(connectPoint);
        } else {
            vportSet.add(VportNumber);
            vportDescribtionMap.put(connectPoint, Vportdescribtion);
        }
//        log.info("=============Vport========{}====", vportMap.get(domainId).toString());
    }

    private void processHostUpdateandReply(DomainId domainId, List<HCPHost> hcpHosts) {
//        log.info("=================hcpHost=========={}", hcpHosts.size());
        Map<HostId, HCPHost> map = hostMap.get(domainId);
        if (map==null) {
            map = new HashMap<>();
            hostMap.put(domainId, map);
        }
        for (HCPHost hcpHost:hcpHosts){
            HostId hostId=HostId.hostId(MacAddress.valueOf(hcpHost.getMacAddress().getLong()));
            if (hcpHost.getHostState().equals(HCPHostState.ACTIVE)){
                map.put(hostId,hcpHost);
            }else{
//                log.info("1111111111111111111111111111111111111111111111");
                map.remove(hostId);
            }
        }
        Set<HCPHost> hosts=getHostByDomainId(domainId);
//        log.info("===================Host======={}=======",hosts.size());

    }

    private void processVportStatusMessage(DomainId domainId, HCPVportStatus hcpVportStatus) {
        switch (hcpVportStatus.getReason()) {
            case ADD:
                addOrUpdateVport(domainId, hcpVportStatus.getVportDescribtion());
                break;
            case MODIFY:
                addOrUpdateVport(domainId, hcpVportStatus.getVportDescribtion());
                break;
            case DELETE:
                removeVport(domainId, hcpVportStatus.getVportDescribtion());
                break;
            default:
                throw new IllegalArgumentException("Illegal vport status for reason type =" + hcpVportStatus.getReason());
        }
    }

    private void processTopologyReply(DomainId domainId, List<HCPInternalLink> internalLinkList) {
        Set<Link> linkSet = new HashSet<>();
        DeviceId deviceId = getDeviceId(domainId);
        for (HCPInternalLink internalLink : internalLinkList) {
            PortNumber srcPortNumber = PortNumber.portNumber(internalLink.getSrcVPort().getPortNumber());
            PortNumber dstPortNumber = PortNumber.portNumber(internalLink.getDstVport().getPortNumber());
            ConnectPoint srcConn = new ConnectPoint(deviceId, srcPortNumber);
            ConnectPoint dstConn = new ConnectPoint(deviceId, dstPortNumber);
            if (srcPortNumber.equals(dstPortNumber)) {
                vportMaxCapability.put(srcConn, internalLink.getBandwidthCapability());
                continue;
            }
            if (internalLink.getDstVport().equals(HCPVport.LOCAL)) {
                vportLoadCapability.put(srcConn, internalLink.getBandwidthCapability());
                continue;
            }
            Link link = DefaultLink.builder()
                    .src(srcConn)
                    .dst(dstConn)
                    .providerId(interDomainLinkProviderId)
                    .type(Link.Type.DIRECT)
                    .state(Link.State.ACTIVE)
                    .build();
            linkSet.add(link);
            IntraDomainLinkDescription.put(link, internalLink);
        }
        IntraDomainLink.put(domainId, linkSet);
    }

    private void processHCPLLDP(DomainId domainId, Ethernet ethernet, PortNumber portNumber) {
        HCPLLDP hcplldp = HCPLLDP.parseHCPLLDP(ethernet);
        if (null == hcplldp) {
            return;
        }
        PortNumber srcPort = PortNumber.portNumber(hcplldp.getVportNum());
        PortNumber dstPort = portNumber;
        DomainId srcDomainId = DomainId.of(hcplldp.getDomianId());
        DeviceId srcDeviceId = DeviceId.deviceId("hcp:" + srcDomainId.toString());
        DeviceId dstDeviceId = getDeviceId(domainId);
        ConnectPoint srcConnection = new ConnectPoint(srcDeviceId, srcPort);
        ConnectPoint dstConnection = new ConnectPoint(dstDeviceId, dstPort);
        Link link = DefaultLink.builder()
                .src(srcConnection)
                .dst(dstConnection)
                .type(Link.Type.DIRECT)
                .state(Link.State.ACTIVE)
                .providerId(interDomainLinkProviderId)
                .build();
        if (InterDomainLink.contains(link)) {
            return;
        }
        InterDomainLink.add(link);
        UpdateTopology();

    }

    private class InternalDomainMessageListener implements HCPDomainMessageListener {

        @Override
        public void handleIncomingMessaget(DomainId domainId, HCPMessage message) {
            if (message.getType() == HCPType.HCP_VPORT_STATUS) {
                HCPVportStatus hcpVportStatus = (HCPVportStatus) message;
                processVportStatusMessage(domainId, hcpVportStatus);
                return;
            }
            if (message.getType() == HCPType.HCP_HOST_REPLY) {
                HCPHostReply hostReply = (HCPHostReply) message;
                processHostUpdateandReply(domainId, hostReply.getHosts());
                return;
            }
            if (message.getType() == HCPType.HCP_HOST_UPDATE) {
                HCPHostUpdate hostUpdate = (HCPHostUpdate) message;
                processHostUpdateandReply(domainId, hostUpdate.getHosts());
            }
            if (message.getType() == HCPType.HCP_TOPO_REPLY) {
                HCPTopologyReply topologyReply = (HCPTopologyReply) message;
                processTopologyReply(domainId, topologyReply.getInternalLink());
            }
            if (message.getType() == HCPType.HCP_SBP) {
                HCPSbp hcpSbp = (HCPSbp) message;
                Ethernet eth = null;
                PortNumber inVport = null;
                if (hcpSbp.getSbpCmpType().equals(HCPSbpCmpType.PACKET_IN)) {
                    HCPPacketIn packetIn = (HCPPacketIn) hcpSbp.getSbpCmpData();
                    inVport = PortNumber.portNumber(packetIn.getInport());
                    eth = superController.parseEthernet(packetIn.getData());
                }
                if (null == eth) {
                    return;
                }
                if (eth.getEtherType() == Ethernet.TYPE_LLDP) {
                    processHCPLLDP(domainId, eth, inVport);
                    return;
                }

            }

        }

        @Override
        public void hanldeOutGoingMessage(DomainId domainId, List<HCPMessage> messages) {

        }
    }

    private class InternalHCPDomainListener implements HCPDomainListener {

        @Override
        public void domainConnected(HCPDomain domain) {
            return;
        }

        @Override
        public void domainDisConnected(HCPDomain domain) {
            for (PortNumber portNumber : vportMap.get(domain.getDomainId())) {
                ConnectPoint vportLocation = new ConnectPoint(domain.getDeviceId(), portNumber);
                vportDescribtionMap.remove(vportLocation);
                vportMaxCapability.remove(vportLocation);
                vportLoadCapability.remove(vportLocation);
            }
            for (Link link : getInterDomainLink()) {
                if (link.dst().deviceId().equals(domain.getDeviceId())
                        || link.src().deviceId().equals(domain.getDeviceId())) {
                    InterDomainLink.remove(link);
                }
            }
            for (Link link:getIntraDomainLink(domain.getDomainId())){
                IntraDomainLinkDescription.remove(link);
            }
            IntraDomainLink.remove(domain.getDomainId());
            vportMap.remove(domain.getDomainId());
            hostMap.remove(domain.getDomainId());
            UpdateTopology();

        }
    }
}
