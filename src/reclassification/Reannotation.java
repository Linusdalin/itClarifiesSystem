package reclassification;

import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
import project.*;
import versioning.*;
import actions.*;
import overviewExport.*;
import module.*;
import search.*;
import crossReference.*;
import reclassification.*;
import dataRepresentation.*;
import databaseLayer.DBKeyInterface;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.condition.*;
import pukkaBO.database.*;

import pukkaBO.acs.*;

/********************************************************
 *
 *    Reannotation - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Reannotation extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ReannotationTable();

    public Reannotation(){

        super();

        if(table == null)
            table = TABLE;
    }

    public Reannotation(String text, boolean add, String date, String project, String document, long fragmentno, String fragment, String pattern, long patternpos, String user, boolean closed){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(text);
           data[1] = new BoolData(add);
           data[2] = new DateData(date);
           data[3] = new StringData(project);
           data[4] = new StringData(document);
           data[5] = new IntData(fragmentno);
           data[6] = new BlobData(fragment);
           data[7] = new TextData(pattern);
           data[8] = new IntData(patternpos);
           data[9] = new StringData(user);
           data[10] = new BoolData(closed);

           exists = true;
        }catch(BackOfficeException e){
            PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not create object.");
            exists = false;
        }


    }
    /*********************************************************************''
     *
     *          Load from database
     *
     * @param condition - the SQL condition for selecting ONE UNIQUE object
     */

    public Reannotation(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Reannotation o = new Reannotation();
        o.data = data;
        o.exists = true;
        return o;
    }

    public String getText(){

        StringData data = (StringData) this.data[0];
        return data.getStringValue();
    }

    public void setText(String text){

        StringData data = (StringData) this.data[0];
        data.setStringValue(text);
    }



    public boolean getAdd(){

        BoolData data = (BoolData) this.data[1];
        return data.value;
    }

    public void setAdd(boolean add){

        BoolData data = (BoolData) this.data[1];
        data.value = add;
    }



    public DBTimeStamp getDate()throws BackOfficeException{

        DateData data = (DateData) this.data[2];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setDate(DBTimeStamp date){

        DateData data = (DateData) this.data[2];
        data.value = date.getISODate().toString();
    }



    public String getProject(){

        StringData data = (StringData) this.data[3];
        return data.getStringValue();
    }

    public void setProject(String project){

        StringData data = (StringData) this.data[3];
        data.setStringValue(project);
    }



    public String getDocument(){

        StringData data = (StringData) this.data[4];
        return data.getStringValue();
    }

    public void setDocument(String document){

        StringData data = (StringData) this.data[4];
        data.setStringValue(document);
    }



    public long getFragmentNo(){

        IntData data = (IntData) this.data[5];
        return data.value;
    }

    public void setFragmentNo(long fragmentno){

        IntData data = (IntData) this.data[5];
        data.value = fragmentno;
    }



    public String getFragment(){

        BlobData data = (BlobData) this.data[6];
        return data.getStringValue();
    }

    public void setFragment(String fragment){

        BlobData data = (BlobData) this.data[6];
        data.setStringValue(fragment);
    }



    public String getPattern(){

        TextData data = (TextData) this.data[7];
        return data.getStringValue();
    }

    public void setPattern(String pattern){

        TextData data = (TextData) this.data[7];
        data.setStringValue(pattern);
    }



    public long getPatternPos(){

        IntData data = (IntData) this.data[8];
        return data.value;
    }

    public void setPatternPos(long patternpos){

        IntData data = (IntData) this.data[8];
        data.value = patternpos;
    }



    public String getUser(){

        StringData data = (StringData) this.data[9];
        return data.getStringValue();
    }

    public void setUser(String user){

        StringData data = (StringData) this.data[9];
        data.setStringValue(user);
    }



    public boolean getClosed(){

        BoolData data = (BoolData) this.data[10];
        return data.value;
    }

    public void setClosed(boolean closed){

        BoolData data = (BoolData) this.data[10];
        data.value = closed;
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/





    public String getPostRequest() throws java.io.UnsupportedEncodingException {

        String fragmentText = getFragment();
        fragmentText = fragmentText.replaceAll("&", "").replaceAll("/", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\}", "").replaceAll("\\{", "").replaceAll("\\]", "").replaceAll("\\[", "");
        String commentText = getText();
        commentText = commentText.replaceAll("&", "").replaceAll("/", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\}", "").replaceAll("\\{", "").replaceAll("\\]", "").replaceAll("\\[", "");

        return "&type=annotation"+
                "&magigKey=" + SessionManagement.MagicKey +
                "&text=" +java.net.URLEncoder.encode(commentText, "UTF-8") +
                "&creating=" + getAdd() +
                "&project=" + getProject() +
                "&document=" + getDocument() +
                "&ordinal=" + getFragmentNo() +
                "&fragment=" + java.net.URLEncoder.encode(fragmentText, "UTF-8") +
                "&pattern=" + getPattern() +
                "&pos=" + getPatternPos() +
                "&user=" + getUser();
    }

    //TODO: The regeneration does not retain the order of the annotations

    public analysis.ParseFeedbackItem generate(Project project, ContractVersionInstance version, PortalUser user, ContractFragment fragment) throws BackOfficeException {

        System.out.println("Body: " + getFragment());
        System.out.println("Located fragment: " + fragment.getOrdinal() + "(" + fragment.getName() + ")");

        ContractAnnotationTable existingAnnotations = new ContractAnnotationTable(new LookupList()
                .addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Fragment.name(), fragment.getKey())));


        String shortDescription = getFragment();
        if(shortDescription.length() > 30)
            shortDescription = shortDescription.substring(0, 30);


        for (DataObjectInterface object : existingAnnotations.getValues()) {

            ContractAnnotation annotation = (ContractAnnotation)object;

            if(annotation.getDescription().equals(getText())){

                System.out.println("Found existing annotation(s) \"" + shortDescription + "...\" in fragment " + fragment.getName() + " Skipping!");
                return new analysis.ParseFeedbackItem(analysis.ParseFeedbackItem.Severity.WARNING,
                        "Found existing annotation(s) \"" + shortDescription + "...\" in fragment " + fragment.getName() + " Skipping!", 0);

            }else{


                System.out.println(" -- NOT matching annotation " + shortDescription + "...");
            }
        }


        ContractAnnotation annotation = new ContractAnnotation(
                getText(),
                fragment.getKey(),
                (long)1,              // No ordinal passed here
                getText(),
                user.getKey(),
                version.getKey(),
                project.getKey(),
                getPattern(),
                getPatternPos(),
                getDate().getISODate());



        annotation.store();

        fragment.setAnnotationCount(fragment.getAnnotationCount() + 1);
        fragment.update();



        return new analysis.ParseFeedbackItem(analysis.ParseFeedbackItem.Severity.INFO, "Generated annotation " + getText() + " in fragment " + fragment.getName() + "!", 0);
    }


    public analysis.ParseFeedbackItem remove(Project project, ContractVersionInstance version, PortalUser user, ContractFragment fragment) throws BackOfficeException {

        System.out.println("Body: " + getFragment());
        System.out.println("Located fragment: " + fragment.getOrdinal() + "(" + fragment.getName() + ")");

        ContractAnnotationTable existingAnnotations = new ContractAnnotationTable(new LookupList()
                .addFilter(new ReferenceFilter(ContractAnnotationTable.Columns.Fragment.name(), fragment.getKey())));

        int existingAnnotationsCount = existingAnnotations.getCount();

        String shortDescription = getDescription();
        if(shortDescription.length() > 30)
            shortDescription = shortDescription.substring(0, 30);

        boolean found = false;

        for (DataObjectInterface object : existingAnnotations.getValues()) {

            ContractAnnotation annotation = (ContractAnnotation)object;

            if(annotation.getDescription().equals(getText())){

                System.out.println("  -- Matching annotation " + shortDescription + "...");
                found = true;

            }else{


                System.out.println("  -- NOT matching annotation " + shortDescription + "...");
            }
        }


        if(!found){

            System.out.println("No existing annotation \"" + shortDescription + "...\" in fragment " + fragment.getName() + "found  Aborting!");
            return new analysis.ParseFeedbackItem(analysis.ParseFeedbackItem.Severity.WARNING,
                    "No existing annotation \"" + shortDescription + "...\" in fragment " + fragment.getName() + "found  Aborting!", 0);
        }

        for (DataObjectInterface object : existingAnnotations.getValues()) {

            ContractAnnotation annotation = (ContractAnnotation)object;

            annotation.delete();
        }




        fragment.setAnnotationCount(fragment.getAnnotationCount() - existingAnnotationsCount);
        fragment.update();

        return new analysis.ParseFeedbackItem(analysis.ParseFeedbackItem.Severity.INFO, "Removed "+ existingAnnotationsCount+" annotations(s) in fragment " + fragment.getName() + "!", 0);
    }




}
