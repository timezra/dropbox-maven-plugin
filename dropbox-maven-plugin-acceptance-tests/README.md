dropbox-maven-plugin
====================================================

This project contains acceptance tests for the dropbox-maven-plugin.

Configuration
----------------------------------------------------
Since the acceptance tests interact directly with Dropbox, you will need to setup an application folder there, and you will need to request an access token for this application folder so that the tests can write to and read from this directory.

You can do this by creating a new app (https://www.dropbox.com/developers/apps) that can take files and datastores. To get your access_token, you can browse to https://www.dropbox.com/1/oauth2/authorize?client_id=APP_KEY&response_type=code where the APP\_KEY is the App key from your dropbox application. Once you are redirected and allow yourself access to this application, you should see an access code.

Then (very quickly), from a commandline, request an ACCESS\_TOKEN using this command, where the ACCESS\_CODE is the access code you just received, the APP\_KEY is the App key from your dropbox application and the APP\_SECRET is the App secret from your dropbox application: 
    
    $ curl --request POST 'https://www.dropbox.com/1/oauth2/token' --data 'code=ACCESS_CODE&grant_type=authorization_code&client_id=APP_KEY&client_secret=APP_SECRET'

In order to run these tests, you must create a file called config/dev.properties. This file contains the following 2 entries:

    client_identifier=APPLICATION_IDENTIFIER
    access_token=ACCESS_TOKEN

Where the APPLICATION\_IDENTIFIER looks like "YOUR DROPBOX APPLICATION NAME/1.0" and the ACCESS\_TOKEN is the ACCESS\_TOKEN you requested above.
