package org.onosproject.hcp.protocol.errormsg;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.types.HCPErrorCauseData;

/**
 * @Author ldy
 * @Date: 20-2-14 下午8:50
 * @Version 1.0
 */
public interface HCPDomainConfigFailedErrorMessage extends HCPObject,HCPErrorMessage {
    HCPVersion getVersion();
    HCPType getType();
    long getXid();
    HCPErrorType getErrorType();
    HCPDomainConfigFaliedCode getCode();
    HCPErrorCauseData getData();

    void writeTo(ChannelBuffer bb);

    public interface Builder extends HCPErrorMessage.Builder{
        HCPDomainConfigFailedErrorMessage build();
        HCPVersion getVersion();
        HCPType getType();
        long getXid();
        Builder setXid(long xid);
        HCPErrorType getErrorType();
        HCPDomainConfigFaliedCode getCode();
        Builder setCode(HCPDomainConfigFaliedCode faildecode);
        HCPErrorCauseData getData();
        Builder setData(HCPErrorCauseData data);

    }
}
