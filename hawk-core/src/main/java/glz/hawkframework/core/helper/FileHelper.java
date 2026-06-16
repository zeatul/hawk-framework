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

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static glz.hawkframework.core.support.ArgumentSupport.*;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public abstract class FileHelper {
    public final static int DEFAULT_BUFFER_SIZE = 4096;
    public final static String DEFAULT_CHARSET = "utf-8";


    /**
     * Gets the inputStream described by resourcePath in the classpath.
     * <p>The resourcePath must start with '/'.</p>
     * <p>Package names are separated by "/".</p>
     *
     * @param resourcePath the absolute location of resource in the classpath.
     * @return the required inputStream, never {@code null}.
     */
    public static InputStream streamOf(String resourcePath) {
        argument(argNotBlank(resourcePath, "resourcePath"), s -> s.startsWith("/"), s -> "The resourcePath must start with '/'");

        return Optional.ofNullable(FileHelper.class.getResourceAsStream(argNotBlank(resourcePath, "resourcePath"))).orElseThrow(() -> new IllegalArgumentException(String.format("The resource('%s') was not found.", resourcePath)));
    }

    /**
     * Gets the inputStream described by File.
     *
     * @param file the required file.
     * @return the required inputStream, never {@code null}.
     */
    public static InputStream streamOf(File file) {
        try {
            return Files.newInputStream(argNotNull(file, "file").toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Gets the inputStream described by path.
     *
     * @param path the required path.
     * @return the required inputStream, never {@code null}.
     */
    public static InputStream streamOf(Path path) {
        try {
            return Files.newInputStream(argNotNull(path, "path"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Gets the inputStream described by filePath.
     *
     * @param filePath the absolute location of the file.
     * @return the required inputStream, never {@code null}.
     */
    public static InputStream streamOfFilePath(String filePath) {
        try {
            return Files.newInputStream(Paths.get(argNotBlank(filePath, "filePath")));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Read all bytes of the input stream.
     *
     * @param inputStream the required input stream.
     * @param bufferSize  the required buffer size, in bytes.
     * @return a byte array containing all bytes read, never {@code null}.
     */
    public static byte[] readAllBytes(InputStream inputStream, int bufferSize) {
        argNotNull(inputStream, "inputStream");
        argument(bufferSize, s -> s > 0, s -> "The bufferSize must bt greater than zero.");
        try (InputStream is = inputStream; ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[bufferSize];
            int readedBytes;
            while ((readedBytes = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readedBytes);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Read all bytes in the path.
     *
     * @param path       the required path.
     * @param bufferSize the required buffer size, in bytes.
     * @return a byte array containing all bytes read, never {@code null}.
     */
    public static byte[] readAllBytes(Path path, int bufferSize) {
        return readAllBytes(streamOf(path), bufferSize);
    }

    /**
     * Read all bytes in the path.
     *
     * @param path the required path.
     * @return a byte array containing all bytes read, never {@code null}.
     */
    public static byte[] readAllBytes(Path path) {
        return readAllBytes(path, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Read all bytes of a file at the specified path under the classpath.
     * <p>The resourcePath must start with '/'.</p>
     * <p>Package names are separated by "/".</p>
     *
     * @param resourcePath the required resource path.
     * @param bufferSize   the required buffer size, in bytes.
     * @return a byte array containing all bytes read, never {@code null}.
     */
    public static byte[] readAllBytes(String resourcePath, int bufferSize) {
        return readAllBytes(streamOf(resourcePath), bufferSize);
    }

    /**
     * Read the content of a file at the specified path under the classpath.
     * <p>The resourcePath must start with '/'.</p>
     * <p>Package names are separated by "/".</p>
     *
     * @param resourcePath the required resource path.
     * @return a byte array containing all bytes read, never {@code null}.
     */
    public static byte[] readAllBytes(String resourcePath) {
        return readAllBytes(resourcePath, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Read all bytes in the file.
     *
     * @param file the required file.
     * @return a byte array containing all bytes read, never {@code null}.
     */
    public static @Nonnull byte[] readAllBytes(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Read all bytes in the file.
     *
     * @param file       the required file.
     * @param bufferSize the required buffer size, in bytes.
     * @return a byte array containing all bytes read, never {@code null}.
     */
    public static @Nonnull byte[] readAllBytes(File file, int bufferSize) {
        if (argNotNull(file, "file").length() == 0) return new byte[0];
        try (FileInputStream fis = new FileInputStream(file)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[bufferSize];
            int nRead;
            while ((nRead = fis.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Read all chars in the path.
     *
     * @param path       the required path
     * @param charset    the required charset
     * @param bufferSize the required buffer size, in bytes.
     * @return a string containing all the chars in the path.
     */
    public static String readAllText(Path path, String charset, int bufferSize) {
        return new String(readAllBytes(path, bufferSize), Charset.forName(charset));
    }

    /**
     * Read all chars in the path.
     *
     * @param path    the required path
     * @param charset the required charset
     * @return a string containing all the chars in the path.
     */
    public static String readAllText(Path path, String charset) {
        return readAllText(path, charset, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Read all chars in the path.
     *
     * @param path the required path
     * @return a string containing all the chars in the path.
     */
    public static String readAllText(Path path) {
        return readAllText(path, DEFAULT_CHARSET);
    }

    /**
     * Read all chars in a file at the specified path under the classpath.
     * <p>The resourcePath must start with '/'.</p>
     * <p>Package names are separated by "/".</p>
     *
     * @param resourcePath the required resourcePath
     * @param charset      the required charset
     * @param bufferSize   the required buffer size, in bytes.
     * @return a string containing all the chars in the path.
     */
    public static String readAllText(String resourcePath, String charset, int bufferSize) {
        return new String(readAllBytes(resourcePath), Charset.forName(charset));
    }

    /**
     * Read all chars in a file at the specified path under the classpath.
     * <p>The resourcePath must start with '/'.</p>
     * <p>Package names are separated by "/".</p>
     *
     * @param resourcePath the required resourcePath
     * @param charset      the required charset
     * @return a string containing all the chars in the path.
     */
    public static String readAllText(String resourcePath, String charset) {
        return readAllText(resourcePath, charset, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Read all chars in a file at the specified path under the classpath.
     * <p>The resourcePath must start with '/'.</p>
     * <p>Package names are separated by "/".</p>
     *
     * @param resourcePath the required resourcePath
     * @return a string containing all the chars in the path.
     */
    public static String readAllText(String resourcePath) {
        return readAllText(resourcePath, DEFAULT_CHARSET);
    }


    /**
     * Read all chars in the file.
     *
     * @param file       the required file
     * @param charset    the required charset
     * @param bufferSize the required buffer size, in bytes.
     * @return a string containing all the chars in the file.
     */
    public static String readAllText(File file, String charset, int bufferSize) {
        return new String(readAllBytes(file, bufferSize), Charset.forName(charset));
    }

    /**
     * Read all chars in the file.
     *
     * @param file    the required file
     * @param charset the required charset
     * @return a string containing all the chars in the file.
     */
    public static String readAllText(File file, String charset) {
        return readAllText(file, charset, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Read the entire content of the file line by line.
     *
     * @param file the required file
     * @return a string containing all the chars in the file.
     */
    public static String readAllText(File file) {
        return readAllText(file, DEFAULT_CHARSET);
    }

    /**
     * Read the entire content of the input stream line by line.
     *
     * @param inputStream the required inputStream
     * @param charset     the required charset
     * @param bufferSize  the required buffer size, in bytes.
     * @return a list of string containing entire content of the input stream.
     */
    public static List<String> readAllLines(InputStream inputStream, String charset, int bufferSize) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(charset)), bufferSize)) {
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read the entire content in the path line by line.
     *
     * @param path       the required path
     * @param charset    the required charset
     * @param bufferSize the required buffer size, in bytes.
     * @return a list of string containing entire content in the path.
     */
    public static List<String> readAllLines(Path path, String charset, int bufferSize) {
        return readAllLines(streamOf(path), charset, bufferSize);
    }

    /**
     * Read the entire content in the path line by line.
     *
     * @param path    the required path
     * @param charset the required charset
     * @return a list of string containing entire content in the path.
     */
    public static List<String> readAllLines(Path path, String charset) {
        return readAllLines(path, charset, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Read the entire content in the path line by line.
     *
     * @param path the required path.
     * @return a list of string containing entire content in the path.
     */
    public static List<String> readAllLines(Path path) {
        return readAllLines(path, DEFAULT_CHARSET, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Read the entire content in a file at the specified path under the classpath line by line.
     * <p>The resourcePath must start with '/'.</p>
     * <p>Package names are separated by "/".</p>
     *
     * @param resourcePath the required resourcePath
     * @param charset      the required charset
     * @param bufferSize   the required buffer size, in bytes.
     * @return a list of string containing entire content in the resourcePath.
     */
    public static List<String> readAllLines(String resourcePath, String charset, int bufferSize) {
        return readAllLines(streamOf(resourcePath), charset, bufferSize);
    }

    /**
     * Read the entire content in a file at the specified path under the classpath line by line.
     * <p>The resourcePath must start with '/'.</p>
     * <p>Package names are separated by "/".</p>
     *
     * @param resourcePath the required resourcePath
     * @param charset      the required charset
     * @return a list of string containing entire content in the resourcePath.
     */
    public static List<String> readAllLines(String resourcePath, String charset) {
        return readAllLines(resourcePath, charset, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Read the entire content in a file at the specified path under the classpath line by line.
     * <p>The resourcePath must start with '/'.</p>
     * <p>Package names are separated by "/".</p>
     *
     * @param resourcePath the required resourcePath
     * @return a list of string containing entire content in the resourcePath.
     */
    public static List<String> readAllLines(String resourcePath) {
        return readAllLines(resourcePath, DEFAULT_CHARSET, DEFAULT_BUFFER_SIZE);
    }


    /**
     * Read the entire content in the file line by line.
     *
     * @param file       the required file
     * @param charset    the required charset
     * @param bufferSize the required buffer size, in bytes.
     * @return a list of string containing entire content in the path.
     */
    public static List<String> readAllLines(File file, String charset, int bufferSize) {
        return readAllLines(streamOf(file), charset, bufferSize);
    }

    /**
     * Read the entire content in the file line by line.
     *
     * @param file    the required file
     * @param charset the required charset
     * @return a list of string containing entire content in the path.
     */
    public static List<String> readAllLines(File file, String charset) {
        return readAllLines(file, charset, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Read the entire content in the file line by line.
     *
     * @param file the required file
     * @return a list of string containing entire content in the path.
     */
    public static List<String> readAllLines(File file) {
        return readAllLines(file, DEFAULT_CHARSET, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Process all content of the inputStream line by line.
     *
     * @param inputStream the required inputStream
     * @param charset     the required charset
     * @param bufferSize  the required buffer size, in bytes.
     */
    public static void processAllLines(InputStream inputStream, String charset, int bufferSize, Consumer<String> consumer) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(charset)), bufferSize)) {
            String line;
            while ((line = reader.readLine()) != null) {
                consumer.accept(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Process all content in the path line by line.
     *
     * @param path       the required path
     * @param charset    the required charset
     * @param bufferSize the required buffer size, in bytes.
     */
    public static void processAllLines(Path path, String charset, int bufferSize, Consumer<String> consumer) {
        processAllLines(streamOf(path), charset, bufferSize, consumer);
    }

    /**
     * Process all content in the path line by line.
     *
     * @param path    the required path
     * @param charset the required charset
     */
    public static void processAllLines(Path path, String charset, Consumer<String> consumer) {
        processAllLines(path, charset, DEFAULT_BUFFER_SIZE, consumer);
    }

    /**
     * Process all content in the path line by line.
     *
     * @param path the required path
     */
    public static void processAllLines(Path path, Consumer<String> consumer) {
        processAllLines(path, DEFAULT_CHARSET, consumer);
    }

    /**
     * Process all content in a file at the specified path under the classpath line by line.
     * <p>The resourcePath must start with '/'.</p>
     * <p>Package names are separated by "/".</p>
     *
     * @param resourcePath the required resourcePath
     * @param charset      the required charset
     * @param bufferSize   the required buffer size, in bytes.
     */
    public static void processAllLines(String resourcePath, String charset, int bufferSize, Consumer<String> consumer) {
        processAllLines(streamOf(resourcePath), charset, bufferSize, consumer);
    }

    /**
     * Process all content in a file at the specified path under the classpath line by line.
     * <p>The resourcePath must start with '/'.</p>
     * <p>Package names are separated by "/".</p>
     *
     * @param resourcePath the required resourcePath
     * @param charset      the required charset
     */
    public static void processAllLines(String resourcePath, String charset, Consumer<String> consumer) {
        processAllLines(resourcePath, charset, DEFAULT_BUFFER_SIZE, consumer);
    }

    /**
     * Process all content in a file at the specified path under the classpath line by line.
     * <p>The resourcePath must start with '/'.</p>
     * <p>Package names are separated by "/".</p>
     *
     * @param resourcePath the required resourcePath
     */
    public static void processAllLines(String resourcePath, Consumer<String> consumer) {
        processAllLines(resourcePath, DEFAULT_CHARSET, consumer);
    }

    /**
     * Process all content in the file line by line.
     *
     * @param file       the required file
     * @param charset    the required charset
     * @param bufferSize the required buffer size, in bytes.
     */
    public static void processAllLines(File file, String charset, int bufferSize, Consumer<String> consumer) {
        processAllLines(streamOf(file), charset, bufferSize, consumer);
    }

    /**
     * Process all content in the file line by line.
     *
     * @param file    the required file
     * @param charset the required charset
     */
    public static void processAllLines(File file, String charset, Consumer<String> consumer) {
        processAllLines(file, charset, DEFAULT_BUFFER_SIZE, consumer);
    }

    /**
     * Process all content in the file line by line.
     *
     * @param file the required file
     */
    public static void processAllLines(File file, Consumer<String> consumer) {
        processAllLines(file, DEFAULT_CHARSET, consumer);
    }
}
