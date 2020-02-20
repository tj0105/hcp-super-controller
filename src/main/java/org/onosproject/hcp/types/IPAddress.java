package org.onosproject.hcp.types;

import com.google.common.base.Preconditions;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

public abstract class IPAddress<F extends IPAddress<F>> implements HCPValueType<F> {
    /**
     * Return the internet protocol(IP version) of the object
     * @return protocol version
     */
    public abstract IPVersion getIPVersion();

    /**
     * Checks if this IPAddress represents a valid CIDR style netmask, i.e.,
     * it has a set of leading "1" bits followed by only "0" bits
     * @return true if this represents a valid CIDR style netmask, false
     * otherwise
     */
    public abstract boolean isCidrMask();

    /**
     * If this IPAddress represents a valid CIDR style netmask (see
     * isCidrMask()) returns the length of the prefix (the number of "1" bits).
     * @return length of CIDR mask if this represents a valid CIDR mask
     * @throws IllegalStateException if isCidrMask() == false
     */
    public abstract int asCidrMaskLength();

    /**
     * Checks if the IPAddress is the global broadcast address
     * 255.255.255.255 in case of IPv4
     * @return boolean true or false
     */
    public abstract boolean isBroadcast();

    /**
     * Checks if the IPAddress is the multicast address
     * @return boolean true or false
     */
    public abstract boolean isMulticast();

    /**
     * Perform a low level AND operation on the bits of two IPAddress objects
     * @param   other IPAddress
     * @return  new IPAddress object after the AND oper
     */
    public abstract F and(F other);

    /**
     * Perform a low level OR operation on the bits of two IPAddress objects
     * @param   other IPAddress
     * @return  new IPAddress object after the AND oper
     */
    public abstract F or(F other);

    /**
     * Returns a new IPAddress object with the bits inverted
     * @return  IPAddress
     */
    public abstract F not();

    public abstract byte[] getBytes();

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();

    /** parse an IPv4Address or IPv6Address from their conventional string representation.
     *  For details on supported representations,  refer to {@link IPv4Address#of(String)}
     *  and {@link IPv6Address#of(String)}
     *
     * @param ip a string representation of an IP address
     * @return the parsed IP address
     * @throws NullPointerException if ip is null
     * @throws IllegalArgumentException if string is not a valid IP address
     */
    public static IPAddress<?> of(String ip) {
        Preconditions.checkNotNull(ip, "ip must not be null");
        if (ip.indexOf('.') != -1)
            return IPv4Address.of(ip);
        else if (ip.indexOf(':') != -1)
            return IPv6Address.of(ip);
        else
            throw new IllegalArgumentException("IP Address not well formed: " + ip);
    }

    /**
     * Factory function for InetAddress values.
     * @param address the InetAddress you wish to parse into an IPAddress object.
     * @return the IPAddress object.
     * @throws NullPointerException if address is null
     */
    public static IPAddress<?> of(InetAddress address) {
        Preconditions.checkNotNull(address, "address must not be null");
        if(address instanceof Inet4Address)
            return IPv4Address.of((Inet4Address) address);
        else if (address instanceof Inet6Address)
            return IPv6Address.of((Inet6Address) address);
        else
            return IPAddress.of(address.getHostAddress());
    }
}
