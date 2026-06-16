package glz.hawkframework.core.helper;

import static glz.hawkframework.core.support.ArgumentSupport.argument;

/**
 * {@link IpHelper} provides useful utility methods
 *
 * @author Leavy
 */
public abstract class IpHelper {

    private static final String IPV4_REGEX = "(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])";

    private IpHelper() {
    }

    /**
     * convert the ipv4 address to a long number
     *
     * @param ipv4 ip address
     * @return the long representing the ip address
     */
    public static Long ipv4ToLong(String ipv4) {
        argument(ipv4, ip -> ip.matches(IPV4_REGEX), ip -> "it is not a ipv4 address");
        long result = 0;
        String[] ipAddressInArray = ipv4.split("\\.");
        for (int i = 3; i >= 0; i--) {
            long ip = Long.parseLong(ipAddressInArray[3 - i]);
            result |= ip << (i * 8);
        }
        return result;
    }


    /**
     * convert the long representing an ip address to a string
     *
     * @param ipv4 the int representing an ip address
     * @return ip address string
     */
    public static String longToIpv4(long ipv4) {
        argument(ipv4, ip -> ip > 0, ip -> "the ipv4 int should be larger than 0");
        return ((ipv4 >> 24) & 0xFF) + "."
            + ((ipv4 >> 16) & 0xFF) + "."
            + ((ipv4 >> 8) & 0xFF) + "."
            + (ipv4 & 0xFF);
    }

}
