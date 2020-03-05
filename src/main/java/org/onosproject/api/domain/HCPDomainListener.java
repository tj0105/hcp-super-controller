package org.onosproject.api.domain;

import org.onosproject.api.HCPDomain;

/**
 * @Author ldy
 * @Date: 20-3-4 下午9:10
 * @Version 1.0
 */
public interface HCPDomainListener {
    void domainConnected(HCPDomain domain);
    void domainDisConnected(HCPDomain domain);
}
