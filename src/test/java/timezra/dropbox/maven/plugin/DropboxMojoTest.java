package timezra.dropbox.maven.plugin;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static timezra.dropbox.maven.plugin.Constants.ACCESS_TOKEN;
import static timezra.dropbox.maven.plugin.Constants.CLIENT_IDENTIFIER;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;

@RunWith(MockitoJUnitRunner.class)
public class DropboxMojoTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private DropboxFactory dropboxFactory;

    @Mock
    protected Log log;

    protected DropboxMojo dropboxMojo;

    protected DropboxMojo createDropboxMojo(final DropboxFactory dropboxFactory) {
        return new DropboxMojo(Constants.API_METHOD, dropboxFactory) {
            @Override
            protected void call(final DbxClient client, final ProgressMonitor pm) throws IOException, DbxException {
                pm.begin(1000);
                pm.worked(10);
                pm.done();
            }
        };
    }

    @Before
    public final void setup() {
        dropboxMojo = createDropboxMojo(dropboxFactory);
        dropboxMojo.setLog(log);
        dropboxMojo.clientIdentifier = CLIENT_IDENTIFIER;
        dropboxMojo.accessToken = ACCESS_TOKEN;
    }

    @Test
    public final void should_create_a_new_client() throws MojoExecutionException, MojoFailureException {
        dropboxMojo.execute();

        verify(dropboxFactory).create(CLIENT_IDENTIFIER, ACCESS_TOKEN);
    }

    @Test
    @Ignore
    public final void should_log_if_verbose() throws MojoExecutionException, MojoFailureException, InterruptedException {
        dropboxMojo.verbose = true;
        dropboxMojo.execute();

        verify(log).info(eq(dropboxMojo.apiMethod + ": starting"));
        verify(log).info(matches(dropboxMojo.apiMethod + ": 10 of 1000 \\[\\d+%\\] \\[\\d+h:\\d+m:\\d+s left\\]"));
        verify(log).info(matches(dropboxMojo.apiMethod + ": finished in \\d+h:\\d+m:\\d+s"));
    }

    @Test
    public final void should_not_log_if_quiet() throws MojoExecutionException, MojoFailureException {
        dropboxMojo.execute();

        verify(log, never()).info(anyString());
    }
}
