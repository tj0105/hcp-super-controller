package org.onosproject.hcp.types;

public interface HCPValueType<T extends HCPValueType<T>> extends Comparable<T>,PrimitiveSinkable {
    int getLength();
    T applyMask(T mask);
}
