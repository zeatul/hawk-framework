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

package glz.hawkframework.core;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class RateLimiterTest {

    @Test
    public void test() throws InterruptedException {
        System.out.println("Start");
        RateLimiter rateLimiter = RateLimiter.create(1500,1,TimeUnit.SECONDS);
        Thread.sleep(5000);
        int count = 0;
        long before = System.currentTimeMillis();
        for (int i = 0; i < 3000; i++) {
            if (!rateLimiter.tryAcquire(500,TimeUnit.MILLISECONDS)) {
                count++;
            }
        }
        long after = System.currentTimeMillis();
        System.out.println("count = " + count);
        System.out.println("cost time = " + (after - before));
    }
}
