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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.Session;

@Mojo(name = FilesPut.apiMethod)
public class FilesPut extends DropboxMojo {

    static final String apiMethod = "files_put";

    @Parameter(defaultValue = "true")
    boolean overwrite;

    @Parameter
    String parent_rev;

    @Parameter(required = true)
    File file;

    @Parameter(required = true)
    String path;

    public FilesPut() {
        super(apiMethod);
    }

    FilesPut(final DropboxFactory<? extends Session> dropboxFactory) {
        super(apiMethod, dropboxFactory);
    }

    @Override
    protected void call(final DropboxAPI<? extends Session> dropbox) throws MojoExecutionException {
        try (InputStream in = new FileInputStream(file)) {
            if (overwrite) {
                dropbox.putFileOverwrite(path, in, file.length(), progressListener);
            } else {
                dropbox.putFile(path, in, file.length(), parent_rev, progressListener);
            }
        } catch (final IOException | DropboxException e) {
            throw new MojoExecutionException("Unable to complete the Dropbox request.", e);
        }
    }
}
