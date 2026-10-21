package glz.hawkframework.core.helper;

import java.io.UncheckedIOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argument;

/**
 * {@link IpHelper} provides useful utility methods
 *
 * @author Leavy
 */
public abstract class IpHelper {

    private static final String IPV4_REGEX = "(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])";
    private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

    private IpHelper() {
    }

    /**
     * convert the ipv4 address to a long number
     *
     * @param ipv4 ip address
     * @return the long representing the ip address
     */
    public static Long ipv4ToLong(String ipv4) {
        argument(ipv4, ip -> IPV4_PATTERN.matcher(ip).matches(), ip -> "it is not a ipv4 address");
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
        argument(ipv4, ip -> ip >= 0, ip -> "the ipv4 int should be greater than or equal to 0");
        return ((ipv4 >> 24) & 0xFF) + "."
            + ((ipv4 >> 16) & 0xFF) + "."
            + ((ipv4 >> 8) & 0xFF) + "."
            + (ipv4 & 0xFF);
    }

    /**
     * Gets all IPV4 addresses.
     *
     * @return an array of IPV4 addresses
     */
    public static List<InetAddress> getAllIpv4Addresses() {

        try {
            List<InetAddress> addresses = new ArrayList<>();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                // Skip disabled network interfaces
                if (!networkInterface.isUp()) continue;

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress addr = inetAddresses.nextElement();
                    // Keep only IPV4 addresses, and exclude loopback addresses
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        addresses.add(addr);
                    }
                }
            }
            return addresses;
        } catch (SocketException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Determines whether an IP is within the specified network (manual calculation).
     *
     * @param ip      the IP address to check
     * @param network the network address (e.g. "192.168.1.0")
     * @param netmask the subnet mask (e.g. "255.255.255.0")
     */
    public static boolean isInNetwork(String ip, String network, String netmask) {
        try {
            // Convert to byte arrays; non-IPv4 addresses are rejected outright, to avoid comparing only the first 4 bytes and producing wrong results
            byte[] ipBytes = toIpv4Bytes(ip);
            byte[] networkBytes = toIpv4Bytes(network);
            byte[] maskBytes = toIpv4Bytes(netmask);

            // Compare byte by byte (IP & Mask == Network & Mask)
            for (int i = 0; i < 4; i++) {
                int ipSegment = ipBytes[i] & 0xFF;
                int networkSegment = networkBytes[i] & 0xFF;
                int maskSegment = maskBytes[i] & 0xFF;

                if ((ipSegment & maskSegment) != (networkSegment & maskSegment)) {
                    return false;
                }
            }
            return true;
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address: ip=" + ip + ", network=" + network + ", netmask=" + netmask, e);
        }
    }

    /**
     * Converts an IPv4 address to a 4-byte array; non-IPv4 addresses (such as IPv6) throw an exception.
     */
    private static byte[] toIpv4Bytes(String address) throws UnknownHostException {
        byte[] bytes = InetAddress.getByName(argNotNull(address, "address")).getAddress();
        if (bytes.length != 4) {
            throw new IllegalArgumentException("Only IPv4 addresses are supported: " + address);
        }
        return bytes;
    }

    /**
     * Determines whether an IP is within a network using CIDR notation.
     * <table border="1" cellpadding="5" cellspacing="0" style="border-collapse: collapse;">
     *   <caption><b>Common CIDR network information table</b></caption>
     *   <thead>
     *     <tr>
     *       <th>CIDR</th>
     *       <th>Subnet mask</th>
     *       <th>Total addresses</th>
     *       <th>Usable addresses</th>
     *       <th>Common use</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr>
     *       <td>/8</td>
     *       <td>255.0.0.0</td>
     *       <td>16,777,216</td>
     *       <td>16,777,214</td>
     *       <td>Very large networks</td>
     *     </tr>
     *     <tr>
     *       <td>/12</td>
     *       <td>255.240.0.0</td>
     *       <td>1,048,576</td>
     *       <td>1,048,574</td>
     *       <td>Large enterprise networks</td>
     *     </tr>
     *     <tr>
     *       <td>/16</td>
     *       <td>255.255.0.0</td>
     *       <td>65,536</td>
     *       <td>65,534</td>
     *       <td>Medium networks</td>
     *     </tr>
     *     <tr>
     *       <td>/20</td>
     *       <td>255.255.240.0</td>
     *       <td>4,096</td>
     *       <td>4,094</td>
     *       <td>Medium subnets</td>
     *     </tr>
     *     <tr>
     *       <td>/24</td>
     *       <td>255.255.255.0</td>
     *       <td>256</td>
     *       <td>254</td>
     *       <td>Small networks (most common)</td>
     *     </tr>
     *     <tr>
     *       <td>/25</td>
     *       <td>255.255.255.128</td>
     *       <td>128</td>
     *       <td>126</td>
     *       <td>Smaller subnets</td>
     *     </tr>
     *     <tr>
     *       <td>/26</td>
     *       <td>255.255.255.192</td>
     *       <td>64</td>
     *       <td>62</td>
     *       <td>Smallest practical subnet</td>
     *     </tr>
     *     <tr>
     *       <td>/27</td>
     *       <td>255.255.255.224</td>
     *       <td>32</td>
     *       <td>30</td>
     *       <td>Small subnets</td>
     *     </tr>
     *     <tr>
     *       <td>/28</td>
     *       <td>255.255.255.240</td>
     *       <td>16</td>
     *       <td>14</td>
     *       <td>Smaller subnets</td>
     *     </tr>
     *     <tr>
     *       <td>/29</td>
     *       <td>255.255.255.248</td>
     *       <td>8</td>
     *       <td>6</td>
     *       <td>Very small networks</td>
     *     </tr>
     *     <tr>
     *       <td>/30</td>
     *       <td>255.255.255.252</td>
     *       <td>4</td>
     *       <td>2</td>
     *       <td>Point-to-point connections</td>
     *     </tr>
     *     <tr>
     *       <td>/31</td>
     *       <td>255.255.255.254</td>
     *       <td>2</td>
     *       <td>2</td>
     *       <td>Point-to-point connections (RFC 3021)</td>
     *     </tr>
     *     <tr>
     *       <td>/32</td>
     *       <td>255.255.255.255</td>
     *       <td>1</td>
     *       <td>1</td>
     *       <td>A single host</td>
     *     </tr>
     *   </tbody>
     * </table>
     *
     * <p>
     * <b>Address count formulas:</b>
     * <br>Total addresses = 2<sup>(32 - prefixLength)</sup>
     * <br>Usable addresses = total addresses - 2 (except for /31 and /32 networks)
     * </p>
     *
     * <p>
     * <b>Notes:</b>
     * <ul>
     *   <li>A /31 network has no network address or broadcast address; both addresses are usable (RFC 3021)</li>
     *   <li>A /32 network represents a single host address</li>
     *   <li>For /30 and smaller networks, point-to-point connections are recommended</li>
     *   <li>A /24 network is the most common subnetting scheme</li>
     * </ul>
     * </p>
     * @param ip   the IP address to check
     * @param cidr the CIDR format (e.g. "192.168.1.0/24")
     */
    public static boolean isInCIDR(String ip, String cidr) {
        argNotNull(ip, "ip");
        argNotNull(cidr, "cidr");
        int slashIndex = cidr.indexOf('/');
        if (slashIndex < 0) throw new IllegalArgumentException("Invalid CIDR format: " + cidr);
        String network = cidr.substring(0, slashIndex);
        int prefixLength;
        try {
            prefixLength = Integer.parseInt(cidr.substring(slashIndex + 1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid CIDR prefix length: " + cidr, e);
        }
        if (prefixLength < 0 || prefixLength > 32)
            throw new IllegalArgumentException("CIDR prefix length must be between 0 and 32: " + prefixLength);

        int maskBytes = prefixLength == 0 ? 0 : (0xFFFFFFFF << (32 - prefixLength));

        try {
            long ipLong = ipToLong(InetAddress.getByName(ip));
            long networkLong = ipToLong(InetAddress.getByName(network));

            long maskedIp = ipLong & maskBytes;
            long maskedNetwork = networkLong & maskBytes;

            return maskedIp == maskedNetwork;
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address in CIDR check: ip=" + ip + ", cidr=" + cidr, e);
        }
    }

    /**
     * Determines whether an IP is within a range (inclusive of the boundaries).
     *
     * @param ip    the ip address to check
     * @param start the start ip address
     * @param end   the end ip address
     * @return whether it is within the range
     */
    public static boolean isBetween(String ip, String start, String end) {
        long ipLong = ipv4ToLong(ip);
        long startLong = ipv4ToLong(start);
        long endLong = ipv4ToLong(end);

        // Ensure start <= end
        if (startLong > endLong) {
            long temp = startLong;
            startLong = endLong;
            endLong = temp;
        }

        return ipLong >= startLong && ipLong <= endLong;
    }

    /**
     * Converts an IP to long (for bit operations).
     */
    private static long ipToLong(InetAddress ip) {
        byte[] octets = ip.getAddress();
        long result = 0;
        for (byte octet : octets) {
            result <<= 8;
            result |= octet & 0xFF;
        }
        return result;
    }

    /**
     * Converts an IP to long.
     */
    public static long ipToLong(String ip) {
        try {
            return ipToLong(InetAddress.getByName(argNotNull(ip, "ip")));
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address: " + ip, e);
        }
    }

}
