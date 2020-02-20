package org.onosproject.hcp.protocol.errormsg;

import org.onosproject.hcp.protocol.HCPErrorMessage;
import org.onosproject.hcp.protocol.HCPMessageReader;
import org.onosproject.hcp.protocol.HCPVersion;
import org.onosproject.hcp.protocol.XidGenerator;

/**
 * @Author ldy
 * @Date: 20-2-14 下午9:19
 * @Version 1.0
 */
public interface HCPErrorMsgBuild extends XidGenerator {
    HCPVersion getVersion();
    HCPMessageReader<HCPErrorMessage> getReader();

    HCPHelloFailedErrorMessage.Builder buildHelloFailedErrMsg();
    HCPDomainConfigFailedErrorMessage.Builder buildDomainConfigFailedErrMsg();
    HCPBadRequestErrorMsg.Builder buildBadRequeErrMsg();

}
