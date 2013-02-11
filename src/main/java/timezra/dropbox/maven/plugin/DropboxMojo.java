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
        this.dropboxFactory = sessionFactory;
        progressListener = new MavenLogProgressListener(apiMethod);
    }

    @Override
    public final void execute() throws MojoExecutionException {
        final DropboxAPI<? extends Session> dropbox = dropboxFactory.create(new AppKeyPair(oauth_consumer_key,
                oauth_signature), new AccessTokenPair(oauth_token, oauth_token_secret), accessTypes.get(root));
        call(dropbox);
    }

    protected abstract void call(final DropboxAPI<? extends Session> dropbox) throws MojoExecutionException;

    protected class MavenLogProgressListener extends ProgressListener {
        private final String apiMethod;

        public MavenLogProgressListener(final String apiMethod) {
            this.apiMethod = apiMethod;
        }

        @Override
        public void onProgress(final long bytes, final long total) {
            getLog().info(apiMethod + ": " + bytes + " of " + total);
        }
    }
}