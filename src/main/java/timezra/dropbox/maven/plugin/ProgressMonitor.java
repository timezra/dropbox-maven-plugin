package timezra.dropbox.maven.plugin;

public interface ProgressMonitor {

    void begin(long totalWork);

    void worked(long worked);

    void done();

}