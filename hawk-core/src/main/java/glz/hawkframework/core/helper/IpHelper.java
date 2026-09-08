package glz.hawkframework.core.helper;

import java.io.UncheckedIOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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

    /**
     * 获取全部IPV4地址
     *
     * @return 一个IPV4地址数组
     */
    public static List<InetAddress> getAllIpv4Addresses() {

        try {
            List<InetAddress> addresses = new ArrayList<>();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                // 跳过未启用的网卡
                if (!networkInterface.isUp()) continue;

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress addr = inetAddresses.nextElement();
                    // 只保留IPV4地址，且排除回环地址
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
     * 判断 IP 是否在指定网段内（手动计算方式）
     *
     * @param ip      要检查的 IP 地址
     * @param network 网段地址（如 "192.168.1.0"）
     * @param netmask 子网掩码（如 "255.255.255.0"）
     */
    public static boolean isInNetwork(String ip, String network, String netmask) {
        try {
            // 转换为 byte 数组
            byte[] ipBytes = InetAddress.getByName(ip).getAddress();
            byte[] networkBytes = InetAddress.getByName(network).getAddress();
            byte[] maskBytes = InetAddress.getByName(netmask).getAddress();

            // 逐字节比较（IP & Mask == Network & Mask）
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
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 使用 CIDR 表示法判断 IP 是否在网段内
     * <table border="1" cellpadding="5" cellspacing="0" style="border-collapse: collapse;">
     *   <caption><b>常用 CIDR 网段信息表</b></caption>
     *   <thead>
     *     <tr>
     *       <th>CIDR</th>
     *       <th>子网掩码</th>
     *       <th>总地址数</th>
     *       <th>可用地址数</th>
     *       <th>常见用途</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr>
     *       <td>/8</td>
     *       <td>255.0.0.0</td>
     *       <td>16,777,216</td>
     *       <td>16,777,214</td>
     *       <td>超大型网络</td>
     *     </tr>
     *     <tr>
     *       <td>/12</td>
     *       <td>255.240.0.0</td>
     *       <td>1,048,576</td>
     *       <td>1,048,574</td>
     *       <td>大型企业网络</td>
     *     </tr>
     *     <tr>
     *       <td>/16</td>
     *       <td>255.255.0.0</td>
     *       <td>65,536</td>
     *       <td>65,534</td>
     *       <td>中型网络</td>
     *     </tr>
     *     <tr>
     *       <td>/20</td>
     *       <td>255.255.240.0</td>
     *       <td>4,096</td>
     *       <td>4,094</td>
     *       <td>中型子网</td>
     *     </tr>
     *     <tr>
     *       <td>/24</td>
     *       <td>255.255.255.0</td>
     *       <td>256</td>
     *       <td>254</td>
     *       <td>小型网络（最常见）</td>
     *     </tr>
     *     <tr>
     *       <td>/25</td>
     *       <td>255.255.255.128</td>
     *       <td>128</td>
     *       <td>126</td>
     *       <td>更小的子网</td>
     *     </tr>
     *     <tr>
     *       <td>/26</td>
     *       <td>255.255.255.192</td>
     *       <td>64</td>
     *       <td>62</td>
     *       <td>最小实用子网</td>
     *     </tr>
     *     <tr>
     *       <td>/27</td>
     *       <td>255.255.255.224</td>
     *       <td>32</td>
     *       <td>30</td>
     *       <td>小型子网</td>
     *     </tr>
     *     <tr>
     *       <td>/28</td>
     *       <td>255.255.255.240</td>
     *       <td>16</td>
     *       <td>14</td>
     *       <td>更小的子网</td>
     *     </tr>
     *     <tr>
     *       <td>/29</td>
     *       <td>255.255.255.248</td>
     *       <td>8</td>
     *       <td>6</td>
     *       <td>极小网络</td>
     *     </tr>
     *     <tr>
     *       <td>/30</td>
     *       <td>255.255.255.252</td>
     *       <td>4</td>
     *       <td>2</td>
     *       <td>点对点连接</td>
     *     </tr>
     *     <tr>
     *       <td>/31</td>
     *       <td>255.255.255.254</td>
     *       <td>2</td>
     *       <td>2</td>
     *       <td>点对点连接（RFC 3021）</td>
     *     </tr>
     *     <tr>
     *       <td>/32</td>
     *       <td>255.255.255.255</td>
     *       <td>1</td>
     *       <td>1</td>
     *       <td>单个主机</td>
     *     </tr>
     *   </tbody>
     * </table>
     *
     * <p>
     * <b>地址数量计算公式：</b>
     * <br>总地址数 = 2<sup>(32 - prefixLength)</sup>
     * <br>可用地址数 = 总地址数 - 2（/31 和 /32 网段除外）
     * </p>
     *
     * <p>
     * <b>注意事项：</b>
     * <ul>
     *   <li>/31 网段无网络地址和广播地址，2 个地址均可使用（RFC 3021）</li>
     *   <li>/32 网段表示单个主机地址</li>
     *   <li>对于 /30 及更小的网段，建议用于点对点连接</li>
     *   <li>/24 网段是最常见的子网划分方式</li>
     * </ul>
     * </p>
     * @param ip   要检查的 IP 地址
     * @param cidr CIDR 格式（如 "192.168.1.0/24"）
     */
    public static boolean isInCIDR(String ip, String cidr) {
        try {
            // 解析 CIDR
            String[] parts = cidr.split("/");
            String network = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);

            // 计算子网掩码
            int maskBytes = prefixLength == 0 ? 0 : (0xFFFFFFFF << (32 - prefixLength));

            // 转换 IP 为整数
            long ipLong = ipToLong(InetAddress.getByName(ip));
            long networkLong = ipToLong(InetAddress.getByName(network));

            // 比较（保留网段部分）
            long maskedIp = ipLong & maskBytes;
            long maskedNetwork = networkLong & maskBytes;

            return maskedIp == maskedNetwork;

        } catch (UnknownHostException e) {
            return false;
        }
    }

    /**
     * 判断 IP 是否在范围内（包含边界）
     *
     * @param ip    要判断的ip地址
     * @param start 起始ip地址
     * @param end   结束ip地址
     * @return 是否在范围内
     */
    public static boolean isBetween(String ip, String start, String end) {
        long ipLong = ipv4ToLong(ip);
        long startLong = ipToLong(start);
        long endLong = ipToLong(end);

        // 确保 start <= end
        if (startLong > endLong) {
            long temp = startLong;
            startLong = endLong;
            endLong = temp;
        }

        return ipLong >= startLong && ipLong <= endLong;
    }

    /**
     * IP 转 long（用于位运算）
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
     * IP 转 long
     */
    public static long ipToLong(String ip) {
        try {
            return ipToLong(InetAddress.getByName(ip));
        } catch (UnknownHostException e) {
            throw new UncheckedIOException(e);
        }
    }

}
