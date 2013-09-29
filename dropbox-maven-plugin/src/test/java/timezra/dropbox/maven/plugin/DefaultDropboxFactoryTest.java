package timezra.dropbox.maven.plugin;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static timezra.dropbox.maven.plugin.Constants.ACCESS_TOKEN;
import static timezra.dropbox.maven.plugin.Constants.CLIENT_IDENTIFIER;

import org.junit.Test;

import com.dropbox.core.DbxClient;

public class DefaultDropboxFactoryTest {

    @Test
    public void should_create_a_new_dropbox_client() {
        final DefaultDropboxFactory dropboxFactory = new DefaultDropboxFactory();

        final DbxClient client = dropboxFactory.create(CLIENT_IDENTIFIER, ACCESS_TOKEN);

        assertThat(client.getAccessToken(), is(ACCESS_TOKEN));
    }
}
