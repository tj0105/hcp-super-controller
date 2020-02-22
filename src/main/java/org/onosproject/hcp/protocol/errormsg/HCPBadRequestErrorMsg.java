package org.onosproject.hcp.protocol.errormsg;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.types.HCPErrorCauseData;

/**
 * @Author ldy
 * @Date: 20-2-14 下午8:34
 * @Version 1.0
 */
public interface HCPBadRequestErrorMsg extends HCPObject,HCPErrorMessage {
    HCPVersion getVersion();
    HCPType getType();
    long getXid();
    HCPErrorType getErrorType();
    HCPBadRequestCode getCode();
    HCPErrorCauseData getData();

    void writeTo(ChannelBuffer bb);

    public interface Builder extends HCPErrorMessage.Builder{
        HCPBadRequestErrorMsg build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);
        HCPErrorType getErrorType();
        HCPBadRequestCode getCode();
        Builder setCode(HCPBadRequestCode code);
        HCPErrorCauseData getData();
        Builder setData(HCPErrorCauseData data);

    }
}
