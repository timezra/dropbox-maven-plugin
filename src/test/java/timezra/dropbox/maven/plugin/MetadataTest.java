package timezra.dropbox.maven.plugin;

import static java.lang.String.format;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.Session;

@RunWith(MockitoJUnitRunner.class)
public class MetadataTest extends DropboxMojoTest<Metadata> {

    private static final String PATH = "TestPath.txt";
    private static final int DEFAULT_FILE_LIMIT = 10000;
    private static final String HASH = "abcdefgh";
    private static final boolean DEFAULT_LIST = true;
    private static final String REV = "1234";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void should_get_the_dropbox_metadata() throws MojoExecutionException, MojoFailureException, DropboxException,
            IOException {
        dropboxMojo.execute();

        verify(dropboxAPI).metadata(eq(PATH), eq(DEFAULT_FILE_LIMIT), eq(HASH), eq(DEFAULT_LIST), eq(REV));
    }

    @Test
    public void should_override_the_default_file_limit() throws MojoExecutionException, MojoFailureException,
            DropboxException, IOException {
        dropboxMojo.file_limit = DEFAULT_FILE_LIMIT + 1;

        dropboxMojo.execute();

        verify(dropboxAPI).metadata(eq(PATH), eq(DEFAULT_FILE_LIMIT + 1), eq(HASH), eq(DEFAULT_LIST), eq(REV));
    }

    @Test
    public void should_override_the_default_list() throws MojoExecutionException, MojoFailureException, DropboxException,
            IOException {
        dropboxMojo.list = !DEFAULT_LIST;

        dropboxMojo.execute();

        verify(dropboxAPI).metadata(eq(PATH), eq(DEFAULT_FILE_LIMIT), eq(HASH), eq(!DEFAULT_LIST), eq(REV));
    }

    @Test
    public void should_output_to_the_log() throws MojoExecutionException, MojoFailureException, DropboxException,
            IOException {
        dropboxMojo.setLog(log);

        final Entry entry = new Entry();
        entry.bytes = 1000;
        entry.clientMtime = "Test Client Mtime";
        entry.contents = Collections.emptyList();
        entry.hash = "Test Hash";
        entry.icon = "Test Icon";
        entry.isDeleted = false;
        entry.isDir = false;
        entry.mimeType = "Test Mime Type";
        entry.modified = "Test Modified";
        entry.path = "Test Path";
        entry.rev = "Test Rev";
        entry.root = "Test Root";
        entry.size = "Test Size";
        entry.thumbExists = false;
        when(dropboxAPI.metadata(eq(PATH), eq(DEFAULT_FILE_LIMIT), eq(HASH), eq(DEFAULT_LIST), eq(REV))).thenReturn(entry);

        dropboxMojo.execute();

        verify(log)
                .info(format(
                        "{\"bytes\":%d,\"clientMtime\":\"%s\",\"contents\":[],\"hash\":\"%s\",\"icon\":\"%s\",\"isDeleted\":%b,\"isDir\":%b,\"mimeType\":\"%s\",\"modified\":\"%s\",\"path\":\"%s\",\"rev\":\"%s\",\"root\":\"%s\",\"size\":\"%s\",\"thumbExists\":%b}",
                        entry.bytes, entry.clientMtime, entry.hash, entry.icon, entry.isDeleted, entry.isDir,
                        entry.mimeType, entry.modified, entry.path, entry.rev, entry.root, entry.size, entry.thumbExists));
    }

    @Test
    public void should_fail_on_dropbox_error() throws MojoExecutionException, DropboxException {
        expectMojoException();

        when(
                dropboxAPI.metadata(any(String.class), any(int.class), any(String.class), any(boolean.class),
                        any(String.class))).thenThrow(new DropboxException(""));

        dropboxMojo.execute();
    }

    @Override
    protected final Metadata createDropboxMojo(final DropboxFactory<Session> dropboxFactory) {
        final Metadata metadata = new Metadata(dropboxFactory);
        metadata.path = PATH;
        metadata.hash = HASH;
        metadata.rev = REV;

        return metadata;
    }
}
