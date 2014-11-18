package fileHandling;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.tools.cloudstorage.*;
import document.AbstractComment;
import log.PukkaLogger;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import pukkaBO.exceptions.BackOfficeException;

import javax.xml.bind.JAXBElement;
import java.io.*;
import java.math.BigInteger;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*******************************************************************************'
 *
 *              DocX file handler
 *
 */


public class DocXFile {

    WordprocessingMLPackage document = null;
    static ObjectFactory factory = Context.getWmlObjectFactory();
    private String fileName;


    public DocXFile(RepositoryFileHandler fileHandler) throws BackOfficeException{

        try {

            this.fileName = fileHandler.getFileName();

            RepositoryInterface repository = new BlobRepository();
            InputStream fileFromRepository = repository.getInputStream(fileHandler);

            document= WordprocessingMLPackage.load(fileFromRepository);

        } catch (IOException e) {

            throw new BackOfficeException(BackOfficeException.AccessError, "Could not find file " + this.fileName + " in document repository (" + fileHandler.toString() + ")");

        } catch (Docx4JException e) {

            throw new BackOfficeException(BackOfficeException.AccessError, "Could not find file " + this.fileName + " in document repository (" + fileHandler.toString() + ")");
        }

    }

  //TODO: Change name

    public RepositoryFileHandler saveToRepository(String fileName)throws BackOfficeException{


        try{
            GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());

            RepositoryInterface repository = new BlobRepository();

            GcsFilename gcsFile = new GcsFilename(repository.getBucket(), fileName);

            GcsOutputChannel outputChannel = gcsService.createOrReplace(gcsFile, GcsFileOptions.getDefaultInstance());

            //ObjectOutputStream oout = new ObjectOutputStream(Channels.newOutputStream(outputChannel));
            OutputStream output = Channels.newOutputStream(outputChannel);

            SaveToZipFile saver = new SaveToZipFile(document);
            saver.save(output);

            output.flush();
            output.close();

            outputChannel.close();

            return new RepositoryFileHandler(fileName);


        } catch (Docx4JException e) {

            throw new BackOfficeException(BackOfficeException.AccessError, "Could not save file " + fileName + " in document repository; " + e.getMessage());

        } catch (IOException e) {

            throw new BackOfficeException(BackOfficeException.AccessError, "Could not save file " + fileName + " in document repository; " + e.getMessage());
        }


    }


    public RepositoryFileHandler saveToBlobStore() throws BackOfficeException{


        try{


            FileService fileService = FileServiceFactory.getFileService();

            // Create a new Blob file with mime-type "text/plain"
            AppEngineFile file = fileService.createNewBlobFile("text/plain");

            // Open a channel to write to it
            boolean lock = true;
            FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);

            //OutputStream output = Channels.newOutputStream(writeChannel);
            document.save(new File("dummy"));

            //output.flush();
            //output.close();

            writeChannel.closeFinally();

            return new RepositoryFileHandler(fileName);

        } catch (/*Docx4J */Exception e) {

            throw new BackOfficeException(BackOfficeException.AccessError, "Could not save file " + fileName + " in document repository; " + e.getMessage());

        }


    }



    public void addClassificationComments(List<AbstractComment> commentsForDocument) {



        MainDocumentPart main = document.getMainDocumentPart();
        CommentsPart commentsPart = main.getCommentsPart();
        Comments comments;

        try {


            if(commentsPart == null){

                // If there are no comments in the document, we set up the comments part

                CommentsPart cp = new CommentsPart();
                document.getMainDocumentPart().addTargetPart(cp);

                // Part must have minimal contents
                comments = factory.createComments();
                cp.setJaxbElement(comments);

            }
            else{

                comments = commentsPart.getJaxbElement();

            }

        } catch (InvalidFormatException e) {

            PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not create comments for document");
            return;

        }



        int noComments = comments.getComment().size();

        List<Object> paragraphs = getAllElementFromObject(document.getMainDocumentPart(), P.class);

        int elementCount = 0;
        BigInteger commentId = BigInteger.valueOf(noComments);  // Start numbering from the number of existing comments

       	for (Object p : paragraphs) {

               P paragraph = (P)p;

               List<Object> runs = getAllElementFromObject(paragraph, R.class);

               for(Object r : runs){

                   R run = (R)r;
                   System.out.println("Run");

                   List<Object> texts = getAllElementFromObject(run, Text.class);

                   for(Object t : texts){

                       Text text = (Text)t;

                       System.out.println(" - Text: " + text.getValue());

                   }

               }




               // Find the right paragraph for the next classification in the list
               // TODO: This could be optimized by sorting the classifications

               for(AbstractComment comment : commentsForDocument){

                   if(comment.getFragmentId() == elementCount){

                       // Create a comment

                       Comments.Comment theComment = createComment(commentId, "Author", null, comment.getComment() + ": " + comment.getAnchor());
              		   comments.getComment().add(theComment);

                       R newRun = createRun(paragraph, comment.getAnchor());

                       Text text = new Text();
                       text.setValue("    " + comment.getType());

              		   createCommentReference(text, newRun, commentId);
                       paragraph.getContent().add(newRun);

                       // ++, for next comment ...
                       commentId = commentId.add(BigInteger.ONE);

                   }
               }


               elementCount++;
        }


    }

    private R createRun(P paragraph, String anchor) {

        R run = factory.createR();
        return run;
    }


    private static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {

    	  List<Object> result = new ArrayList<Object>();

    	  if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();

    	  if (obj.getClass().equals(toSearch))
    	   result.add(obj);
    	  else if (obj instanceof ContentAccessor) {
    	   List<?> children = ((ContentAccessor) obj).getContent();
    	   for (Object child : children) {
    	    result.addAll(getAllElementFromObject(child, toSearch));
    	   }

    	  }
    	  return result;
    	 }


	    private static Comments.Comment createComment(BigInteger commentId,
    	    		String author, Calendar date, String message) {

			Comments.Comment comment = factory.createCommentsComment();

			comment.setId( commentId );
			if (author!=null) {

				comment.setAuthor(author);
			}

			P commentP = factory.createP();
			comment.getEGBlockLevelElts().add(commentP);
			R commentR = factory.createR();
			commentP.getContent().add(commentR);
			Text commentText = factory.createText();
			commentR.getContent().add(commentText);
			commentText.setValue(message);
	    	return comment;
	    }


    private static R  createCommentReference(Text text, R run, BigInteger commentId) {

        System.out.println("Creating a reference with text " + text.getValue());

        R.CommentReference commentRef = factory.createRCommentReference();
        CommentRangeStart rangeStart = factory.createCommentRangeStart();
        rangeStart.setId( commentId );
        CommentRangeEnd rangeEnd = factory.createCommentRangeEnd();
        rangeEnd.setId( commentId );
        commentRef.setId( commentId );

        //run = factory.createR();
        //run.getContent().remove(0);        //TODO: Handle comments to existing text
        run.getContent().add(rangeStart);
        run.getContent().add(text);
        run.getContent().add(rangeEnd);
        run.getContent().add(commentRef);


        return run;
    }


    public String toString()  {

        ByteArrayOutputStream byos = new ByteArrayOutputStream();

        try {

            document.save(new File("dummy"));

            return new String(byos.toByteArray(), "ISO-8859-1");

        } catch (Docx4JException e) {

            PukkaLogger.log(PukkaLogger.Level.FATAL, "Failed to save file");
            e.printStackTrace();

        } catch (Exception e) {

            PukkaLogger.log(PukkaLogger.Level.FATAL, "Failed to save file. No encoding");
            e.printStackTrace();
        }

        return "no file";

    }

    public String getFileName() {
        return fileName;
    }
}
