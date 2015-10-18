/*
 * This application is deisnged to Synchronize files between a given
 * directory and AWS S3 Bucket
 *
 * @author Archie Gunasekara
 * @date 2015.05.12
 * 
 */
 
package s3filesynchronizer;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import java.io.FileOutputStream;
import Config.AppConfig;
import Config.SyncMethod;
import java.io.FileWriter;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Syncer {
    
    private final AppConfig appConfig;
    public static Log log;
    
    public Syncer(AppConfig appConfig) {
        
        this.appConfig = appConfig;
    }
    
    public void run() throws FileNotFoundException, IllegalArgumentException, IOException {
                
        log = LogFactory.getLog(Syncer.class);
        AWSCredentials credentials = new PropertiesCredentials(new File(appConfig.getS3CredentialsFile()));
        
        log.info("Starting the Application...");
        
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);
        AmazonS3 client = new AmazonS3Client(credentials, clientConfig);        
        this.sync(client, appConfig.getBucket(), new File(appConfig.getDirectory()));
        log.info("Finished Syncing. Exiting the Application");	
    }
    
    private void sync(AmazonS3 client, String bucketName, File root) {
        
        System.out.println("Syncing " + root + " to " + bucketName);
        log.info("Getting local file list...");
        Map<String, File> fileMap = new HashMap<String, File>();
        Map<String, String> localFiles = getLocalFiles(root, fileMap);
        log.info("Loading object listing from Amazon S3...");
        Map<String, String> serverFiles = getServerFiles(client, bucketName);
        
        if(appConfig.getSyncMethod() == SyncMethod.Methods.BIDIRECTIONAL) {
        
            syncToS3(localFiles, serverFiles, client, bucketName, fileMap);
            syncFromS3(localFiles, serverFiles, client, bucketName);
        }
        else if(appConfig.getSyncMethod() == SyncMethod.Methods.TOS3) {
        
            syncToS3(localFiles, serverFiles, client, bucketName, fileMap);
            
            if(appConfig.doDeleteFiles()) {
                
                cleanS3(localFiles, serverFiles, client, bucketName);
            }
        }
        else if(appConfig.getSyncMethod() == SyncMethod.Methods.FROMS3) {
            
            syncFromS3(localFiles, serverFiles, client, bucketName);
            
            if(appConfig.doDeleteFiles()) {
                
                cleanLocal(localFiles, serverFiles);
            }
        }
    }
    
    private void syncToS3(Map<String, String> localFiles, Map<String, String> serverFiles, AmazonS3 client, String bucketName, Map<String, File> fileMap) {
        
        log.info("Syncing files TO S3...");
        
        //Sync to S3
        for(Map.Entry<String, String> entry : localFiles.entrySet()) {
            
            if(serverFiles.containsKey(entry.getKey()) && serverFiles.get(entry.getKey()).equals(entry.getValue())) {

                //File is up to date. Do nothing!
            }
            else {
                
                System.out.print((serverFiles.containsKey(entry.getKey()) ? "M" : "A") + " " + entry.getKey());
                
                if(!appConfig.isPreviewMode()) {
                    
                    //upload the file to S3
                    upload(client, bucketName, entry.getKey(), fileMap.get(entry.getKey()));
                }
            }
        }
    }
    
    private void syncFromS3(Map<String, String> localFiles, Map<String, String> serverFiles, AmazonS3 client, String bucketName) {
        
        log.info("Syncing files FROM S3...");
        
        //Sync from S3
        for(Map.Entry<String, String> entry : serverFiles.entrySet()) {

            if(localFiles.containsKey(entry.getKey()) && localFiles.get(entry.getKey()).equals(entry.getValue())) {
            
                //File is up to date. Do nothing!
            }
            else {
                
                System.out.print((localFiles.containsKey(entry.getKey()) ? "M" : "A") + " " + entry.getKey());
                
                if(!appConfig.isPreviewMode()) {
                    
                    try {

                        download(client, bucketName, entry.getValue(), entry.getKey());
                    }
                    catch(AmazonS3Exception aS3Ex) {
                        
                        System.out.println("S3 Error - " + aS3Ex.getMessage());
                    }
                    catch(IOException ioEx) {
                        
                        System.out.println("Error Downloading file - " + ioEx.getMessage());
                    }
                    catch(Exception ex) {
                        
                        System.out.println("General Error - " + ex.getMessage());
                    }
                }
            }
        }
    }
    
    private void upload(AmazonS3 client, String bucketName, String key, File file) {
        
        PutObjectRequest request = new PutObjectRequest(bucketName, key, file);
        ObjectMetadata metadata = new ObjectMetadata();
        
        //can add other file types in here
        if(file.getName().endsWith(".html")) {
            
            metadata.setContentType("text/html; charset=utf-8");
            System.out.print(" [html]");
        }
        
        request.setMetadata(metadata); 
        client.putObject(request);
    }
    
    //upload files to S3
    private void download(AmazonS3 client, String bucketName, String key, String fileName) throws IOException {
        
        Path p = Paths.get(appConfig.getDirectory() + fileName);
        String dir = p.getParent().toString();
        
        File file = new File(dir);
        file.mkdirs(); //for several levels, without the "s" for one level

        GetObjectRequest request = new GetObjectRequest(bucketName, fileName);
        S3Object object = client.getObject(request);
        S3ObjectInputStream objectContent = object.getObjectContent();
        IOUtils.copy(objectContent, new FileOutputStream(appConfig.getDirectory() + fileName));
    }
    
    //Remove files from S3
    private void cleanS3(Map<String, String> localFiles, Map<String, String> serverFiles, AmazonS3 client, String bucketName) {
        
        log.info("Oneway Syncing - Clening up files FROM S3...");
                
        for(Map.Entry<String, String> entry : serverFiles.entrySet()) {

            if(localFiles.containsKey(entry.getKey()) && localFiles.get(entry.getKey()).equals(entry.getValue())) {
            
                //File is up to date. Do nothing!
            }
            else {
                
                System.out.print("D" + " " + entry.getKey());
                
                if(!appConfig.isPreviewMode()) {
                    
                    client.deleteObject(bucketName, entry.getKey());
                }
            }
        }
    }
    
    private void cleanLocal(Map<String, String> localFiles, Map<String, String> serverFiles) {
        
        log.info("Oneway Syncing - Clening up files FROM Local...");

        //Clean local
        for(Map.Entry<String, String> entry : localFiles.entrySet()) {
            
            if(serverFiles.containsKey(entry.getKey()) && serverFiles.get(entry.getKey()).equals(entry.getValue())) {

                //File is up to date. Do nothing!
            }
            else {
                
                System.out.print("D" + " " + entry.getKey());
                
                if(!appConfig.isPreviewMode()) {
                    
                    Path path = Paths.get(appConfig.getDirectory() + entry.getKey());
                    
                    try {
                        
                        Files.delete(path);
                    }
                    catch (NoSuchFileException noSFEx) {
                        
                        System.out.println("Unable to delete. No such file or directory " + path.toString());
                    }
                    catch (DirectoryNotEmptyException dNEEx) {
                        
                        System.out.println("Unable to Delete. Not empty " + path.toString());
                    }
                    catch (IOException ioEx) {
                        
                        // File permission problems are caught here.
                        System.out.println(ioEx.getMessage());
                    }
                }
            }
        }
    }
    
    private Map<String, String> getServerFiles(AmazonS3 client, String bucketName) {
        
        ObjectListing listing;
        Map<String, String> files = new TreeMap<String, String>();
        
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
        
        do {
            
            listing = client.listObjects(listObjectsRequest);
            
            for (S3ObjectSummary summary : listing.getObjectSummaries()) {
                
                files.put(summary.getKey(), summary.getETag());
            }
        }
        while(listing.isTruncated());
        
        return files;
    }
    
    private Map<String, String> getLocalFiles(File root, Map<String, File> fileMap) {
        
        Map<String, String> files = new TreeMap<String, String>();
        getLocalFilesRecursive(root, files, fileMap, "");
        return files;
    }
    
    private void getLocalFilesRecursive(File dir, Map<String, String> list, Map<String, File> fileMap, String prefix) {
        
        for (File file : dir.listFiles()) {
            
            if (file.isFile()) {
                
                if (file.getName().equals("Thumbs.db")) continue;
                if (file.getName().startsWith(".")) continue;
                String key = prefix + file.getName();
                key = key.replaceFirst("\\.noex\\.[^\\.]+$", "");
                list.put(key, getMD5Checksum(file));
                fileMap.put(key, file);
            }
            else if(file.isDirectory()) {
                
                getLocalFilesRecursive(file, list, fileMap, prefix + file.getName() + "/");
            }
        }
    }

    /*
     * Reference
     * http://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
     */
    private byte[] createChecksum(File file) throws Exception {
        
        InputStream fis = new FileInputStream(file);
        
        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        
        do {
            
            numRead = fis.read(buffer);
            if(numRead > 0) {
                
                complete.update(buffer, 0, numRead);
            }
        }
        while(numRead != -1);
        
        fis.close();
        return complete.digest();
    }
    
    private String getMD5Checksum(File file) {
        
        byte[] b;
        
        try {
            
            b = createChecksum(file);
        } catch(Exception e) {
            
            return "";
        }
        
        String result = "";
        
        for (int i = 0; i < b.length; i++) {
            
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        
        return result;
    }
}
