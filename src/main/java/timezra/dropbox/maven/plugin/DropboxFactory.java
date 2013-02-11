package timezra.dropbox.maven.plugin;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.Session.AccessType;

interface DropboxFactory<SESS_T extends Session> {
    DropboxAPI<SESS_T> create(final AppKeyPair appKeyPair, final AccessTokenPair accessTokenPair, final AccessType type);
}
