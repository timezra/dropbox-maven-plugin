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
package timezra.dropbox.maven.plugin;

import java.io.IOException;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

@Mojo(name = CommitChunkedUpload.API_METHOD)
public class CommitChunkedUpload extends DropboxMojo {

    static final String API_METHOD = "commit_chunked_upload";

    @Parameter(required = true, property = "path")
    String path;

    @Parameter(defaultValue = "true", property = "overwrite")
    boolean overwrite;

    @Parameter(required = true, property = "upload_id")
    String upload_id;

    @Parameter(property = "parent_rev")
    String parent_rev;

    public CommitChunkedUpload() {
        super(API_METHOD);
    }

    CommitChunkedUpload(final DropboxFactory dropboxFactory) {
        super(API_METHOD, dropboxFactory);
    }

    @Override
    protected final void call(final DbxClient client, final ProgressMonitor pm) throws IOException, DbxException {
        pm.begin(1);
        final DbxWriteMode writeMode = overwrite ? DbxWriteMode.force() : DbxWriteMode.update(parent_rev);
        client.chunkedUploadFinish(path, writeMode, upload_id);
    }
}
