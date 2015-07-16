/*
 * This application is deisnged to Synchronize files between a given
 * directory and AWS S3 Bucket
 */
package s3filesynchronizer;

import java.io.IOException;
import Config.AppConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 *
 * @author Archie Gunasekara
 * @date 2015.05.12
 * 
 */
public class S3FileSynchronizer {
    
    public static Log log;

    /**
     * @param args the command line arguments
     * This application can be started using the CLI or GUI
     * Trigger the application in CLI mode when running using Cron or Windows Task Scheduler
     */
    public static void main(String[] args) {
        
        log = LogFactory.getLog(S3FileSynchronizer.class);
        
        //GetConfiguration
        try {
            
            AppConfig appConfig = new AppConfig();
            
            //Start Syncing
            try {
            
                Syncer syncer = new Syncer(appConfig);
                syncer.run();
            }
            catch(Exception ex) {
                
                System.out.println(ex.toString());
            }
        }
        catch(IOException ioEx) {
            
            System.out.println("Fatal Error - Unable to read the configuration file. " + ioEx.getMessage());
        }
        catch(Exception ex) {
            
            System.out.println("Fatal Error - Invalid Configuration. " + ex.getMessage());
        }
    }
    
}
