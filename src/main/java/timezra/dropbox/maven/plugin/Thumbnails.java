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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.IOUtil;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxThumbnailFormat;
import com.dropbox.core.DbxThumbnailSize;

@Mojo(name = Thumbnails.API_METHOD)
public class Thumbnails extends DropboxMojo {

    static final String API_METHOD = "thumbnails";

    @Parameter(required = true, property = "path")
    String path;

    @Parameter(property = "file")
    File file;

    @Parameter(defaultValue = "jpeg", property = "format")
    String format;

    @Parameter(defaultValue = "s", property = "size")
    String size;

    @Parameter(property = "rev")
    String rev;

    public Thumbnails() {
        super(API_METHOD);
    }

    Thumbnails(final DropboxFactory dropboxFactory) {
        super(API_METHOD, dropboxFactory);
    }

    @Override
    protected final void call(final DbxClient client, final ProgressMonitor pm) throws DbxException, IOException {
        pm.begin(1);
        OutputStream out = null;
        boolean theFileShouldBeClosed = false;

        try {
            if (file == null) {
                out = System.out;
            } else {
                if (!file.exists()) {
                    final File parentFile = file.getParentFile();
                    if (parentFile != null) {
                        parentFile.mkdirs();
                    }
                    file.createNewFile();
                }
                out = new FileOutputStream(file);
                theFileShouldBeClosed = true;
            }
            final com.dropbox.core.DbxEntry.File thumbnail = client.getThumbnail(size(), format(), path, rev, out);
            if (thumbnail == null) {
                file.delete();
                getLog().info("no file at that path");
            } else {
                getLog().info(thumbnail.toString());
            }
        } finally {
            if (theFileShouldBeClosed) {
                IOUtil.close(out);
            }
        }
    }

    private DbxThumbnailFormat format() {
        switch (format) {
        case "jpeg":
            return DbxThumbnailFormat.JPEG;
        case "png":
            return DbxThumbnailFormat.PNG;
        default:
            return DbxThumbnailFormat.bestForFileName(path, DbxThumbnailFormat.JPEG);
        }
    }

    private DbxThumbnailSize size() {
        switch (size) {
        case "xs":
            return DbxThumbnailSize.w32h32;
        case "s":
            return DbxThumbnailSize.w64h64;
        case "m":
            return DbxThumbnailSize.w128h128;
        case "l":
            return DbxThumbnailSize.w640h480;
        case "xl":
            return DbxThumbnailSize.w1024h768;
        default:
            throw new IllegalArgumentException("Unknown size " + size);
        }
    }
}
