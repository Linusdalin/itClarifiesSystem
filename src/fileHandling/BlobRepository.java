package fileHandling;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.*;
import com.google.appengine.tools.cloudstorage.*;
import com.google.common.io.ByteStreams;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.Map;

/******************************************************************'''
 *
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2014-09-03
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */

public class BlobRepository implements RepositoryInterface {

    private static final String EMPTY = "_EMPTY";

    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private FileService fileService = FileServiceFactory.getFileService();

    /**
     * This is where backoff parameters are configured. Here it is aggressively retrying with
     * backoff, up to 10 times but taking no more that 15 seconds total to do so.
     */
    private GcsService gcsService;


    public BlobRepository(){

        gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
                .initialRetryDelayMillis(10)
                .retryMaxAttempts(10)
                .totalRetryPeriodMillis(15000)
                .build());

    }

    /******************************************************************************'
     *
     *          Handle the actual file data storage on the server
     *
     *
     * @param req - the HTTP request
     */

    public RepositoryFileHandler getFileHandler(String fileName, HttpServletRequest req) {

        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        BlobKey blobKey = blobs.get(fileName);

        return new RepositoryFileHandler(fileName);


    }

    public void serveFile(RepositoryFileHandler fileHandler, HttpServletResponse res) throws IOException {

        GcsFilename gcsFile = new GcsFilename(getBucket(), fileHandler.getFileName());

        BlobKey blobKey = blobstoreService.createGsBlobKey(
            "/gs/" + gcsFile.getBucketName() + "/" + gcsFile.getObjectName());

        blobstoreService.serve(blobKey, res);

    }

    public boolean existsFile(RepositoryFileHandler fileHandler) {

        if(fileHandler.toString() == null)
            return false;

        return true;
    }


    public RepositoryFileHandler getEmptyFileHandler() {

        return new RepositoryFileHandler(EMPTY);
    }



    public InputStream getInputStream(RepositoryFileHandler fileHandler) throws IOException {


        GcsFilename gcsFile = new GcsFilename(getBucket(), fileHandler.getFileName());

        GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(gcsFile, 0, 1024 * 1024);

        InputStream input = Channels.newInputStream(readChannel);

        return input;



    }


    public OutputStream getOutputStream(RepositoryFileHandler fileHandler) throws IOException {

        GcsFilename gcsFile = new GcsFilename(getBucket(), fileHandler.getFileName());

        GcsOutputChannel outputChannel = gcsService.createOrReplace(gcsFile, GcsFileOptions.getDefaultInstance());

        //ObjectOutputStream oout = new ObjectOutputStream(Channels.newOutputStream(outputChannel));
        OutputStream oout = Channels.newOutputStream(outputChannel);

        return oout;
    }


    /*

    @Override
    public OutputStream getOutputStream(RepositoryFileHandler fileHandler) throws IOException {

        AppEngineFile file = new AppEngineFile(fileHandler.toString());

        FileWriteChannel writeChannel = fileService.openWriteChannel(file, false);

        OutputStream output = Channels.newOutputStream(writeChannel);

        return output;


    }

    */


        /*
    @Override
    public InputStream getInputStream(RepositoryFileHandler fileHandler) throws IOException {

        System.out.println(" brk 1");
        AppEngineFile file = new AppEngineFile(fileHandler.toString());

        System.out.println(" brk 2");
        FileReadChannel readChannel = fileService.openReadChannel(file, false);

        System.out.println(" brk 3");
        InputStream input = Channels.newInputStream(readChannel);

        System.out.println(" brk 4");
        return input;


    }
*/


    public RepositoryFileHandler saveFile(String fileName, InputStream uploaded) throws BackOfficeException {

        try{
            GcsFilename gcsFile = new GcsFilename(getBucket(), fileName);

            GcsOutputChannel outputChannel = gcsService.createOrReplace(gcsFile, GcsFileOptions.getDefaultInstance());


            OutputStream output = Channels.newOutputStream(outputChannel);
            ByteStreams.copy(uploaded, output);

            output.flush();
            output.close();

            outputChannel.close();

            PukkaLogger.log(PukkaLogger.Level.ACTION, "Stored file " + fileName);
            return new RepositoryFileHandler(fileName);  // Store the file path

        } catch (IOException e) {

            throw new BackOfficeException(BackOfficeException.AccessError, "Could not save file " + fileName + " in document repository; " + e.getMessage());

        } catch (NonRetriableException e) {

            throw new BackOfficeException(BackOfficeException.AccessError, "File is empty; " + e.getMessage());
        }

    }

    /*

    public RepositoryFileHandler saveFile(String fileName, InputStream uploaded) throws BackOfficeException {


        try{

            // Create a new Blob file with mime-type "text/plain"
            AppEngineFile file = fileService.createNewBlobFile("text/plain");

            // Open a channel to write to it
            boolean lock = true;
            FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);

            OutputStream output = Channels.newOutputStream(writeChannel);
            ByteStreams.copy(uploaded, output);

            output.flush();
            output.close();

            writeChannel.closeFinally();

            return new RepositoryFileHandler(fileName);  // Store the file path

        } catch (IOException e) {

            throw new BackOfficeException(BackOfficeException.AccessError, "Could not save file " + fileName + " in document repository; " + e.getMessage());
        }


    }

      */

    public String getBucket(){

        AppIdentityService appIdentityService = AppIdentityServiceFactory.getAppIdentityService();
        String defaultBucketName = appIdentityService.getDefaultGcsBucketName();

        return defaultBucketName;
    }

}
