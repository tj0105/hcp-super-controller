package org.onosproject.system.Super;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.onosproject.api.Super.HCPSuperController;
import org.onosproject.system.HCPMessageDecoder;
import org.onosproject.system.HCPMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author ldy
 * @Date: 20-3-4 下午11:08
 * @Version 1.0
 */
public class HCPSuperPiplineFactory implements ChannelPipelineFactory,ExternalResourceReleasable{
    private final static Logger log= LoggerFactory.getLogger(HCPSuperPiplineFactory.class);

    private HCPSuperController superController;
    protected Timer timer;
    protected IdleStateHandler idleStateHandler;
    protected ReadTimeoutHandler readTimeoutHandler;

    public HCPSuperPiplineFactory(HCPSuperController superController){
        super();
        this.superController=superController;
        this.timer=new HashedWheelTimer();
        this.idleStateHandler=new IdleStateHandler(timer,20,25,0);
        this.readTimeoutHandler=new ReadTimeoutHandler(timer,30);
    }


    @Override
    public ChannelPipeline getPipeline() throws Exception {
        HCPSuperChannelHandler handler=new HCPSuperChannelHandler(superController);

        ChannelPipeline pipeline= Channels.pipeline();
        pipeline.addLast("hcpmessageDecoder",new HCPMessageDecoder());
        pipeline.addLast("hcpmessageEncoder",new HCPMessageEncoder());
        pipeline.addLast("idle",idleStateHandler);
        pipeline.addLast("timeout",readTimeoutHandler);
        pipeline.addLast("handler",handler);
        return pipeline;
    }
    @Override
    public void releaseExternalResources(){
        timer.stop();
    }


}
