package org.onosproject.hcp.protocol;

import com.google.common.hash.PrimitiveSink;
import org.onosproject.hcp.types.PrimitiveSinkable;

public interface HCPObject extends Writeable,PrimitiveSinkable {
    HCPVersion getVersion();
}
