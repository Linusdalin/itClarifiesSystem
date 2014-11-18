package test;

import fileHandling.BlobRepository;
import fileHandling.DocXFile;
import fileHandling.RepositoryFileHandler;
import fileHandling.RepositoryInterface;
import org.junit.Test;
import pukkaBO.exceptions.BackOfficeException;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2014-09-02
 * Time: 18:11
 * To change this template use File | Settings | File Templates.
 */
public class DocXFileTester {

    @Test
    public void basicTest(){

        try {


            DocXFile file = new DocXFile(new RepositoryFileHandler("Test document.docx"));
            //file.saveToDisk("Testcase output.docx");

        } catch (BackOfficeException e) {


            e.printStackTrace();
            assertTrue(false);
        }

    }

    @Test
    public void streamTest(){

        try {

            PrintWriter pw = new PrintWriter("test.txt");

            DocXFile file = new DocXFile(new RepositoryFileHandler("Test document.docx"));
            String text = file.toString();
            System.out.println("text:" + text);


        } catch (FileNotFoundException e) {


            e.printStackTrace();
            assertTrue(false);

        } catch (BackOfficeException e) {


            e.printStackTrace();
            assertTrue(false);
        }

    }



}
