package timezra.dropbox.maven.plugin;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static timezra.dropbox.maven.plugin.Constants.ACCESS_TOKEN;
import static timezra.dropbox.maven.plugin.Constants.ACCESS_TOKEN_SECRET;
import static timezra.dropbox.maven.plugin.Constants.ACCESS_TYPE;
import static timezra.dropbox.maven.plugin.Constants.APP_KEY;
import static timezra.dropbox.maven.plugin.Constants.APP_SECRET;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import timezra.dropbox.maven.plugin.DropboxMojo.DropboxMojoExecutionException;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.Session.AccessType;

@RunWith(MockitoJUnitRunner.class)
public abstract class DropboxMojoTest<MOJO extends DropboxMojo> {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private DropboxFactory<Session> dropboxFactory;

    @Mock
    protected DropboxAPI<Session> dropboxAPI;

    @Mock
    private Log log;

    protected MOJO dropboxMojo;

    protected abstract MOJO createDropboxMojo(DropboxFactory<Session> dropboxFactory);

    @Before
    public final void setup() {
        dropboxMojo = createDropboxMojo(dropboxFactory);
        dropboxMojo.oauth_consumer_key = APP_KEY;
        dropboxMojo.oauth_signature = APP_SECRET;
        dropboxMojo.oauth_token = ACCESS_TOKEN;
        dropboxMojo.oauth_token_secret = ACCESS_TOKEN_SECRET;
        dropboxMojo.root = ACCESS_TYPE.toString();

        when(dropboxFactory.create(any(AppKeyPair.class), any(AccessTokenPair.class), any(AccessType.class))).thenReturn(
                dropboxAPI);
    }

    @Test
    public final void should_create_a_new_session() throws MojoExecutionException, MojoFailureException {
        dropboxMojo.execute();

        verify(dropboxFactory).create(new AppKeyPair(APP_KEY, APP_SECRET),
                new AccessTokenPair(ACCESS_TOKEN, ACCESS_TOKEN_SECRET), ACCESS_TYPE);
    }

    @Test
    public final void should_log_progress() {
        dropboxMojo.setLog(log);

        dropboxMojo.progressListener.onProgress(10, 1000);
        dropboxMojo.progressListener.onProgress(20, 1000);

        verify(log).info(dropboxMojo.apiMethod + ": 10 of 1000 [1%]");
        verify(log).info(dropboxMojo.apiMethod + ": 20 of 1000 [2%]");
    }

    protected final void expectMojoException() {
        expectedException.expect(DropboxMojoExecutionException.class);
        expectedException.expectMessage("Unable to complete the Dropbox " + dropboxMojo.apiMethod + " request.");
    }
}
