package org.onosproject.hcp.types;

import com.google.common.hash.PrimitiveSink;
/**
 * @Author ldy
 * @Date: 01-2-12 下午5:46
 * @Version 1.0
 */
public interface PrimitiveSinkable {
    public void putTo(PrimitiveSink sink);
}
