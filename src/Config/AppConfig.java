/*
 * This application is deisnged to Synchronize files between a given
 * directory and AWS S3 Bucket
 *
 * @author Archie Gunasekara
 * @date 2015.05.12
 * 
 */

package Config;

import java.io.IOException;

public class AppConfig {
    
    private final String directory;
    private final String bucket;
    private final String s3CredentialsFile;
    private final boolean previeMode;
    private SyncMethod.Methods syncMethod;
    private final boolean deleteFiles;
    
    public AppConfig() throws IOException, Exception {
        
        ConfigFileReader config = ConfigFileReader.getConfigFileReaderInstance();
        
        try {
        
            this.directory = config.getPropertyFor("directory");
            this.bucket = config.getPropertyFor("bucket");
            this.s3CredentialsFile = config.getPropertyFor("s3CredentialsFile");
            this.previeMode = Boolean.parseBoolean(config.getPropertyFor("previeMode"));
            this.syncMethod = SyncMethod.Methods.valueOf(config.getPropertyFor("syncMethod"));
            this.deleteFiles = Boolean.parseBoolean(config.getPropertyFor("deleteFiles"));
        }
        catch(Exception ex) {
            
            throw new Exception("Please Check the Configuration File. " + ex.getMessage());
        }
    }
    
    public String getDirectory() {
        
        return this.directory;
    }
    
    public String getBucket() {
        
        return this.bucket;
    }
    
    public String getS3CredentialsFile() {
        
        return this.s3CredentialsFile;
    }
    
    public boolean isPreviewMode() {
        
        return this.previeMode;
    }
    
    public SyncMethod.Methods getSyncMethod() {
        
        return this.syncMethod;
    }
    
    public boolean doDeleteFiles() {
        
        return this.deleteFiles;
    }
}
