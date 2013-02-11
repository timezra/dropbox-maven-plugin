dropbox-maven-plugin
====================================================

This project provides a bridge between maven and the dropbox-java-sdk (https://github.com/timezra/dropbox-java-sdk).

Configuration
----------------------------------------------------
In order to use this Maven plugin, you will need to configure your pom.xml (or proxy repository) to point to the repository at http://timezra.github.com/maven/snapshots

<code lang="xml">
&nbsp;&nbsp;&nbsp;&nbsp;&lt;pluginRepositories&gt;  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;pluginRepository&gt;  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;id&gt;tims-repo&lt;/id&gt;  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;url&gt;http://timezra.github.com/maven/releases&lt;/url&gt;  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;releases&gt;  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;enabled&gt;true&lt;/enabled&gt;  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/releases&gt;  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;snapshots&gt;  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;enabled&gt;false&lt;/enabled&gt;  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/snapshots&gt;  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/pluginRepository&gt;  
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/pluginRepositories&gt;
</code>

Usage
----------------------------------------------------
Work in progress....

For now, please use 'mvn timezra.maven:dropbox-maven-plugin:1.5.3-SNAPSHOT:help' to find out usage information.


