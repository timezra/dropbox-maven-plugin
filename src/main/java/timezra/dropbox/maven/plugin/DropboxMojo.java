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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.Session.AccessType;

public abstract class DropboxMojo extends AbstractMojo {

    protected final DropboxFactory<? extends Session> dropboxFactory;

    protected final ProgressListener progressListener;

    @Parameter(required = true)
    protected String oauth_consumer_key;

    @Parameter(required = true)
    protected String oauth_signature;

    @Parameter(required = true)
    protected String oauth_token;

    @Parameter(required = true)
    protected String oauth_token_secret;

    @Parameter(required = true)
    protected String root;

    final String apiMethod;

    private static final Map<String, AccessType> accessTypes;

    static {
        final Map<String, AccessType> m = new HashMap<String, AccessType>();
        for (final AccessType a : AccessType.values()) {
            m.put(a.toString(), a);
        }
        accessTypes = Collections.unmodifiableMap(m);
    }

    protected DropboxMojo(final String apiMethod) {
        this(apiMethod, new DefaultDropboxFactory());
    }

    protected DropboxMojo(final String apiMethod, final DropboxFactory<? extends Session> sessionFactory) {
        dropboxFactory = sessionFactory;
        this.apiMethod = apiMethod;
        progressListener = new MavenLogProgressListener();
    }

    @Override
    public final void execute() throws MojoExecutionException {
        final DropboxAPI<? extends Session> dropbox = dropboxFactory.create(new AppKeyPair(oauth_consumer_key,
                oauth_signature), new AccessTokenPair(oauth_token, oauth_token_secret), accessTypes.get(root));
        call(dropbox);
    }

    protected abstract void call(final DropboxAPI<? extends Session> dropbox) throws MojoExecutionException;

    protected class MavenLogProgressListener extends ProgressListener {
        @Override
        public void onProgress(final long bytes, final long total) {
            final long percent = bytes * 100 / total;
            getLog().info(String.format("%s: %d of %d [%d%%]", apiMethod, bytes, total, percent));
        }
    }

    protected final class DropboxMojoExecutionException extends MojoExecutionException {
        private static final long serialVersionUID = 1L;

        public DropboxMojoExecutionException(final Exception cause) {
            super("Unable to complete the Dropbox " + apiMethod + " request.", cause);
        }
    }
}