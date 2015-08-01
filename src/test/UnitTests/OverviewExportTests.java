package test.UnitTests;

import dataRepresentation.DataObjectInterface;
import databaseLayer.DBKeyInterface;
import diff.DiffStructure;
import diff.FragmentComparator;
import log.PukkaLogger;
import org.junit.Test;
import overviewExport.CellValue;
import overviewExport.Extraction;
import overviewExport.ExtractionTagList;
import pukkaBO.exceptions.BackOfficeException;
import test.PukkaTest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 *          Simple tests for the overview export functionality
 */


public class OverviewExportTests extends PukkaTest{

    /******************************************************************
     *
     *
     *          Tests for cell value.
     *
     *          TODO: Improvement Stability: Add tests of the actual rendering.
     */



    @Test
    public void testCellValue(){

        try{

            CellValue cell1 = new CellValue( "test" );
            CellValue cell2 = new CellValue( 4711 );

            assertVerbose(" Getting value 1", cell1.getStringValue(), is("test"));
            assertVerbose(" Getting value 2", cell2.getStringValue(), is(""));
            assertVerbose(" Getting value 2", cell2.getValue(), is( 4711 ));

            cell1.asBox();
            cell1.asRow();

            cell2.bold();
            cell2.noWrap();
            cell2.italics();
            cell2.tableHeadline();
            cell2.withFont(12);


        }catch(Exception e){

            e.printStackTrace(System.out);
            assertTrue(false);
        }


    }

    @Test
    public void testExtraction(){

        try{

            Extraction extraction = new Extraction("Name", "classification", "text",  "fragmentKey", (long)1, 2,
                    "style", (DBKeyInterface) null, (DBKeyInterface)null, "risk", "description", "comment", "sheet", (DBKeyInterface)null );


            extraction.asHeadline( 0 );
            assertVerbose("Expocting style Heading", extraction.getStyle(), is("Heading"));

            extraction.asTitle();
            assertVerbose("Expocting style Title", extraction.getStyle(), is("Title"));

        }catch(Exception e){

            e.printStackTrace(System.out);
            assertTrue(false);
        }


    }

    @Test
    public void testExtractionTagList(){

        try{

            List<String> children = new ArrayList<String>();
            children.add("#Child1");
            children.add("#Child2");

            ExtractionTagList tagList = new ExtractionTagList("#Main");
            tagList.setChildren(children);


            assertVerbose("Get Main Tag  ", tagList.getMainTag(), is("#Main"));
            assertVerbose("Is  Applicable main", tagList.isApplicableFor("#Main"),   is(true));
            assertVerbose("Is  Applicable child", tagList.isApplicableFor("#Child1"), is(true));
            assertVerbose("NOT Applicable other", tagList.isApplicableFor("#Cousin"), is(false));

        }catch(Exception e){

            e.printStackTrace(System.out);
            assertTrue(false);
        }


    }

}