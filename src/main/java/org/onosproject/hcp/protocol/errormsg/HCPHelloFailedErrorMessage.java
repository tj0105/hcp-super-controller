package org.onosproject.hcp.protocol.errormsg;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.types.HCPErrorCauseData;

/**
 * @Author ldy
 * @Date: 20-2-14 下午9:02
 * @Version 1.0
 */
public interface HCPHelloFailedErrorMessage extends HCPObject,HCPErrorMessage {
    HCPVersion getVersion();
    HCPType getType();
    long getXid();
    HCPErrorType getErrorType();
    HCPHelloFailedCode getCode();
    HCPErrorCauseData getData();

    void writeTo(ChannelBuffer bb);

    public interface Builder extends  HCPErrorMessage.Builder{
        HCPHelloFailedErrorMessage build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);
        HCPErrorType getErrorType();
        HCPHelloFailedCode getCode();
        Builder setCode(HCPHelloFailedCode code);
        HCPErrorCauseData getData();
        Builder setData(HCPErrorCauseData data);
    }
}
