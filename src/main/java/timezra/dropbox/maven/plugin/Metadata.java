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

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxEntry.WithChildren;
import com.dropbox.core.DbxException;
import com.dropbox.core.util.Maybe;

@Mojo(name = Metadata.API_METHOD)
public class Metadata extends DropboxMojo {

    static final String API_METHOD = "metadata";

    @Parameter(required = true, property = "path")
    String path;

    // currently hard-coded to 25000
    // @Parameter
    // int file_limit = 25000;

    @Parameter(property = "hash")
    String hash;

    @Parameter(defaultValue = "true", property = "list")
    boolean list;

    // currently not supported
    // @Parameter
    // String rev;

    public Metadata() {
        super(API_METHOD);
    }

    Metadata(final DropboxFactory dropboxFactory) {
        super(API_METHOD, dropboxFactory);
    }

    private Iterable<DbxEntry> from(final WithChildren metadata) {
        final Collection<DbxEntry> entries = new ArrayList<>();
        entries.add(metadata.entry);
        if (metadata.children != null) {
            entries.addAll(metadata.children);
        }
        return entries;
    }

    @Override
    protected final void call(final DbxClient client, final ProgressMonitor pm) throws DbxException {
        final Iterable<DbxEntry> entries;
        pm.begin(1);
        if (list) {
            if (hash == null) {
                entries = from(client.getMetadataWithChildren(path));
            } else {
                final Maybe<WithChildren> metadata = client.getMetadataWithChildrenIfChanged(path, hash);
                entries = metadata.isNothing() ? Collections.<DbxEntry> emptyList() : from(metadata.getJust());
            }
        } else {
            entries = singletonList(client.getMetadata(path));
        }
        for (final DbxEntry entry : entries) {
            getLog().info(entry.toString());
        }
    }
}
