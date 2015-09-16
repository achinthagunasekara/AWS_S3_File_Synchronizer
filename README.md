AWS S3 File Synchronizer
========================

Author: Achintha Gunasekara

web: http://www.achinthagunasekara.com

Contact: contact@achinthagunasekara.com

Info
----
This application was written using JDK 8 and Netbeans 8.0.2

Please make sure to add the liberaries included in the /libs directory when you build the application.

Application will fail to compile, if any of these libraries are missing.

* aws-java-sdk-1.9.34.jar
* commons-logging-1.2.jar
* com.fasterxml.jackson.annotations.jar
* com.fasterxml.jackson.core.jar
* com.fasterxml.jackson.databind.jar
* commons-codec-1.9.jar
* commons-logging-1.2.jar
* fluent-hc-4.4.1.jar
* httpclient-4.4.1.jar
* httpclient-cache-4.4.1.jar
* httpclient-win-4.4.1.jar
* httpcore-4.4.1.jar
* httpmime-4.4.1.jar
* jna-4.1.0.jar
* jna-platform-4.1.0.jar
* joda-time-2.8.jar

These are the versions I've used when writing this application, but there may be later versions available. It's recommended to use the latest libraries, but changes to the code base may be required depending on the library changes.

How to Use
----------
Set your configuration in /config.properties file

```html
directory=Directory_TO_SYNC
bucket=S3_BUCKET_NAME
s3CredentialsFile=FILE_WITH_S3 CREDENTIALS
previeMode=PREVIEW_MODE_SET_TRUE_AND_NO_CHANGES_WOULD_MAKE_TO_THE_FILE_SYSTEM
syncMethod=POSSIBLE_VALUES_ARE_FROMS3_TOS3_OR_BIDIRECTIONAL
deleteFiles=NO_FILES_WILL_BE_DELETED_ONLY_NEW_FILES_WILL_BE_ADDED
```
