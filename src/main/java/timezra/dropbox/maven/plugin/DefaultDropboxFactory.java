package timezra.dropbox.maven.plugin;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;

class DefaultDropboxFactory implements DropboxFactory<WebAuthSession> {
    @Override
    public DropboxAPI<WebAuthSession> create(final AppKeyPair appKeyPair, final AccessTokenPair accessTokenPair,
            final AccessType type) {
        return new DropboxAPI<WebAuthSession>(new WebAuthSession(appKeyPair, type, accessTokenPair));
    }
}
