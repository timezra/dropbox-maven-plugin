package timezra.dropbox.maven.plugin;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.Session;

@RunWith(MockitoJUnitRunner.class)
public class FilesTest extends DropboxMojoTest<Files> {

    private static final String PATH = "TestPath.txt";
    private static final String REV = "1234";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void should_output_to_the_console_by_default() throws MojoExecutionException, MojoFailureException,
            DropboxException, IOException {
        dropboxMojo.execute();

        verify(dropboxAPI).getFile(eq(PATH), eq(REV), eq(System.out), isNotNull(ProgressListener.class));
    }

    @Test
    public void should_output_to_a_specified_file() throws MojoExecutionException, MojoFailureException, DropboxException,
            IOException {
        dropboxMojo.file = temporaryFolder.newFile();

        dropboxMojo.execute();

        verify(dropboxAPI).getFile(eq(PATH), eq(REV), isNotNull(FileOutputStream.class), isNotNull(ProgressListener.class));
    }

    @Test
    public void should_create_a_nonexistent_output_file() throws MojoExecutionException, MojoFailureException,
            DropboxException, IOException {
        dropboxMojo.file = new File(temporaryFolder.getRoot(), "testFile.txt");

        assertThat(dropboxMojo.file.exists(), is(false));
        dropboxMojo.execute();
        assertThat(dropboxMojo.file.exists(), is(true));
    }

    @Test
    public void should_create_any_nonexistent_parent_directories() throws MojoExecutionException, MojoFailureException,
            DropboxException, IOException {
        dropboxMojo.file = new File(temporaryFolder.getRoot(), "subdirectory/testFile.txt");

        assertThat(dropboxMojo.file.getParentFile().exists(), is(false));
        dropboxMojo.execute();
        assertThat(dropboxMojo.file.getParentFile().exists(), is(true));
    }

    @Test
    public void should_fail_on_dropbox_error() throws MojoExecutionException, DropboxException {
        expectMojoException();

        when(dropboxAPI.getFile(any(String.class), any(String.class), any(OutputStream.class), any(ProgressListener.class)))
                .thenThrow(new DropboxException(""));

        dropboxMojo.execute();
    }

    @Test
    public void should_fail_on_file_error() throws MojoExecutionException, DropboxException {
        expectMojoException();

        dropboxMojo.file = new File(String.valueOf('\0'));

        dropboxMojo.execute();
    }

    @Override
    protected Files createDropboxMojo(final DropboxFactory<Session> dropboxFactory) {
        final Files files = new Files(dropboxFactory);
        files.path = PATH;
        files.rev = REV;
        return files;
    }
}
