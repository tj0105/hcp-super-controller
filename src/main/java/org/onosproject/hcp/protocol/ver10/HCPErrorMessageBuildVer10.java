package org.onosproject.hcp.protocol.ver10;

import org.onosproject.hcp.protocol.HCPErrorMessage;
import org.onosproject.hcp.protocol.HCPMessageReader;
import org.onosproject.hcp.protocol.HCPVersion;
import org.onosproject.hcp.protocol.XidGenerator;
import org.onosproject.hcp.protocol.errormsg.HCPBadRequestErrorMsg;
import org.onosproject.hcp.protocol.errormsg.HCPDomainConfigFailedErrorMessage;
import org.onosproject.hcp.protocol.errormsg.HCPErrorMsgBuild;
import org.onosproject.hcp.protocol.errormsg.HCPHelloFailedErrorMessage;
import org.onosproject.hcp.types.XidGenerators;

/**
 * @Author ldy
 * @Date: 20-2-22 下午3:19
 * @Version 1.0
 */
public class HCPErrorMessageBuildVer10 implements HCPErrorMsgBuild{
    public final static HCPErrorMessageBuildVer10 INSTANCE=new HCPErrorMessageBuildVer10();

    private final XidGenerator xidGenerator= XidGenerators.global();
    @Override
    public HCPVersion getVersion() {
        return HCPVersion.HCP_10;
    }

    @Override
    public HCPMessageReader<HCPErrorMessage> getReader() {
        return HCPErrorMessageVer10.READER;
    }

    @Override
    public HCPHelloFailedErrorMessage.Builder buildHelloFailedErrMsg() {
        return new HCPHelloFailedCodeErrorMessageVer10.Builder().setXid(nextXid());
    }

    @Override
    public HCPDomainConfigFailedErrorMessage.Builder buildDomainConfigFailedErrMsg() {
        return new HCPDomainConfigFailedErrorMessageVer10.Builder().setXid(nextXid());
    }

    @Override
    public HCPBadRequestErrorMsg.Builder buildBadRequeErrMsg() {
        return new HCPBadRequestErrorMessgeVer10.Builder().setXid(nextXid());
    }

    @Override
    public long nextXid() {
        return xidGenerator.nextXid();
    }
}
