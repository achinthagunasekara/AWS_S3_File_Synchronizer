#AWS S3 File Synchronizer

Application to Sync files between a directory on your machine and AWS S3.

##Compiling the Application

This application was written using JDK 8 and Netbeans 8.0.2

Please make sure to add the liberaries included in the /libs directory when you build the application.

Application will fail to compile, if any of these libraries are missing.

| Library | More Information |
|---------|------------------|
| aws-java-sdk-1.9.34.jar | https://aws.amazon.com/sdk-for-java/ |
| commons-logging-1.2.jar | https://commons.apache.org/proper/commons-logging/download_logging.cgi |
| com.fasterxml.jackson.annotations.jar | http://wiki.fasterxml.com/JacksonHome |
| com.fasterxml.jackson.core.jar | http://wiki.fasterxml.com/JacksonHome |
| com.fasterxml.jackson.databind.jar | http://wiki.fasterxml.com/JacksonHome |
| commons-codec-1.9.jar | https://commons.apache.org/proper/commons-codec/download_codec.cgi |
| fluent-hc-4.4.1.jar | https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fluent.html |
| httpclient-4.4.1.jar | https://hc.apache.org/httpcomponents-client-ga/download.html |
| httpclient-cache-4.4.1.jar | https://hc.apache.org/httpcomponents-client-ga/download.html |
| httpclient-win-4.4.1.jar | https://hc.apache.org/httpcomponents-client-ga/download.html |
| httpcore-4.4.1.jar | https://hc.apache.org/downloads.cgi |
| httpmime-4.4.1.jar | https://hc.apache.org/downloads.cgi |
| jna-4.1.0.jar | https://github.com/java-native-access/jna |
| jna-platform-4.1.0.jar | https://github.com/java-native-access/jna |
| joda-time-2.8.jar | http://www.joda.org/joda-time/ |

These are the versions I've used when writing this application, but there may be later versions available. It's recommended to use the latest libraries, but changes to the code base may be required depending on the library changes.

##How to Use

Set your configuration in /config.properties file. Please have a look below for more detailed explanation of the config file items.

```properties
directory=/tmp/Test
bucket=archie_test_bucket
s3CredentialsFile=/tmp/s3credentials.properties
previeMode=true
syncMethod=BIDIRECTIONAL
deleteFiles=true
```

###Configuration File Explanied

| Configuration Item | Details |
|-------------------|---------|
| directory | Directory to Sync with S3 bucket |
| bucket | Name of the S3 bucket |
| s3CredentialsFile | Full path and the name of S3 credentials file |
| previeMode | Valid vaules are TRUE or FALSE. If set to TRUE no files will be synced. Console output will be printed |
| syncMethod | Valid values are FROMS3, TOS3 or BIDIRECTIONAL. This will tell which way to sync files |
| deleteFiles | Valid vaules are TRUE or FALSE. If set to TRUE, files will only be added, but not deleted |

You'll also need a file as specified under "s3CredentialsFile" to store your AWS access key and the secret key as below.

```properties
accessKey=ACCESS_KEY
secretKey=SECRET_KEY
```

You are able to run this script via the command line or as a scheduled task (Cron/Windows Task Schedular)
