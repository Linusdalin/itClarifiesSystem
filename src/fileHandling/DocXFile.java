package fileHandling;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.tools.cloudstorage.*;
import contractManagement.ContractFragment;
import contractManagement.ContractFragmentTable;
import contractManagement.ContractVersionInstance;
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
import pukkaBO.condition.LookupList;
import pukkaBO.condition.Ordering;
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
 *              Using docx4J to create a file
 *
 */


public class DocXFile {

    WordprocessingMLPackage document = null;
    static ObjectFactory factory = Context.getWmlObjectFactory();  // Factory to create objects
    private String fileName;


    /*****************************************************************
     *
     *          Create a docxFile internally given a file handler to
     *          a file in storage (disk, cloud...)
     *
     *
     * @param fileHandler
     * @throws BackOfficeException
     */


    public DocXFile(RepositoryFileHandler fileHandler) throws BackOfficeException{



        try {

            this.fileName = fileHandler.getFileName();
            PukkaLogger.log(PukkaLogger.Level.DEBUG, "*** Reading file " + fileName + " from repository");

            RepositoryInterface repository = new BlobRepository();
            InputStream fileFromRepository = repository.getInputStream(fileHandler);

            document= WordprocessingMLPackage.load(fileFromRepository);

        } catch (IOException e) {

            throw new BackOfficeException(BackOfficeException.AccessError, "Could not find file " + this.fileName + " in document repository (" + fileHandler.toString() + ")");

        } catch (Docx4JException e) {

            throw new BackOfficeException(BackOfficeException.AccessError, "Could not find file " + this.fileName + " in document repository (" + fileHandler.toString() + ")");
        }

    }

    /******************************************************************************'
     *
     *          Store a file to cloud storage
     *
     *
     * @param fileName
     * @return
     * @throws BackOfficeException
     */



