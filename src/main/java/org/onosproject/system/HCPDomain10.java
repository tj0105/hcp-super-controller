package org.onosproject.system;

import org.jboss.netty.channel.Channel;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.HCPSuper;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.types.DomainId;
import org.onosproject.net.DeviceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.BadBinaryOpValueExpException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @Author ldy
 * @Date: 20-3-4 下午9:51
 * @Version 1.0
 */
public class HCPDomain10 implements HCPDomain{


    public final Logger log= LoggerFactory.getLogger(HCPDomain10.class);

    private Set<HCPConfigFlags> flags;
    private int period;
    private long missSendLength;
    private Set<HCPCapabilities> capabilities;
    private HCPSbpType hcpSbpType;
    private HCPSbpVersion sbpVersion;
    private DomainId domainId;
    private DeviceId deviceId;
    private HCPVersion hcpVersion;
    private HCPSuperController superController;
    private int hcpdomainport=0;
    private String hcpdomainIp=null;
    private Channel channel;
    private String ChannelId;
    private boolean connected;

    private boolean BandwidthFlag=false;
    private boolean DelayFlag=false;
    private boolean HopFlag=false;
    private boolean AdvanceFlag=false;

    public HCPDomain10(HCPSuperController superController){
        this.superController=superController;
    }

    private void sendMessageOnChanel(List<HCPMessage> messages){
        if (channel.isConnected())  {
            channel.write(messages);
//            log.info("=================message==========={}",messages.get(0).getType());
            superController.processDownstremMessage(domainId,messages);
        }else{
            log.warn("Drop those messages because hcpdomain channel is disconnected, messages:{}",messages);
        }
    }
    @Override
    synchronized public void sendMsg(HCPMessage message) {
        this.sendMessageOnChanel(Collections.singletonList(message));
    }

    @Override
    public void sendMsg(List<HCPMessage> messageList) {
        this.sendMessageOnChanel(messageList);
    }

    @Override
    public void handleMessage(HCPMessage message) {
        this.superController.processMessage(domainId,message);
    }

    @Override
    public HCPFactory factory() {
        return HCPFactories.getFactory(hcpVersion);
    }

    @Override
    public void setConnected(boolean isConnected) {
        this.connected=isConnected;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public String channleId() {
        return ChannelId;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel=channel;
    }

    @Override
    public DeviceId getDeviceId() {
        return deviceId;
    }

    @Override
    public void setDeviceId(DeviceId deviceId) {
        this.deviceId=deviceId;
    }

    @Override
    public Set<HCPConfigFlags> getFlags() {
        return this.flags;
    }

    @Override
    public void setFlags(Set<HCPConfigFlags> flags) {
        this.flags=flags;
        for (HCPConfigFlags configFlags:flags){
            switch (configFlags){
                case MODE_ADVANCED:
                    AdvanceFlag=true;
                    break;
                case CAPABILITIES_BW:
                    BandwidthFlag=true;
                    break;
                case CAPABILITIES_DELAY:
                    DelayFlag=true;
                    break;
                case CAPABILITIES_HOP:
                    HopFlag=true;
                    break;
            }
        }
    }

    @Override
    public int getPeriod() {
        return this.period;
    }

    @Override
    public void setPeriod(int period) {
        this.period=period;
    }

    @Override
    public long getMissSendLength() {
        return missSendLength;
    }

    @Override
    public void setMissSendLength(long missSendLength) {
        this.missSendLength=missSendLength;
    }

    @Override
    public Set<HCPCapabilities> getCapabilities() {
        return this.capabilities;
    }

    @Override
    public void setCapabilities(Set<HCPCapabilities> capabilities) {
        this.capabilities=capabilities;
    }

    @Override
    public DomainId getDomainId() {
        return this.domainId;
    }

    @Override
    public void setDomainId(DomainId domainId) {
        this.domainId=domainId;
    }

    @Override
    public HCPSbpType getHCPSbpType() {
        return this.hcpSbpType;
    }

    @Override
    public void setHCPSbpType(HCPSbpType sbpType) {
        this.hcpSbpType=sbpType;
    }

    @Override
    public HCPSbpVersion getHCPSbpVersion() {
        return sbpVersion;
    }

    @Override
    public void setHCPSbpVersion(HCPSbpVersion hcpSbpVersion) {
        this.sbpVersion=hcpSbpVersion;
    }

    @Override
    public int getDomainPort() {
       return hcpdomainport;
    }

    @Override
    public void setDomainPort(int hcpSuperPort) {
        this.hcpdomainport=hcpSuperPort;
    }

    @Override
    public String getDomainIp() {
        return hcpdomainIp;
    }

    @Override
    public void setDomainIp(String hcpSuperIp) {
        this.hcpdomainIp=hcpSuperIp;
    }

    @Override
    public HCPVersion getHCPVersion() {
        return this.hcpVersion;
    }

    @Override
    public void setHCPVersion(HCPVersion hcpVersion) {
        this.hcpVersion=hcpVersion;
    }

    @Override
    public boolean isBandWidthFlag() {
        return BandwidthFlag;
    }

    @Override
    public boolean isDelayFlag() {
        return DelayFlag;
    }

    @Override
    public boolean isHopFlag() {
        return HopFlag;
    }

    @Override
    public boolean isAdvanceFlag() {
        return AdvanceFlag;
    }
}
