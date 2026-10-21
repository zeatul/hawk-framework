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
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class FileHelperTest {

    @TempDir
    Path tempDir;

    @Test
    public void readAllLines_resourcePath(){
        List<String> lines = FileHelper.readAllLines("/glz/file_read_demo.txt");
        assertThat(lines).hasSize(4);
    }

    @Test
    public void readAllText_resourcePath(){
        String text = FileHelper.readAllText("/glz/file_read_demo.txt");
        assertThat(text).isNotEmpty();
    }

    @Test
    public void readAllBytes_resourcePath(){
        byte[] bytes = FileHelper.readAllBytes("/glz/file_read_demo.txt");
        assertThat(bytes).isNotEmpty();
    }

    @Test
    public void readAllText_file_usesBufferSize(){
        // Create temp file with known content
        File file = tempDir.resolve("test.txt").toFile();
        String content = "Hello\nWorld\n";
        writeFile(file, content);

        // readAllText(File, String, int) should respect bufferSize (no exception, just verify it works)
        String result = FileHelper.readAllText(file, StandardCharsets.UTF_8.name(), 512);
        assertThat(result).isEqualTo(content);
    }

    @Test
    public void readAllBytes_file_nullThrows(){
        assertThatThrownBy(() -> FileHelper.readAllBytes((File) null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void readAllText_file_nullThrows(){
        assertThatThrownBy(() -> FileHelper.readAllText((File) null, StandardCharsets.UTF_8.name()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void readAllLines_file_nullThrows(){
        assertThatThrownBy(() -> FileHelper.readAllLines((File) null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void readAllBytes_path_nullThrows(){
        assertThatThrownBy(() -> FileHelper.readAllBytes((Path) null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void readAllText_path_nullThrows(){
        assertThatThrownBy(() -> FileHelper.readAllText((Path) null, StandardCharsets.UTF_8.name()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void processAllLines_file_nullThrows(){
        assertThatThrownBy(() -> FileHelper.processAllLines((File) null, l -> {}))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void streamOf_nullResourcePath_throws(){
        assertThatThrownBy(() -> FileHelper.streamOf((String) null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void streamOf_emptyResourcePath_throws(){
        assertThatThrownBy(() -> FileHelper.streamOf(""))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void streamOf_nonSlashResourcePath_throws(){
        assertThatThrownBy(() -> FileHelper.streamOf("relative/path"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private void writeFile(File file, String content) {
        try {
            Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}