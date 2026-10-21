/*
 * Copyright 2025-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package glz.hawkframework.core.helper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class IpHelperTest {

    @Test
    public void ipv4ToLong_basic(){
        long result = IpHelper.ipv4ToLong("192.168.1.1");
        assertThat(result).isEqualTo(3232235777L);
    }

    @Test
    public void ipv4ToLong_zero_allowed(){
        // 0.0.0.0 is valid IPv4
        long result = IpHelper.ipv4ToLong("0.0.0.0");
        assertThat(result).isEqualTo(0L);
    }

    @Test
    public void ipv4ToLong_max_allowed(){
        // 255.255.255.255 is valid
        long result = IpHelper.ipv4ToLong("255.255.255.255");
        assertThat(result).isEqualTo(4294967295L);
    }

    @Test
    public void ipv4ToLong_invalidFormat_throws(){
        assertThatThrownBy(() -> IpHelper.ipv4ToLong("256.1.1.1"))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> IpHelper.ipv4ToLong("1.2.3"))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> IpHelper.ipv4ToLong("abc.def.ghi.jkl"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void longToIpv4_basic(){
        String result = IpHelper.longToIpv4(3232235777L);
        assertThat(result).isEqualTo("192.168.1.1");
    }

    @Test
    public void longToIpv4_zero_allowed(){
        // 0.0.0.0 (0) should be valid
        String result = IpHelper.longToIpv4(0L);
        assertThat(result).isEqualTo("0.0.0.0");
    }

    @Test
    public void longToIpv4_negative_throws(){
        assertThatThrownBy(() -> IpHelper.longToIpv4(-1L))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void isInNetwork_ipv4_basic(){
        assertThat(IpHelper.isInNetwork("192.168.1.50", "192.168.1.0", "255.255.255.0")).isTrue();
        assertThat(IpHelper.isInNetwork("192.168.2.50", "192.168.1.0", "255.255.255.0")).isFalse();
    }

    @Test
    public void isInNetwork_ipv6_rejected(){
        // IPv6 addresses should be rejected
        assertThatThrownBy(() -> IpHelper.isInNetwork(
            "2001:db8::1", "2001:db8::", "ffff:ffff:ffff:ffff::"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void isInCIDR_basic(){
        assertThat(IpHelper.isInCIDR("192.168.1.50", "192.168.1.0/24")).isTrue();
        assertThat(IpHelper.isInCIDR("192.168.2.50", "192.168.1.0/24")).isFalse();
    }

    @Test
    public void isInCIDR_ipv6_rejected(){
        assertThatThrownBy(() -> IpHelper.isInCIDR(
            "2001:db8::1", "2001:db8::/64"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void isBetween_basic(){
        assertThat(IpHelper.isBetween("192.168.1.50", "192.168.1.10", "192.168.1.100")).isTrue();
        assertThat(IpHelper.isBetween("192.168.1.5", "192.168.1.10", "192.168.1.100")).isFalse();
    }

    @Test
    public void isBetween_swappedStartEnd_works(){
        // Should handle start > end by swapping
        assertThat(IpHelper.isBetween("192.168.1.50", "192.168.1.100", "192.168.1.10")).isTrue();
    }

    @Test
    public void isBetween_invalidIp_throws(){
        assertThatThrownBy(() -> IpHelper.isBetween("invalid", "192.168.1.1", "192.168.1.100"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void isInNetwork_hostname_resolvesToIpv4_works(){
        // "localhost" resolves to 127.0.0.1 (IPv4), so it works
        // This test documents the current behavior - hostnames that resolve to IPv4 work
        assertThat(IpHelper.isInNetwork("localhost", "127.0.0.0", "255.0.0.0")).isTrue();
    }
}