package org.onosproject.hcp.protocol;

import org.onosproject.hcp.types.PrimitiveSinkable;

public interface HCPSbpCmpData extends Writeable,PrimitiveSinkable {
    byte[] getData();
}
