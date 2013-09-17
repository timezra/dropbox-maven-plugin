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
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;url&gt;http://timezra.github.com/maven/releases &lt;/url&gt;  
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

For now, please use 'mvn timezra.maven:dropbox-maven-plugin:1.7.4:help' to find out usage information.

For the most part, available operations and their parameters follow the core REST API at <https://www.dropbox.com/developers/core/docs>

### Examples: ###

    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:files_put -Dverbose=true -Dpath=/{A FILE} -Dfile={A FILE} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:files -Dverbose=true -Dpath=/{A FILE} -Dfile={A FILE} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:metadata -Dverbose=true -Dpath=/ -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:delta -Dverbose=true -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:delta -Dverbose=true -Dcursor={A CURSOR} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:revisions -Dverbose=true -Dpath=/{A FILE} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:restore -Dverbose=true -Dpath=/{A FILE} -Drev={A REVISION} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:search -Dverbose=true -Dpath=/ -Dquery={A QUERY} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:shares -Dverbose=true -Dpath=/{A FILE} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:media -Dverbose=true -Dpath=/{A FILE} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:copy_ref -Dverbose=true -Dpath=/{A FILE} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:thumbnails -Dverbose=true -Dpath=/{AN IMAGE FILE} -Dformat={PNG OR JPEG} -Dfile={AN IMAGE FILE} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:chunked_upload -Dverbose=true -Dfile={A FILE} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:chunked_upload -Dverbose=true -Dfile={A FILE} -Dupload_id={AN UPLOAD ID} -Doffset={A CHUNK SIZE} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:commit_chunked_upload -Dverbose=true -Dpath=/{A FILE} -Dupload_id={AN UPLOAD ID} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:copy -Dverbose=true -Dfrom_path=/{A FILE} -Dto_path=/{ANOTHER FILE} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:copy -Dverbose=true -Dfrom_copy_ref={A COPY REF} -Dto_path=/{ANOTHER FILE} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:create_folder -Dverbose=true -Dpath=/{A FOLDER} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:delete -Dverbose=true -Dpath=/{A FOLDER} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:move -Dverbose=true -Dfrom_path=/{A FILE} -Dto_path=/{ANOTHER FILE} -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
    $ mvn timezra.maven:dropbox-maven-plugin:1.7.4:info -Dverbose=true -DclientIdentifier={MY APP} -DaccessToken={MY ACCESS TOKEN} -e
