package org.onosproject.system.Super;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.onlab.packet.ChassisId;
import org.onlab.packet.DeserializationException;
import org.onlab.packet.Ethernet;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.HCPDomainMessageListener;
import org.onosproject.api.HCPSuper;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.api.domain.HCPDomainListener;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.net.DefaultDevice;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.provider.ProviderId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author ldy
 * @Date: 20-3-5 上午1:02
 * @Version 1.0
 */
@Component(immediate = true)
@Service
public class HCPSuperControllerImpl implements HCPSuperController {

    private static final Logger log = LoggerFactory.getLogger(HCPSuperControllerImpl.class);
    //保存DomainId和每个Domain的实列
    private Map<DomainId, HCPDomain> domainMap;
    //保存DeviceId的Device
    private Map<DeviceId, Device> deviceMap;

    //保存每個域的Configflag
    private Map<DomainId, Integer> domainModeFlag;
    private Map<DomainId, Boolean> loadBlanceFlag;
    private HCPSuperConnector connector = new HCPSuperConnector(this);
    private HCPDomainListener domainListener = new InternalHCPDomainListener();
    private Set<HCPDomainMessageListener> hcpDomainMessageListeners = new CopyOnWriteArraySet<>();
    private Set<HCPDomainListener> hcpDomainListeners = new CopyOnWriteArraySet<>();

    protected String HCPSuperIP = "127.0.0.1";
    protected int HCPSuperPort = 8899;
    private HCPVersion hcpVersion;
    private HCPFactory hcpFactory;

    private HCPConfigFlags pathComputerParam = HCPConfigFlags.CAPABILITIES_BW;
    private Boolean isLoadBlance = true;

    @Activate
    public void activate() {
        this.setHCPVersion(HCPVersion.HCP_10);
        this.setHCPSuperPort(HCPSuperPort);
        this.setHCPSuperPort(HCPSuperPort);
        hcpFactory = HCPFactories.getFactory(hcpVersion);
        domainMap = new HashMap<>();
        deviceMap = new HashMap<>();
        domainModeFlag = new HashMap<>();
        loadBlanceFlag = new HashMap<>();
        this.addHCPDomainListener(domainListener);
        connector.start();
        log.info("====================HCPSuperController Started=================");
    }

    @Deactivate
    public void deactivate() {
//        log.info("Domain controller size:{} Domain Controller Channel {}",domainMap.size(),domainMap.get(DomainId.of(1111)).channleId());
        connector.stop();
        domainMap.clear();
        deviceMap.clear();
        this.removeHCPDomainListener(domainListener);
        log.info("========================HCPSuperController stopped================");
    }


    @Override
    public HCPVersion getVersion() {
        return this.hcpVersion;
    }

    @Override
    public void setHCPVersion(HCPVersion hcpVersion) {
        this.hcpVersion = hcpVersion;
    }

    @Override
    public int getHCPSuperPort() {
        return this.HCPSuperPort;
    }

    @Override
    public void setHCPSuperPort(int hcpSuperPort) {
        this.HCPSuperPort = hcpSuperPort;
    }

    @Override
    public String getHCPSuperIp() {
        return HCPSuperIP;
    }

    @Override
    public void setHCPSuperIp(String hcpSuperIp) {
        this.HCPSuperIP = hcpSuperIp;
    }

    @Override
    public void addMessageListener(HCPDomainMessageListener listener) {
        this.hcpDomainMessageListeners.add(listener);
    }

    @Override
    public void removeMessageListener(HCPDomainMessageListener listener) {
        this.hcpDomainMessageListeners.remove(listener);
    }

    @Override
    public void addHCPDomainListener(HCPDomainListener listener) {
        this.hcpDomainListeners.add(listener);
    }

    @Override
    public void removeHCPDomainListener(HCPDomainListener listener) {
        this.hcpDomainListeners.remove(listener);
    }

    @Override
    public void sendHCPMessge(DomainId domainId, HCPMessage message) {
        HCPDomain domain = getHCPDomain(domainId);
        if (null != domain && domain.isConnected()) {
            domain.sendMsg(message);
        }
    }

