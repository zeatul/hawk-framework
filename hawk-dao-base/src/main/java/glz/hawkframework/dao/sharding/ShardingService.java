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

package glz.hawkframework.dao.sharding;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * This interface is responsible for
 * <p>定义计算分片号的方法</p>
 *
 * @author Zhang Peng
 */
public interface ShardingService {


    /**
     * 生成分片号
     *
     * @param shardingParam 用来计算分片号的参数
     * @return 分片号
     */
    @Nonnull
    String generateShardingId(@Nonnull ShardingParam shardingParam);
}
