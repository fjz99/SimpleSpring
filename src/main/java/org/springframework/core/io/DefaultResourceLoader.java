package org.springframework.core.io;

import java.io.IOException;
import java.net.URL;

/**
 * @date 2022/1/8 14:45
 */
public class DefaultResourceLoader implements ResourceLoader {
    public static final String CLASSPATH_PREFIX = "classpath:";

    @Override
    public Resource getResource(String location) {
        if (location.startsWith (CLASSPATH_PREFIX)) {
            return new ClassPathResource (location.substring (CLASSPATH_PREFIX.length ()));
        }

        try {
            return new URLResource (new URL (location));
        } catch (IOException e) {
            return new FileSystemResource (location);
        }

    }
}
