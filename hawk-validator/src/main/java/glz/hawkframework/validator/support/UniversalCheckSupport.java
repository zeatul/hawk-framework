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

package glz.hawkframework.validator.support;

import glz.hawkframework.validator.constraints.UniversalCheck;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 * <p>支持UniversalCheckValidator，缓存{@link UniversalCheck}</p>
 *
 * @author Zhang Peng
 */
public class UniversalCheckSupport {

    private final static Map<String, Function<?, Boolean>> checkerMap = new HashMap<>();
    private final static Map<String, Function<?, Map<String, Object>>> messageParameterSupplierMap = new HashMap<>();

    /**
     * 获取校验方法
     *
     * @param checkerName 校验方法名
     * @return 校验方法
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> Function<T, Boolean> loadChecker(String checkerName) {
        Function<T, Boolean> checker = (Function<T, Boolean>) checkerMap.get(argNotNull(checkerName, "checkMethod"));
        if (checker == null) throw new IllegalStateException(String.format("Can't find check method named %s", checkerName));
        return checker;
    }

    /**
     * 查找[消息模板参数提供者]方法，用来在校验失败的时候，获取消息模板需要的参数
     *
     * @param checkerName 校验方法名
     * @return 消息参数提供者
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> Function<T, Map<String, Object>> findMessageParameterSupplier(@Nonnull String checkerName) {
        return (Function<T, Map<String, Object>>) messageParameterSupplierMap.get(argNotNull(checkerName, "checkerName"));
    }

    /**
     * 新增校验方法
     *
     * @param checkerName 校验方法名
     * @param function    校验方法
     */
    public static <T> void addChecker(String checkerName, Function<T, Boolean> function) {
        if (checkerMap.putIfAbsent(argNotNull(checkerName, "checkerName"), argNotNull(function, "function")) != null)
            throw new IllegalArgumentException(String.format("Duplicate check method named %s", checkerName));
    }

    public static <T> void addMessageParameterSupplier(String checkerName, Function<T, Map<String, Object>> function) {
        if (messageParameterSupplierMap.putIfAbsent(argNotNull(checkerName, "checkerName"), argNotNull(function, "function")) != null)
            throw new IllegalArgumentException(String.format("Duplicate check method named %s", checkerName));
    }
}
