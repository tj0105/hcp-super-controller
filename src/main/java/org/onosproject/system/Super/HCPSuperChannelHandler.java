package org.onosproject.system.Super;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.omg.PortableInterceptor.ACTIVE;
import org.onosproject.api.HCPDomain;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.hcp.protocol.*;
import org.onosproject.net.DeviceId;
import org.onosproject.system.HCPDomain10;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * @Author ldy
 * @Date: 20-3-4 下午11:19
 * @Version 1.0
 */
public class HCPSuperChannelHandler extends IdleStateAwareChannelHandler {

    private static final Logger log = LoggerFactory.getLogger(HCPSuperChannelHandler.class);

    private HCPSuperController hcpSuperController;
    private HCPDomain hcpDomain;
    private Channel channel;
    private volatile ChannelState state;

    private HCPVersion hcpVersion;
    private HCPFactory hcpFactory;

    private int handshakeTransactions = -1;

    public HCPSuperChannelHandler(HCPSuperController hcpSuperController) {
        this.hcpSuperController = hcpSuperController;
        this.hcpVersion = hcpSuperController.getVersion();
        this.hcpFactory = HCPFactories.getFactory(hcpVersion);
    }

    enum ChannelState {
        INIT(false),
        WAIT_HELLO(false) {
            @Override
            void processHCPHello(HCPSuperChannelHandler h, HCPHello m) {
                if (m.getVersion().getWireVersion() == h.getHcpVersion().getWireVersion()) {
                    h.hcpVersion = h.getHcpVersion();
                    h.hcpFactory = HCPFactories.getFactory(h.hcpVersion);
                    h.hcpDomain = new HCPDomain10(h.hcpSuperController);
                    h.hcpDomain.setHCPVersion(h.hcpVersion);
                    String[] IpAndPort = h.channel.getRemoteAddress().toString()
                            .substring("/".length()).split(":");
                    h.hcpDomain.setDomainIp(IpAndPort[0]);
                    h.hcpDomain.setDomainPort(Integer.valueOf(IpAndPort[1]));
                    h.sendHandShakeHelloMessage();
                    log.info("SuperController Received HCPhello message {} {}", m.getVersion(), m.getXid());
                } else {
                    log.error("Received Hello of version from hcp domain controller at {}" +
                            "{},but this DomainController work with HCP_10.HCPDomainController" +
                            "disconnected {}", m.getVersion(), h.channel.getRemoteAddress());
                }
                h.sendHandShakeFeaturesRequestMessage();
                h.setState(WAIT_FEATURES_REPLY);
            }

            @Override
            void processHCPError(HCPSuperChannelHandler h, HCPErrorMessage m) {
                logError(h, m);
            }
        },
        WAIT_FEATURES_REPLY(false) {
            @Override
            void processHCPFeatureReply(HCPSuperChannelHandler h, HCPFeaturesReply m) {
                log.info("SuperController Received HCPFeaturesReply message {} {}", m.getDomainId(), m.getCapabilities().toString());
                log.info("==============={}==============", m.getDomainId().toString());
                h.hcpDomain.setDomainId(m.getDomainId());
//                log.info("==============={}==============",h.hcpDomain.getDomainId().toString());
                h.hcpDomain.setDeviceId(DeviceId.deviceId("hcp:" + h.hcpDomain.getDomainId().toString()));
                h.hcpDomain.setCapabilities(m.getCapabilities());
                h.hcpDomain.setHCPSbpType(m.getSbpType());
                h.hcpDomain.setHCPSbpVersion(m.getSbpVersion());
                h.sendHandShakeGetConfigRequestMessage();
                h.setState(WAIT_GET_CONFIG_REPLY);
            }

            @Override
            void processHCPError(HCPSuperChannelHandler h, HCPErrorMessage m) {
                logError(h, m);
            }
        },
        WAIT_GET_CONFIG_REPLY(false) {
            @Override
            void processHCPGetConfigReply(HCPSuperChannelHandler h, HCPGetConfigReply m) {
                log.info("SuperController Received HCPGetConfigReply message {} {}", m.getPeriod(), m.getFlags().toString());
                h.hcpDomain.setFlags(m.getFlags());
                h.hcpDomain.setPeriod(m.getPeriod());
                h.hcpDomain.setMissSendLength(m.getMissSendLength());
                h.hcpDomain.setChannel(h.channel);
                h.hcpDomain.setConnected(true);
                h.hcpSuperController.addDomain(h.hcpDomain.getDomainId(), h.hcpDomain);
                h.setState(ACTIVE);
            }
        },
        ACTIVE(true) {
            @Override
            void processHCPError(HCPSuperChannelHandler h, HCPErrorMessage m) {
                h.dispatchMessage(m);
            }

            @Override
            void processHCPTopologyReply(HCPSuperChannelHandler h, HCPTopologyReply m) {
                h.dispatchMessage(m);
            }

            @Override
            void processHCPHostReply(HCPSuperChannelHandler h, HCPHostReply m) {
                h.dispatchMessage(m);
            }

            @Override
            void processHCPHostUpdate(HCPSuperChannelHandler h, HCPHostUpdate m) {
                h.dispatchMessage(m);
            }

            @Override
            void processHCPSbp(HCPSuperChannelHandler h, HCPSbp m) {
                h.dispatchMessage(m);
            }

            @Override
            void processHCPVportStatus(HCPSuperChannelHandler h, HCPVportStatus m) {
                h.dispatchMessage(m);
            }
        };

        private final boolean handshakeComplete;

        ChannelState(boolean handshakeComplete) {
            this.handshakeComplete = handshakeComplete;
        }

        public boolean isHandshakeComplete() {
            return handshakeComplete;
        }

