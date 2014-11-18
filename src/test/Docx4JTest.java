package test;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import org.junit.Test;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2014-09-01
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 */
public class Docx4JTest {


    @Test
    public void test(){

        try {

            WordprocessingMLPackage document = WordprocessingMLPackage.createPackage();

            document = WordprocessingMLPackage.load(new java.io.File("Test Document4.docx"));

            MainDocumentPart main = document.getMainDocumentPart();
            CommentsPart commentsPart = main.getCommentsPart();
            Comments comments = commentsPart.getJaxbElement();

            List<Object> paragraphs = getAllElementFromObject(document.getMainDocumentPart(), P.class);

            int runCount = 0;

           	 for (Object p : paragraphs) {

                    P paragraph = (P)p;

                    List<Object> runs = getAllElementFromObject(p, R.class);

                    for (Object r : runs) {

                        runCount++;
                        R run = (R) r;

                        List<Object> texts = getAllElementFromObject(r, Text.class);
                        for(Object t : texts){

                            Text text = (Text)t;

                            System.out.println("Text: \"" + text.getValue() + "\"");

                             if(runCount == 2){

                                 System.out.println("Creating comment:");

                                 java.math.BigInteger commentId = BigInteger.valueOf(2);

                         		 Comments.Comment theComment = createComment(commentId, "LD", null, "my first comment");
                        		 comments.getComment().add(theComment);

                        		 createCommentReference(text, run, commentId);
                                 //paragraph.getContent().add(run);

                             }

                     }

           	   }
           	 }



            document.save(new java.io.File("output.docx"));

        } catch (Docx4JException e) {

            e.printStackTrace();
        }

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

    static org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();

    	    private static org.docx4j.wml.Comments.Comment createComment(java.math.BigInteger commentId,
        	    		String author, Calendar date, String message) {

    			org.docx4j.wml.Comments.Comment comment = factory.createCommentsComment();

    			comment.setId( commentId );
    			if (author!=null) {

    				comment.setAuthor(author);
    			}

    			org.docx4j.wml.P commentP = factory.createP();
    			comment.getEGBlockLevelElts().add(commentP);
    			org.docx4j.wml.R commentR = factory.createR();
    			commentP.getContent().add(commentR);
    			org.docx4j.wml.Text commentText = factory.createText();
    			commentR.getContent().add(commentText);
    			commentText.setValue(message);
    	    	return comment;
    	    }

        private static org.docx4j.wml.R  createRunCommentReference(java.math.BigInteger commentId) {

            org.docx4j.wml.R run = factory.createR();
            org.docx4j.wml.R.CommentReference commentRef = factory.createRCommentReference();
            run.getContent().add(commentRef);
            commentRef.setId(commentId);

            return run;
        }


    private static org.docx4j.wml.R  createCommentReference(Text text, org.docx4j.wml.R run, java.math.BigInteger commentId) {

        System.out.println("Creating a reference with text " + text.getValue());

        org.docx4j.wml.R.CommentReference commentRef = factory.createRCommentReference();
        CommentRangeStart rangeStart = factory.createCommentRangeStart();
        rangeStart.setId( commentId );
        CommentRangeEnd rangeEnd = factory.createCommentRangeEnd();
        rangeEnd.setId( commentId );
        commentRef.setId( commentId );

        //run = factory.createR();
        run.getContent().remove(0);
        run.getContent().add(rangeStart);
        run.getContent().add(text);
        run.getContent().add(rangeEnd);
        run.getContent().add(commentRef);

        return run;
    }



    @Test
    public void helloWorld(){

        try {

            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            wordMLPackage.getMainDocumentPart().addParagraphOfText("Hello Word!");
            wordMLPackage.save(new java.io.File("HelloWord1.docx"));

        } catch (Docx4JException e) {

            e.printStackTrace();
        }

    }

}
