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

import static org.codehaus.plexus.util.IOUtil.close;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.Session;

@Mojo(name = Files.API_METHOD)
public class Files extends DropboxMojo {

    static final String API_METHOD = "files";

    @Parameter(required = true)
    String request_type;

    @Parameter
    String rev;

    @Parameter(required = true)
    String path;

    @Parameter
    File file;

    public Files() {
        super(API_METHOD);
    }

    Files(final DropboxFactory<? extends Session> dropboxFactory) {
        super(API_METHOD, dropboxFactory);
    }

    @Override
    protected final void call(final DropboxAPI<? extends Session> dropbox) throws MojoExecutionException {
        OutputStream out = null;
        boolean theFileShouldBeClosed = false;
        try {
            if (file == null) {
                out = System.out;
            } else {
                if (!file.exists()) {
                    final File parentFile = file.getParentFile();
                    if (parentFile != null) {
                        parentFile.mkdirs();
                    }
                    file.createNewFile();
                }
                out = new FileOutputStream(file);
                theFileShouldBeClosed = true;
            }
            dropbox.getFile(path, rev, out, progressListener);
        } catch (final IOException | DropboxException e) {
            throw new DropboxMojoExecutionException(e);
        } finally {
            if (theFileShouldBeClosed) {
                close(out);
            }
        }
    }
}