        protected String getSuperStateMessage(HCPSuperChannelHandler H, HCPMessage m, String details) {
            return String.format(" Super State:[%s],received:[%],details:[%] ",
                    this.toString(), m.getType().toString(), details);
        }

        /**
         * We have an HCPMessage we didn't expect given the current state and
         * we want to ignore the message.
         *
         * @param h
         * @param m
         */
        protected void unHandledMessageReceived(HCPSuperChannelHandler h, HCPMessage m) {
            if (log.isDebugEnabled()) {
                String msg = getSuperStateMessage(h, m, "Ignoring unexpected message");
                log.debug(msg);
            }
        }

        protected void logError(HCPSuperChannelHandler h, HCPErrorMessage m) {
            log.error("HCP Message error:{} from super in stae {}", m, this.toString());
        }

        protected void processHCPMessage(HCPSuperChannelHandler h, HCPMessage m) {
            switch (m.getType()) {
                case HCP_HELLO:
                    processHCPHello(h, (HCPHello) m);
                    break;
                case HCP_ERROR:
                    processHCPError(h, (HCPErrorMessage) m);
                    break;
                case HCP_ECHO_REQUEST:
                    processHCPEchoRequest(h, (HCPEchoRequest) m);
                    break;
                case HCP_ECHO_REPLY:
                    processHCPEchoReply(h, (HCPEchoReply) m);
                    break;
                case HCP_FEATURES_REPLY:
                    processHCPFeatureReply(h, (HCPFeaturesReply) m);
                    break;
                case HCP_GET_CONFIG_REPLY:
                    processHCPGetConfigReply(h, (HCPGetConfigReply) m);
                    break;
                case HCP_TOPO_REPLY:
                    processHCPTopologyReply(h, (HCPTopologyReply) m);
                    break;
                case HCP_HOST_REPLY:
                    processHCPHostReply(h, (HCPHostReply) m);
                    break;
                case HCP_HOST_UPDATE:
                    processHCPHostUpdate(h, (HCPHostUpdate) m);
                    break;
                case HCP_VPORT_STATUS:
                    processHCPVportStatus(h, (HCPVportStatus) m);
                    break;
                case HCP_SBP:
                    processHCPSbp(h, (HCPSbp) m);
                    break;
                default:
                    unHandledMessageReceived(h, m);
            }
        }

        void processHCPHello(HCPSuperChannelHandler h, HCPHello m) {

        }

        void processHCPEchoRequest(HCPSuperChannelHandler h, HCPEchoRequest m) {
            HCPEchoReply reply = h.hcpFactory.buildEchoReply()
                    .setData(m.getData())
                    .setXid(m.getXid())
                    .build();
            h.channel.write(Collections.singletonList(reply));
        }

        void processHCPEchoReply(HCPSuperChannelHandler h, HCPEchoReply m) {

        }

        void processHCPError(HCPSuperChannelHandler h, HCPErrorMessage m) {

        }

        void processHCPFeatureReply(HCPSuperChannelHandler h, HCPFeaturesReply m) {

        }

        void processHCPGetConfigReply(HCPSuperChannelHandler h, HCPGetConfigReply m) {

        }

        void processHCPTopologyReply(HCPSuperChannelHandler h, HCPTopologyReply m) {

        }

        void processHCPHostReply(HCPSuperChannelHandler h, HCPHostReply m) {

        }

        void processHCPHostUpdate(HCPSuperChannelHandler h, HCPHostUpdate m) {

        }

        void processHCPVportStatus(HCPSuperChannelHandler h, HCPVportStatus m) {

        }

        void processHCPSbp(HCPSuperChannelHandler h, HCPSbp m) {

        }
    }

    private void sendHandShakeHelloMessage() {
        HCPMessage.Builder mb = hcpFactory.buildHello().setXid(this.handshakeTransactions--);
        log.info("Send HCP_10 Hello to Domain Controller: {} ", channel.getRemoteAddress());
        channel.write(Collections.singletonList(mb.build()));
    }

    private void sendHandShakeFeaturesRequestMessage() {
        log.info("Start send FeaturesRequest message");
        HCPMessage.Builder mb = hcpFactory.buildFeaturesRequest()
                .setXid(this.handshakeTransactions--);
        log.info("Send HCP_10 FeaturesRequest to Domain Controller: {} ", channel.getRemoteAddress());
        channel.write(Collections.singletonList(mb.build()));
    }

    private void sendHandShakeGetConfigRequestMessage() {
        HCPMessage.Builder mb = hcpFactory.buildGetConfitRequest()
                .setXid(this.handshakeTransactions--);
        log.info("Send HCP_10 GetConfigRequest to Domain Controller: {} ", channel.getRemoteAddress());
        channel.write(Collections.singletonList(mb.build()));
        log.info("Send HCP_10 GetConfigRequest to Domain Controller: {} ", channel.getRemoteAddress());
    }

    private void setState(ChannelState state) {
        this.state = state;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
//        log.info("MessengEvent message: {}",e.getMessage());
        if (e.getMessage() instanceof List) {
            List<HCPMessage> messages = (List<HCPMessage>) e.getMessage();
            for (HCPMessage m : messages) {
                state.processHCPMessage(this, m);
            }
        } else
            state.processHCPMessage(this, (HCPMessage) e.getMessage());

    }

    private HCPVersion getHcpVersion() {
        return this.hcpVersion;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        channel = e.getChannel();
        log.info("New Domain Controller Connection from {}", channel.getRemoteAddress());
        setState(ChannelState.WAIT_HELLO);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        log.info("HCP domain Controller disconnected from here");
        hcpSuperController.removeDomain(hcpDomain.getDomainId());
    }

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
        super.channelIdle(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        log.info(e.toString());
    }

    private void dispatchMessage(HCPMessage m) {
        hcpDomain.handleMessage(m);
    }
}
