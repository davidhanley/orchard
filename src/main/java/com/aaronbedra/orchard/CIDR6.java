package com.aaronbedra.orchard;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class CIDR6 implements CIDR {
    private static final int MASKSHIFT = 128;
    private BigInteger start;
    private BigInteger end;
    private int mask;

    public CIDR6(final InetAddress base, final int mask) throws OrchardException {
        this.mask = mask;
        BigInteger baseInt = ipv6toint(base);
        BigInteger bigMask = deriveMask(mask);
        start = baseInt.and(bigMask);
        end = baseInt.add(deriveEnd(mask)).subtract(BigInteger.ONE);
    }

    @Override
    public final boolean contains(final String address) throws OrchardException {
        if (address == null) {
            throw new OrchardException("Address cannot be null");
        }

        try {
            InetAddress adr = InetAddress.getByName(address);
            BigInteger addressInt = ipv6toint(adr);
            return addressInt.compareTo(start) >= 0 && addressInt.compareTo(end) <= 0;
        } catch (UnknownHostException e) {
            throw new OrchardException("Invalid IP Address");
        }
    }

    @Override
    public final int getMask() {
        return mask;
    }

    private static BigInteger ipv6toint(final InetAddress address) {
        return new BigInteger(address.getAddress());
    }

    private static BigInteger deriveMask(final int mask) {
        return BigInteger.ONE.shiftLeft(MASKSHIFT - mask).subtract(BigInteger.ONE).not();
    }

    private static BigInteger deriveEnd(final int mask) {
        return BigInteger.ONE.shiftLeft(MASKSHIFT - mask);
    }

}
