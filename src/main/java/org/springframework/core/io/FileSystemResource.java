package org.springframework.core.io;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @date 2022/1/8 14:43
 */
public class FileSystemResource implements Resource {
    private final String path;

    public FileSystemResource(String path) {
        this.path = path;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return FileUtils.openInputStream (new File (path));
    }
}
