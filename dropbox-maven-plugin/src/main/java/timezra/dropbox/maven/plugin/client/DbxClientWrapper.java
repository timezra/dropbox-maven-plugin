/*
 * Copyright (c) 2013 timezra
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package timezra.dropbox.maven.plugin.client;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxEntry.File;
import com.dropbox.core.DbxEntry.Folder;
import com.dropbox.core.DbxEntry.WithChildrenC;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxHost;
import com.dropbox.core.DbxPath;
import com.dropbox.core.DbxRequestUtil;
import com.dropbox.core.DbxUrlWithExpiration;
import com.dropbox.core.http.HttpRequestor;
import com.dropbox.core.json.JsonArrayReader;
import com.dropbox.core.json.JsonDateReader;
import com.dropbox.core.json.JsonReadException;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.util.Collector;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class DbxClientWrapper {

    private final DbxClient client;

    public DbxClientWrapper(final DbxClient client) {
        this.client = client;
    }

    private String getApi() {
        return DbxHost.Default.api;
    }

    /**
     * All this nonsense because the the shareable url should be short by default
     */
    public String createShareableUrl(final String path, final boolean short_url) throws DbxException {
        DbxPath.checkArg("path", path);

        final String apiPath = "1/shares/auto" + path;
        final String[] params = { "short_url", String.valueOf(short_url) };

        return client.doPost(getApi(), apiPath, params, null, new DbxRequestUtil.ResponseHandler<String>() {
            @Override
            public String handle(final HttpRequestor.Response response) throws DbxException {
                if (response.statusCode == 404) {
                    return null;
                }
                if (response.statusCode != 200) {
                    throw DbxRequestUtil.unexpectedStatus(response);
                }
                final DbxUrlWithExpiration uwe = DbxRequestUtil.readJsonFromResponse(DbxUrlWithExpiration.Reader,
                        response.body);
                return uwe.url;
            }
        });
    }

    /**
     * All this nonsense because the DbxEntry#read method returns null if an entry is deleted
     */
    public DbxEntry.File restoreFile(final String path, final String rev) throws DbxException {
        DbxPath.checkArgNonRoot("path", path);
        if (rev == null) {
            throw new IllegalArgumentException("'rev' can't be null");
        }
        if (rev.length() == 0) {
            throw new IllegalArgumentException("'rev' can't be empty");
        }

        final String apiPath = "1/restore/auto" + path;
        final String[] params = { "rev", rev };

        return DbxRequestUtil.doGet(client.getRequestConfig(), client.getAccessToken(), getApi(), apiPath, params, null,
                new DbxRequestUtil.ResponseHandler<DbxEntry.File>() {
                    @Override
                    public DbxEntry.File handle(final HttpRequestor.Response response) throws DbxException {
                        if (response.statusCode == 404) {
                            return null;
                        }
                        if (response.statusCode != 200) {
                            throw DbxRequestUtil.unexpectedStatus(response);
                        }
                        return DbxRequestUtil.readJsonFromResponse(Reader, response.body);
                    }
                });
    }

    /**
     * All this nonsense because the DbxEntry#read method returns null if an entry is deleted
     */
    public List<File> getRevisions(final String path) throws DbxException {
        DbxPath.checkArgNonRoot("path", path);

        final String apiPath = "1/revisions/auto" + path;

        return DbxRequestUtil.doGet(client.getRequestConfig(), client.getAccessToken(), getApi(), apiPath, null, null,
                new DbxRequestUtil.ResponseHandler<List<File>>() {
                    @Override
                    public List<File> handle(final HttpRequestor.Response response) throws DbxException {
                        if (response.statusCode == 406) {
                            return null;
                        }
                        if (response.statusCode != 200) {
                            throw DbxRequestUtil.unexpectedStatus(response);
                        }
                        return DbxRequestUtil.readJsonFromResponse(JsonArrayReader.mk(Reader), response.body);
                    }
                });
    }

    private static final JsonReader<File> Reader = new JsonReader<File>() {
        @Override
        public final File read(final JsonParser parser) throws IOException, JsonReadException {
            final JsonLocation top = parser.getCurrentLocation();
            final WithChildrenC<Object> theEntry = readWithNulls(parser, null);
            if (theEntry == null) {
                return null;
            }
            final DbxEntry e = theEntry.entry;
            if (!(e instanceof File)) {
                throw new JsonReadException("Expecting a file entry, got a folder entry", top);
            }
            return (File) e;
        }
    };

    private static final int FM_size = 0;
    private static final int FM_bytes = 1;
    private static final int FM_path = 2;
    private static final int FM_is_dir = 3;
    private static final int FM_is_deleted = 4;
    private static final int FM_rev = 5;
    private static final int FM_thumb_exists = 6;
    private static final int FM_icon = 7;
    private static final int FM_modified = 8;
    private static final int FM_client_mtime = 9;
    private static final int FM_hash = 10;
    private static final int FM_contents = 11;
    private static final JsonReader.FieldMapping FM;
    static {
        final JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("size", FM_size);
        b.add("bytes", FM_bytes);
        b.add("path", FM_path);
        b.add("is_dir", FM_is_dir);
        b.add("is_deleted", FM_is_deleted);
        b.add("rev", FM_rev);
        b.add("thumb_exists", FM_thumb_exists);
        b.add("icon", FM_icon);
        b.add("modified", FM_modified);
        b.add("client_mtime", FM_client_mtime);
        b.add("hash", FM_hash);
        b.add("contents", FM_contents);
        FM = b.build();
    }

    private static <C> WithChildrenC<C> readWithNulls(final JsonParser parser,
            final Collector<DbxEntry, ? extends C> collector) throws IOException, JsonReadException {
        final JsonLocation top = JsonReader.expectObjectStart(parser);

        String size = null;
        long bytes = -1;
        String path = null;
        Boolean is_dir = null;
        Boolean is_deleted = null;
        String rev = null;
        Boolean thumb_exists = null;
        String icon = null;
        Date modified = null;
        Date client_mtime = null;
        String hash = null;
        C contents = null;

        while (parser.getCurrentToken() == JsonToken.FIELD_NAME) {
            final String fieldName = parser.getCurrentName();
            JsonReader.nextToken(parser);

            final int fi = FM.get(fieldName);
            try {
                switch (fi) {
                case -1:
                    JsonReader.skipValue(parser);
                    break;
                case FM_size:
                    size = JsonReader.StringReader.readField(parser, fieldName, size);
                    break;
                case FM_bytes:
                    bytes = JsonReader.readUnsignedLongField(parser, fieldName, bytes);
                    break;
                case FM_path:
                    path = JsonReader.StringReader.readField(parser, fieldName, path);
                    break;
                case FM_is_dir:
                    is_dir = JsonReader.BooleanReader.readField(parser, fieldName, is_dir);
                    break;
                case FM_is_deleted:
                    is_deleted = JsonReader.BooleanReader.readField(parser, fieldName, is_deleted);
                    break;
                case FM_rev:
                    rev = JsonReader.StringReader.readField(parser, fieldName, rev);
                    break;
                case FM_thumb_exists:
                    thumb_exists = JsonReader.BooleanReader.readField(parser, fieldName, thumb_exists);
                    break;
                case FM_icon:
                    icon = JsonReader.StringReader.readField(parser, fieldName, icon);
                    break;
                case FM_modified:
                    modified = JsonDateReader.Dropbox.readField(parser, fieldName, modified);
                    break;
                case FM_client_mtime:
                    client_mtime = JsonDateReader.Dropbox.readField(parser, fieldName, client_mtime);
                    break;
                case FM_hash:
                    if (collector == null) {
                        throw new JsonReadException("not expecting \"hash\" field, since we didn't ask for children",
                                parser.getCurrentLocation());
                    }
                    hash = JsonReader.StringReader.readField(parser, fieldName, hash);
                    break;
                case FM_contents:
                    if (collector == null) {
                        throw new JsonReadException("not expecting \"contents\" field, since we didn't ask for children",
                                parser.getCurrentLocation());
                    }
                    contents = JsonArrayReader.mk(Reader, collector).readField(parser, fieldName, contents);
                    break;
                default:
                    throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                }
            } catch (final JsonReadException ex) {
                throw ex.addFieldContext(fieldName);
            }
        }

        JsonReader.expectObjectEnd(parser);

        if (path == null) {
            throw new JsonReadException("missing field \"path\"", top);
        }
        if (icon == null) {
            throw new JsonReadException("missing field \"icon\"", top);
        }
        if (is_deleted == null) {
            is_deleted = Boolean.FALSE;
        }
        if (is_dir == null) {
            is_dir = Boolean.FALSE;
        }
        if (thumb_exists == null) {
            thumb_exists = Boolean.FALSE;
        }

        if (is_dir && (contents != null || hash != null)) {
            if (hash == null) {
                throw new JsonReadException("missing \"hash\", when we asked for children", top);
            }
            if (contents == null) {
                throw new JsonReadException("missing \"contents\", when we asked for children", top);
            }
        }

        DbxEntry e;
        if (is_dir) {
            e = new Folder(path, icon, thumb_exists);
        } else {
            // Normal File
            if (size == null) {
                throw new JsonReadException("missing \"size\" for a file entry", top);
            }
            if (bytes == -1) {
                throw new JsonReadException("missing \"bytes\" for a file entry", top);
            }
            if (modified == null) {
                throw new JsonReadException("missing \"modified\" for a file entry", top);
            }
            if (client_mtime == null) {
                throw new JsonReadException("missing \"client_mtime\" for a file entry", top);
            }
            if (rev == null) {
                throw new JsonReadException("missing \"rev\" for a file entry", top);
            }
            e = new File(path, icon, thumb_exists, bytes, size, modified, client_mtime, rev);
        }

        return new WithChildrenC<C>(e, hash, contents);
    }
}
