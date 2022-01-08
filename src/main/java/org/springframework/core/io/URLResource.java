package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @date 2022/1/8 14:42
 */
public class URLResource implements Resource {
    private final URL url;

    public URLResource(URL url) {
        this.url = url;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return url.openStream ();
    }

}
