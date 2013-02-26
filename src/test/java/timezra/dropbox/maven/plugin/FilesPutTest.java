package timezra.dropbox.maven.plugin;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.Session;

@RunWith(MockitoJUnitRunner.class)
public class FilesPutTest extends DropboxMojoTest<FilesPut> {

    private static final String PATH = "TestPath.txt";
    private static final File FILE = new File("src/test/resources/Test.txt");
    private static final String PARENT_REV = "1234";

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

    @Test
    public void should_fail_on_dropbox_error() throws MojoExecutionException, DropboxException {
        expectMojoException();

        when(
                dropboxAPI.putFile(any(String.class), any(FileInputStream.class), any(int.class), any(String.class),
                        any(ProgressListener.class))).thenThrow(new DropboxException(""));

        dropboxMojo.execute();
    }

    @Test
    public void should_fail_on_file_error() throws MojoExecutionException, DropboxException {
        expectMojoException();

        dropboxMojo.file = new File("ThisIsNotAFile");

        dropboxMojo.execute();
    }

    @Override
    protected final FilesPut createDropboxMojo(final DropboxFactory<Session> dropboxFactory) {
        final FilesPut filesPut = new FilesPut(dropboxFactory);
        filesPut.path = PATH;
        filesPut.file = FILE;
        filesPut.parent_rev = PARENT_REV;
        filesPut.overwrite = false;
        return filesPut;
    }
}