    @Override
    public void addDomain(DomainId domainId, HCPDomain domain) {
        domainMap.put(domainId, domain);
        DeviceId deviceId = domain.getDeviceId();
        Device device = new DefaultDevice(ProviderId.NONE, deviceId, Device.Type.CONTROLLER
                , "USTC", "1.0", "Domain Controller", "1", new ChassisId(domain.getDomainId().getLong()));
        deviceMap.put(deviceId, device);
        for (HCPDomainListener listener : hcpDomainListeners) {
            listener.domainConnected(domain);
        }
    }

    @Override
    public void removeDomain(DomainId domainId) {
        HCPDomain hcpDomain = getHCPDomain(domainId);
        if (null != hcpDomain) {
            deviceMap.remove(hcpDomain.getDeviceId());
            domainMap.remove(domainId);
            for (HCPDomainListener listener : hcpDomainListeners) {
                listener.domainDisConnected(hcpDomain);
            }
        }
    }

    @Override
    public void processDownstremMessage(DomainId domainId, List<HCPMessage> messages) {
        for (HCPDomainMessageListener messageListener : hcpDomainMessageListeners) {
            messageListener.hanldeOutGoingMessage(domainId, messages);
        }
    }

    @Override
    public void processMessage(DomainId domainId, HCPMessage message) {
        for (HCPDomainMessageListener listener : hcpDomainMessageListeners) {
            listener.handleIncomingMessaget(domainId, message);
        }
    }

    @Override
    public HCPDomain getHCPDomain(DomainId domainId) {
        return domainMap.get(domainId);
    }

    @Override
    public HCPDomain getHCPDomain(String domainId) {
        for (DomainId domainId1 : domainMap.keySet()) {
            if (domainId1.toString().equals(domainId)) {
                return domainMap.get(domainId1);
            }
        }
        return null;
    }

    @Override
    public HCPDomain getHCPDomain(long domainId) {
        for (DomainId domainId1 : domainMap.keySet()) {
            if (domainId1.getLong() == domainId) {
                return domainMap.get(domainId1);
            }
        }
        return null;
    }

    @Override
    public Set<HCPDomain> getDomains() {
        return ImmutableSet.copyOf(domainMap.values());
    }

    @Override
    public long getDomainCount() {
        return domainMap.size();
    }

    @Override
    public Ethernet parseEthernet(byte data[]) {
        ChannelBuffer buffer = ChannelBuffers.copiedBuffer(data);
        Ethernet eth = null;
        try {
            eth = Ethernet.deserializer().deserialize(buffer.array(), 0, buffer.readableBytes());
        } catch (DeserializationException e) {
            return null;
        }
        return eth;
    }

    @Override
    public Set<Device> getDevices() {
        return new HashSet<>(deviceMap.values());
    }

    @Override
    public Device getDevice(DeviceId deviceId) {
        return deviceMap.get(deviceId);
    }

    @Override
    public boolean isLoadBlance() {
        return isLoadBlance;
    }

    @Override
    public HCPConfigFlags getPathComputerParam() {
        return pathComputerParam;
    }

    private class InternalHCPDomainListener implements HCPDomainListener {

        @Override
        public void domainConnected(HCPDomain domain) {
            DomainId domainId = domain.getDomainId();
            int flag = 0;
            if (domain.isAdvanceFlag()) {
                loadBlanceFlag.put(domainId, true);
            } else {
                isLoadBlance = false;
                loadBlanceFlag.put(domainId, false);
            }
            if (domain.isBandWidthFlag()) {
                flag |= 1 << 0;
            } else if (domain.isDelayFlag()) {
                flag |= 1 << 1;
            } else if (domain.isHopFlag()) {
                flag |= 1 << 2;
            }
            domainModeFlag.put(domainId, flag);
            if (getDomains().size() != 1) {
                return;
            }
            switch (flag) {
                case 1:
                    pathComputerParam = HCPConfigFlags.CAPABILITIES_BW;
                    break;
                case 2:
                    pathComputerParam = HCPConfigFlags.CAPABILITIES_DELAY;
                    break;
                case 4:
                    pathComputerParam = HCPConfigFlags.CAPABILITIES_HOP;
                    break;
            }

        }

        @Override
        public void domainDisConnected(HCPDomain domain) {
            domainModeFlag.remove(domain.getDomainId());
            loadBlanceFlag.remove(domain.getDomainId());
        }
    }
}
