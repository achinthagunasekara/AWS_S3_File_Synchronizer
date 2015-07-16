AWS S3 File Synchronizer
========================

Author: Achintha Gunasekara

Info
----
This application was written using JDK 8 and Netbeans 8.0.2
Please make sure to add the liberaries included in the /libs directory when you build the applicationR

How to Use
----------
Set your configuration in /config.properties file

directory=Directory_TO_SYNC
bucket=S3_BUCKET_NAME
s3CredentialsFile=FILE_WITH_S3 CREDENTIALS
previeMode=PREVIEW_MODE_SET_TRUE_AND_NO_CHANGES_WOULD_MAKE_TO_THE_FILE_SYSTEM
syncMethod=POSSIBLE_VALUES_ARE_FROMS3_TOS3_OR_BIDIRECTIONAL
deleteFiles=NO_FILES_WILL_BE_DELETED_ONLY_NEW_FILES_WILL_BE_ADDED