    public RepositoryFileHandler saveToRepository(String fileName)throws BackOfficeException{

        PukkaLogger.log(PukkaLogger.Level.DEBUG, " *** Saving file " + fileName + " to repository");

        try{
            GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());

            RepositoryInterface repository = new BlobRepository();

            GcsFilename gcsFile = new GcsFilename(repository.getBucket(), fileName);

            GcsOutputChannel outputChannel = gcsService.createOrReplace(gcsFile, GcsFileOptions.getDefaultInstance());

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

    /*************************************************************************************************************
     *
     *
     *              Annotate the original file with attributes as comments.
     *
     *              The update is done as a sideeffect to "document"
     *
     *
     *
     *
     * @param commentsForDocument    - comments compiled from risks, annotations, definitions and classifications
     * @param version                - the document
     */


    public void annotateFile(List<AbstractComment> commentsForDocument, ContractVersionInstance version) {


        try {

            List<ContractFragment> fragmentsForDocument = version.getFragmentsForVersion(new LookupList().addOrdering(ContractFragmentTable.Columns.Ordinal.name(), Ordering.FIRST));

            addCommentToMain(document, commentsForDocument, fragmentsForDocument);


        } catch (InvalidFormatException e) {

            PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not create comments for document");

        } catch (Exception e) {

            PukkaLogger.log( e );

        }



    }

    /*****************************************************************************
     *
     *          This is the main traversing function to modify the document
     *
     *
     *
     *
     * @param document                  - xml document structure that will be modified
     * @param commentsForDocument       - comments
     * @param fragments                 - fragments (for matching
     * @return
     * @throws InvalidFormatException
     *
     *              The traversing of the file has to match the original parsing to be able to
     *              connect the comments with the right paragraph. There are also some paragraphs
     *              skipped in the analysis. It is essential that this is mirrored here or the
     *              comments will end up on the wrong line
     *
     *
     */


    private void addCommentToMain(WordprocessingMLPackage document, List<AbstractComment> commentsForDocument, List<ContractFragment> fragments) throws InvalidFormatException{

        Comments comments = getCommentsPartForDocument(document);
        List<Object> paragraphs = getAllElementFromObject(document.getMainDocumentPart(), P.class);

        PukkaLogger.log(PukkaLogger.Level.DEBUG, "***** Found " + paragraphs.size() + " paragraphs!");

        int paragraphNo = 0;  // The paragraph counter, used to match the comment paragraphs

        for (Object o : paragraphs) {

            P paragraph = (P)o;

            // There are implicit paragraphs generated in the parsing. These are not present in the
            // document so we need to increment the counter


            while(paragraphNo < fragments.size() && fragments.get(paragraphNo).getType().equalsIgnoreCase("IMPLICIT")){
                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring implicit fragment " + paragraphNo);
                paragraphNo++;
            }


            List<AbstractComment> commentsForParagraph = getCommentsForParagraph(paragraphNo, commentsForDocument);

            // Get all elements. We are interested in the runs and the comment ranges

            List<Object> paragraphObjects = getAllElementForParagraph(paragraph);

            // Paragraphs without any content are not generated in the parsing, so we should ignore them here.

            if(paragraphObjects.size() > 0){

                BigInteger commentId = BigInteger.valueOf(comments.getComment().size());  // Start numbering from the number of existing comments

                for (AbstractComment abstractComment : commentsForParagraph) {

                    Comments.Comment theComment = createComment(commentId, "Author", null, abstractComment.getComment() + ": " + abstractComment.getAnchor());
           		    comments.getComment().add(theComment);

                    System.out.println(" ! Replacing "+ abstractComment.getAnchor()+"("+abstractComment.getStart() + ", " + abstractComment.getLength() +") for paragraph " + paragraphNo);

                    if(abstractComment.getStart() == -1){

                        R newRun = factory.createR();
                        Text text = new Text();
                        text.setValue( abstractComment.getType());
                        createCommentReference(text, newRun, commentId);

                        paragraph.getContent().add(newRun);
                    }
                    else{

                        // We have a position. Clear the paragraph and add the runs
                        // again now modified to contain the comment reference

                        paragraph.getContent().clear();

                        int textPosition = 0;  // Counter for the entire paragraph text.

                        for (Object object : paragraphObjects) {

                            List<?> texts = getAllElementFromObject(object, Text.class);

                            if(texts.size() > 0){

                                Text t = (Text)texts.get(0);
                                System.out.println("   - Checking " + t.getValue() + " at position "+ textPosition + " to match pattern");

                                // This is a very simplified functionality that will use the entire run.
                                // An improvement would be to split the run into two here


                                if(startsInCurrentRun(textPosition, t.getValue().length(), abstractComment)){

                                    paragraph.getContent().add(createRangeStart(commentId));

                                }

                                paragraph.getContent().add(object);  // Add the actual object

                                if(endsInCurrentRun(textPosition, t.getValue().length(), abstractComment)){

                                    // If the comment ends the current run, we add a rangeend (and comment reference tag AFTER the run

                                    paragraph.getContent().add(createRangeEnd( commentId));
                                    paragraph.getContent().add(createCommentRef(commentId));


                                }


                                textPosition += t.getValue().length();
                            }
                            else{

                                System.out.println("   - Could not find any texts!!!");
                            }

                        }
                    }


                   // ++, for next comment ...
                   commentId = commentId.add(BigInteger.ONE);

                }

                paragraphNo++;
            }
            else
                PukkaLogger.log(PukkaLogger.Level.DEBUG, "Ignoring empty run");

        }


    }

    private CommentRangeEnd createRangeEnd(BigInteger commentId) {

        CommentRangeEnd rangeEnd = factory.createCommentRangeEnd();
        rangeEnd.setId( commentId );
        return rangeEnd;
    }

    private CommentRangeStart createRangeStart(BigInteger commentId) {

        CommentRangeStart rangeStart = factory.createCommentRangeStart();
        rangeStart.setId( commentId );

        return rangeStart;
    }

    private R.CommentReference createCommentRef(BigInteger commentId) {

        R.CommentReference commentRef = factory.createRCommentReference();
        commentRef.setId( commentId );

        return commentRef;
    }



    private boolean startsInCurrentRun(int textPos, int length, AbstractComment abstractComment) {

        boolean in = (abstractComment.getStart() >= textPos && abstractComment.getStart() < textPos + length);
        if(in)
            System.out.println("   - comment starts in run!");

        return in;
    }

    private boolean endsInCurrentRun(int textPos, int length, AbstractComment abstractComment) {

        int endPos = abstractComment.getStart() + abstractComment.getLength();
        boolean in = (endPos > textPos && endPos <= textPos + length);
        if(in)
            System.out.println("   - comment ends in run!");

        return in;
    }


    private List<AbstractComment> getCommentsForParagraph(int paragraphNo, List<AbstractComment> commentsForDocument) {

        List<AbstractComment> commentsForParagraph = new ArrayList<AbstractComment>();

        for (AbstractComment abstractComment : commentsForDocument) {

            if(abstractComment.getFragmentId() == paragraphNo){
                commentsForParagraph.add(abstractComment);
            }
        }

        return commentsForParagraph;

    }

    private Comments getCommentsPartForDocument(WordprocessingMLPackage document) throws InvalidFormatException {

        CommentsPart commentsPart = document.getMainDocumentPart().getCommentsPart();
        Comments comments;

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

        return comments;

    }


    /************************************************************************************'
     *
     *          Extracting relevant elements for the paragraph. This includes:
     *
     *          Run, CommentRangeStart and CommentRangeEnd
     *
     *
     * @param paragraph     - paragraph to look at
     * @return              - list of all the relevant objects
     */


    private static List<Object> getAllElementForParagraph(P paragraph) {

    	  List<Object> result = new ArrayList<Object>();
          List<?> children = paragraph.getContent();

        for(Object obj : children){

            if (obj instanceof JAXBElement)
                   obj = ((JAXBElement<?>) obj).getValue();

            if (obj.getClass().equals(R.class) || obj.getClass().equals(CommentRangeStart.class) || obj.getClass().equals(CommentRangeEnd.class) )
         	    result.add(obj);
             else{
                   System.out.println("Ignoring " + obj.getClass().getName());
               }

        }


        return result;

    }



    private static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {

    	  List<Object> result = new ArrayList<Object>();

    	  if (obj instanceof JAXBElement)
              obj = ((JAXBElement<?>) obj).getValue();

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


    /******************************************************************************''
     *
     *          Create the actual comment
     *
     *
     * @param commentId - new id in the document
     * @param author    - set the author. (Not working)
     * @param date      - not in use
     * @param message   - The comment body
     * @return          - The comment
     */


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

    /*********************************************************************************
     *
     *          Create the comment reference in the Paragraph
     *
     *
     * @param text
     * @param run
     * @param commentId
     * @return
     */


    private static R  createCommentReference(Text text, R run, BigInteger commentId) {

        System.out.println("Creating a reference with text " + text.getValue());

        R.CommentReference commentRef = factory.createRCommentReference();
        CommentRangeStart rangeStart = factory.createCommentRangeStart();
        rangeStart.setId( commentId );
        CommentRangeEnd rangeEnd = factory.createCommentRangeEnd();
        rangeEnd.setId( commentId );
        commentRef.setId( commentId );

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


}
