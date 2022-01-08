package org.springframework.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @date 2022/1/8 14:37
 */
public class ClassPathResource implements Resource {
    public static final String CLASSPATH_PREFIX = "classpath:";
    private final String path;

    public ClassPathResource(String path) {
        if (path.startsWith ("/")) {
            path = path.substring (1);
        }
        this.path = path;
    }

    /**
     * classloader确定的是绝对路径，而且我的实现不能跨jar包
     */
    @Override
    public InputStream getInputStream() throws IOException {
        InputStream stream = getClass ().getClassLoader ().getResourceAsStream (path);
        if (stream == null) {
            throw new FileNotFoundException ();
        }
        return stream;
    }
}
