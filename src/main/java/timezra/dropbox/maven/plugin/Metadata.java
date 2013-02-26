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

import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.json.simple.JSONValue;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.Session;

@Mojo(name = Metadata.API_METHOD)
public class Metadata extends DropboxMojo {

    static final String API_METHOD = "metadata";

    @Parameter(required = true)
    String path;

    @Parameter
    int file_limit = 10000;

    @Parameter
    String hash;

    @Parameter
    boolean list = true;

    @Parameter
    String rev;

    public Metadata() {
        super(API_METHOD);
    }

    Metadata(final DropboxFactory<? extends Session> dropboxFactory) {
        super(API_METHOD, dropboxFactory);
    }

    @Override
    protected final void call(final DropboxAPI<? extends Session> dropbox) throws MojoExecutionException {
        try {
            final Entry metadata = dropbox.metadata(path, file_limit, hash, list, rev);
            final Map<?, ?> simplified = Simplifier.simplify(metadata);
            if (simplified != null) {
                simplified.remove("JsonExtractor");
                getLog().info(JSONValue.toJSONString(simplified));
            }
        } catch (final DropboxException | IllegalArgumentException | IllegalAccessException e) {
            throw new DropboxMojoExecutionException(e);
        }
    }
}
