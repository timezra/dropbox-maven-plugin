dropbox-maven-plugin
====================================================

This project provides a bridge between maven and the dropbox-java-sdk (https://github.com/timezra/dropbox-java-sdk).

Configuration
----------------------------------------------------
In order to use this Maven plugin, you will need to configure your pom.xml (or proxy repository) to point to the repository at http://timezra.github.com/maven/snapshots

<code lang="xml">
    &lt;pluginRepositories&gt;

        &lt;pluginRepository&gt;

            &lt;id&gt;tims-repo&lt;/id&gt;

            &lt;url&gt;http://timezra.github.com/maven/releases&lt;/url&gt;

            &lt;releases&gt;

                &lt;enabled&gt;true&lt;/enabled&gt;

            &lt;/releases&gt;

            &lt;snapshots&gt;

                &lt;enabled&gt;false&lt;/enabled&gt;

            &lt;/snapshots&gt;

        &lt;/pluginRepository&gt;

    &lt;/pluginRepositories&gt;
</code>

Usage
----------------------------------------------------
Work in progress....

For now, please use 'mvn timezra.maven:dropbox-maven-plugin:1.5.3-SNAPSHOT:help' to find out usage information.


