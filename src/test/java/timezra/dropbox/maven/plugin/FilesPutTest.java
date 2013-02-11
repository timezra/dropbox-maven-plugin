package timezra.dropbox.maven.plugin;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static timezra.dropbox.maven.plugin.Constants.ACCESS_TOKEN;
import static timezra.dropbox.maven.plugin.Constants.ACCESS_TOKEN_SECRET;
import static timezra.dropbox.maven.plugin.Constants.ACCESS_TYPE;
import static timezra.dropbox.maven.plugin.Constants.APP_KEY;
import static timezra.dropbox.maven.plugin.Constants.APP_SECRET;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.Session.AccessType;

@RunWith(MockitoJUnitRunner.class)
public class FilesPutTest {

    private static final String PATH = "TestPath.txt";
    private static final File FILE = new File("src/test/resources/Test.txt");
    private static final String PARENT_REV = "1234";

    @Mock
    private DropboxFactory<Session> dropboxFactory;

    @Mock
    private DropboxAPI<Session> dropboxAPI;

    @Mock
    private Log log;

    private FilesPut dropboxMojo;

    @Before
    public void setup() {
        dropboxMojo = new FilesPut(dropboxFactory);
        dropboxMojo.oauth_consumer_key = APP_KEY;
        dropboxMojo.oauth_signature = APP_SECRET;
        dropboxMojo.oauth_token = ACCESS_TOKEN;
        dropboxMojo.oauth_token_secret = ACCESS_TOKEN_SECRET;
        dropboxMojo.root = ACCESS_TYPE.toString();
        dropboxMojo.path = PATH;
        dropboxMojo.file = FILE;
        dropboxMojo.parent_rev = PARENT_REV;
        dropboxMojo.overwrite = false;

        when(dropboxFactory.create(any(AppKeyPair.class), any(AccessTokenPair.class), any(AccessType.class))).thenReturn(
                dropboxAPI);
    }

    @Test
    public void should_create_a_new_session() throws MojoExecutionException, MojoFailureException {

        dropboxMojo.execute();

        verify(dropboxFactory).create(new AppKeyPair(APP_KEY, APP_SECRET),
                new AccessTokenPair(ACCESS_TOKEN, ACCESS_TOKEN_SECRET), ACCESS_TYPE);
    }

    @Test
    public void should_call_the_dropbox_api() throws MojoExecutionException, MojoFailureException, DropboxException,
            IOException {

        dropboxMojo.execute();

        verify(dropboxAPI).putFile(eq(PATH), isNotNull(FileInputStream.class), eq(FILE.length()), eq(PARENT_REV),
                isNotNull(ProgressListener.class));
    }

    @Test
    public void should_overwrite_the_remote_file_with_the_dropbox_api() throws MojoExecutionException, MojoFailureException,
            DropboxException, IOException {
        dropboxMojo.overwrite = true;

        dropboxMojo.execute();

        verify(dropboxAPI).putFileOverwrite(eq(PATH), isNotNull(FileInputStream.class), eq(FILE.length()),
                isNotNull(ProgressListener.class));
    }

    @Test(expected = MojoExecutionException.class)
    public void should_fail_on_dropbox_error() throws MojoExecutionException, DropboxException {
        when(
                dropboxAPI.putFile(any(String.class), any(FileInputStream.class), any(int.class), any(String.class),
                        any(ProgressListener.class))).thenThrow(new DropboxException(""));

        dropboxMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    public void should_fail_on_file_error() throws MojoExecutionException, DropboxException {
        dropboxMojo.file = new File("ThisIsNotAFile");

        dropboxMojo.execute();
    }

    @Test
    public void should_log_progress() {
        dropboxMojo.setLog(log);

        dropboxMojo.progressListener.onProgress(10, 1000);
        dropboxMojo.progressListener.onProgress(20, 1000);

        verify(log).info("files_put: 10 of 1000");
        verify(log).info("files_put: 20 of 1000");
    }
}
