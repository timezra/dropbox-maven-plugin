package timezra.dropbox.maven.plugin;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static timezra.dropbox.maven.plugin.Constants.ACCESS_TOKEN;
import static timezra.dropbox.maven.plugin.Constants.ACCESS_TOKEN_SECRET;
import static timezra.dropbox.maven.plugin.Constants.ACCESS_TYPE;
import static timezra.dropbox.maven.plugin.Constants.APP_KEY;
import static timezra.dropbox.maven.plugin.Constants.APP_SECRET;

import org.junit.Test;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.WebAuthSession;

public class DefaultDropboxFactoryTest {

    @Test
    public void should_create_a_new_dropbox_bridge() {
        final DefaultDropboxFactory dropboxFactory = new DefaultDropboxFactory();

        final AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        final AccessTokenPair accessTokenPair = new AccessTokenPair(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
        final DropboxAPI<WebAuthSession> dropbox = dropboxFactory.create(appKeyPair, accessTokenPair, ACCESS_TYPE);

        final WebAuthSession session = dropbox.getSession();
        assertThat(session.getAppKeyPair(), is(appKeyPair));
        assertThat(session.getAccessTokenPair(), is(accessTokenPair));
        assertThat(session.getAccessType(), is(ACCESS_TYPE));
    }
}
