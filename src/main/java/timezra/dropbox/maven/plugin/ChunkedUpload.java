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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;

@Mojo(name = ChunkedUpload.API_METHOD)
public class ChunkedUpload extends DropboxMojo {

    static final String API_METHOD = "chunked_upload";

    @Parameter(defaultValue = "4194304", property = "chunkSize")
    int chunkSize;

    @Parameter(required = true, property = "file")
    File file;

    @Parameter(property = "upload_id")
    String upload_id;

    @Parameter(defaultValue = "0", property = "offset")
    long offset;

    public ChunkedUpload() {
        super(API_METHOD);
    }

    ChunkedUpload(final DropboxFactory dropboxFactory) {
        super(API_METHOD, dropboxFactory);
    }

    @Override
    protected final void call(final DbxClient client, final ProgressMonitor pm) throws IOException, DbxException {
        final int dataSize = (int) Math.min(chunkSize, file.length() - offset);
        pm.begin(file.length());
        final byte[] data = new byte[dataSize];
        try (InputStream in = new FileInputStream(file)) {
            in.skip(offset);
            final int read = in.read(data);
            if (upload_id == null) {
                getLog().info("upload_id=" + client.chunkedUploadFirst(data, 0, read));
            } else {
                final long correctOffset = client.chunkedUploadAppend(upload_id, offset, data);
                if (correctOffset != -1) {
                    getLog().error("expected the offset to be " + correctOffset);
                }
            }
            pm.worked(offset + read);
        }
    }
}