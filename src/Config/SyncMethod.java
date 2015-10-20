/*
 * This application is deisnged to Synchronize files between a given
 * directory and AWS S3 Bucket
 *
 * @author Archie Gunasekara
 * @date 2015.05.12
 * 
 */
 
package Config;

public class SyncMethod {
    
    public enum Methods {
        FROMS3,
        TOS3,
        BIDIRECTIONAL
    }
    
}
