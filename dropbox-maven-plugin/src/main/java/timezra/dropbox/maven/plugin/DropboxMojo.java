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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;

public abstract class DropboxMojo extends AbstractMojo {

    protected final DropboxFactory dropboxFactory;

    @Parameter(required = true, property = "clientIdentifier")
    protected String clientIdentifier;

    @Parameter(required = true, property = "accessToken")
    protected String accessToken;

    @Parameter(defaultValue = "false", property = "verbose")
    protected boolean verbose;

    final String apiMethod;

    protected DropboxMojo(final String apiMethod) {
        this(apiMethod, new DefaultDropboxFactory());
    }

    protected DropboxMojo(final String apiMethod, final DropboxFactory dropboxFactory) {
        this.dropboxFactory = dropboxFactory;
        this.apiMethod = apiMethod;
    }

    @Override
    public final void execute() throws MojoExecutionException {
        final DbxClient client = dropboxFactory.create(clientIdentifier, accessToken);
        final ProgressMonitor pm = verbose ? new ThrottlingProgressMonitor(new MavenLogProgressMonitor())
                : NullProgressMonitor.INSTANCE;
        try {
            call(client, pm);
        } catch (IOException | DbxException e) {
            throw new DropboxMojoExecutionException(apiMethod, e);
        } finally {
            pm.done();
        }
    }

    protected abstract void call(final DbxClient client, final ProgressMonitor pm) throws IOException, DbxException;

    private static final class NullProgressMonitor implements ProgressMonitor {

        static final ProgressMonitor INSTANCE = new NullProgressMonitor();

        @Override
        public void begin(final long totalWork) {
        }

        @Override
        public void worked(final long worked) {
        }

        @Override
        public void done() {
        }
    }

    private static abstract class TrackingProgressMonitor implements ProgressMonitor {
        private long startTime = -1;
        protected long timeTakenSoFar;
        protected long timeRemaining;
        protected long totalWork;
        protected long workedSoFar;
        protected long rightNow;

        @Override
        public final void begin(final long totalWork) {
            startTime = System.currentTimeMillis();
            markTime();
            this.totalWork = totalWork;
            start(totalWork);
        }

        @Override
        public final void worked(final long worked) {
            markTime();
            workedSoFar += worked;
            final long expectedTotalTime = timeTakenSoFar * totalWork / workedSoFar;
            timeRemaining = expectedTotalTime - timeTakenSoFar;
            work(worked);
        }

        @Override
        public final void done() {
            markTime();
            timeRemaining = 0;
            finish();
        }

        private void markTime() {
            rightNow = System.currentTimeMillis();
            timeTakenSoFar = rightNow - startTime;
        }

        protected abstract void start(final long totalWork);

        protected abstract void work(final long worked);

        protected abstract void finish();
    }

    private static final class ThrottlingProgressMonitor extends TrackingProgressMonitor {

        private static final long ONE_HOUR_INTERVAL = 13000;
        private static final long HALF_HOUR_INTERVAL = 8000;
        private static final long FIFTEEN_MINUTE_INTERVAL = 5000;
        private static final long SEVEN_MINUTE_INTERVAL = 3000;
        private static final long ONE_MINUTE_INTERVAL = 2000;
        private static final long LESS_THAN_A_MINUTE_INTERVAL = 1000;

        private static final long ONE_MINUTE = 1000 * 60;
        private static final long SEVEN_MINUTES = ONE_MINUTE * 7;
        private static final long FIFTEEN_MINUTES = ONE_MINUTE * 15;
        private static final long HALF_HOUR = FIFTEEN_MINUTES * 2;
        private static final long AN_HOUR = HALF_HOUR * 2;

        private final ProgressMonitor delegate;

        private long lastLogTime;
        private long logInterval = LESS_THAN_A_MINUTE_INTERVAL;

        private long workAccumulator;

        ThrottlingProgressMonitor(final ProgressMonitor delegate) {
            this.delegate = delegate;
        }

        @Override
        public void start(final long totalWork) {
            delegate.begin(totalWork);
            lastLogTime = rightNow;
        }

        @Override
        public void work(final long worked) {
            workAccumulator += worked;
            if (rightNow - lastLogTime >= logInterval) {
                delegate.worked(workAccumulator);
                workAccumulator = 0;
                lastLogTime = rightNow;
                logInterval = inferLogInterval();
            }
        }

        private long inferLogInterval() {
            if (timeRemaining > AN_HOUR) {
                return ONE_HOUR_INTERVAL;
            }
            if (timeRemaining > HALF_HOUR) {
                return HALF_HOUR_INTERVAL;
            }
            if (timeRemaining > FIFTEEN_MINUTES) {
                return FIFTEEN_MINUTE_INTERVAL;
            }
            if (timeRemaining > SEVEN_MINUTES) {
                return SEVEN_MINUTE_INTERVAL;
            }
            if (timeRemaining > ONE_MINUTE) {
                return ONE_MINUTE_INTERVAL;
            }
            return LESS_THAN_A_MINUTE_INTERVAL;
        }

        @Override
        public void finish() {
            delegate.done();
        }
    }

    private final class MavenLogProgressMonitor extends TrackingProgressMonitor {

        @Override
        public void start(final long totalWork) {
            getLog().info(apiMethod + ": starting");
        }

        @Override
        public void work(final long worked) {
            final long percent = workedSoFar * 100 / totalWork;
            final long[] hms = toHoursMinutesSeconds(timeRemaining);
            getLog().info(
                    String.format("%s: %d of %d [%d%%] [%dh:%dm:%ds left]", apiMethod, workedSoFar, totalWork, percent,
                            hms[0], hms[1], hms[2]));
        }

        @Override
        public void finish() {
            final long[] hms = toHoursMinutesSeconds(timeTakenSoFar);
            getLog().info(String.format("%s: finished in %dh:%dm:%ds", apiMethod, hms[0], hms[1], hms[2]));
        }

        private long[] toHoursMinutesSeconds(final long ms) {
            final long timeRemainingInS = ms / 1000;
            final long timeRemainingInM = timeRemainingInS / 60;
            final long timeRemainingInH = timeRemainingInM / 60;
            return new long[] { timeRemainingInH, timeRemainingInM % 60, timeRemainingInS % 60 };
        }
    }

    private static final class DropboxMojoExecutionException extends MojoExecutionException {
        private static final long serialVersionUID = 1L;

        public DropboxMojoExecutionException(final String apiMethod, final Exception cause) {
            super("Unable to complete the Dropbox " + apiMethod + " request.", cause);
        }
    }
}