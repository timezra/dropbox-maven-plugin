/*
 * Copyright (c) 2013 timezra
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package timezra.dropbox.maven.plugin;

import java.text.SimpleDateFormat;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxUrlWithExpiration;

@Mojo(name = Media.API_METHOD)
public class Media extends DropboxMojo {

    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z"; // rfc822

    static final String API_METHOD = "media";

    @Parameter(required = true, property = "path")
    String path;

    public Media() {
        super(API_METHOD);
    }

    Media(final DropboxFactory dropboxFactory) {
        super(API_METHOD, dropboxFactory);
    }

    @Override
    protected final void call(final DbxClient client, final ProgressMonitor pm) throws DbxException {
        pm.begin(1);
        final DbxUrlWithExpiration urlWithExpiration = client.createTemporaryDirectUrl(path);
        if (urlWithExpiration == null) {
            getLog().info("no file at that path");
        } else {
            getLog().info("url=" + urlWithExpiration.url);
            getLog().info("expires=" + new SimpleDateFormat(DATE_FORMAT).format(urlWithExpiration.expires));
        }
    }
}
