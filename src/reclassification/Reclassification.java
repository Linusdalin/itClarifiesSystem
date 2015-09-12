package reclassification;

import contractManagement.*;
import classification.*;
import userManagement.*;
import project.*;
import dataRepresentation.*;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.condition.*;

/********************************************************
 *
 *    Reclassification - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Reclassification extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ReclassificationTable();

    public Reclassification(){

        super();

        if(table == null)
            table = TABLE;
    }

    public Reclassification(String classification, boolean add, String date, String project, String document, long fragmentno, String fragment, String pattern, long patternpos, String user, boolean closed){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(classification);
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

    public Reclassification(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Reclassification o = new Reclassification();
        o.data = data;
        o.exists = true;
        return o;
    }

    public String getClassification(){

        StringData data = (StringData) this.data[0];
        return data.getStringValue();
    }

    public void setClassification(String classification){

        StringData data = (StringData) this.data[0];
        data.setStringValue(classification);
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

    /*
            new ReferenceColumn("Fragment", DataColumn.noFormatting, new TableReference("ContractFragment", "Name")),
            new StringColumn("ClassTag", DataColumn.noFormatting),
            new IntColumn("RequirementLevel", DataColumn.noFormatting),
            new IntColumn("ApplicablePhase", DataColumn.noFormatting),
            new TextColumn("Comment", DataColumn.noFormatting),
            new TextColumn("Keywords", DataColumn.noFormatting),
            new ReferenceColumn("Creator", DataColumn.noFormatting, new TableReference("PortalUser", "Name")),
            new ReferenceColumn("Version", DataColumn.noFormatting, new TableReference("ContractVersionInstance", "Version")),
            new ReferenceColumn("Project", DataColumn.noFormatting, new TableReference("Project", "Name")),
            new TextColumn("Pattern", DataColumn.noFormatting),
            new IntColumn("Pos", DataColumn.noFormatting),
            new IntColumn("Length", DataColumn.noFormatting),
            new IntColumn("Significance", DataColumn.noFormatting),
            new TextColumn("RuleId", DataColumn.noFormatting),
            new TimeStampColumn("Time", DataColumn.noFormatting),

     */


    public analysis.ParseFeedbackItem generate(Project project, ContractVersionInstance version, PortalUser user, ContractFragment fragment) throws BackOfficeException {

        System.out.println("Body: " + getFragment());
        System.out.println("Located fragment: " + fragment.getOrdinal() + "(" + fragment.getName() + ")");

        language.LanguageInterface languageForImport = new language.English();

        FragmentClassificationTable existingClassifications = new FragmentClassificationTable(new LookupList()
                .addFilter(new ReferenceFilter(FragmentClassificationTable.Columns.Fragment.name(), fragment.getKey()))
                .addFilter(new ColumnFilter(FragmentClassificationTable.Columns.ClassTag.name(), getClassification())));




        if(existingClassifications.getCount() > 0){

            System.out.println("Found an existing classification of type " + getClassification() + " in fragment " + fragment.getName() + " Aborting!");
            return new analysis.ParseFeedbackItem(analysis.ParseFeedbackItem.Severity.WARNING,
                    "Found an existing classification of type " + getClassification() + " in fragment " + fragment.getName() + " Aborting!", 0);
        }


        featureTypes.FeatureTypeInterface featureType = services.DocumentService.getFeatureTypeByTag(getClassification(), languageForImport);

        String keywords = "";
        if(featureType == null){

            return new analysis.ParseFeedbackItem(analysis.ParseFeedbackItem.Severity.WARNING,
                    "Could not find feature type for classification of type " + getClassification() + ". Aborting!", 0);

        }else{

            keywords = featureType.getHierarchy();
        }




        FragmentClassification classification = new FragmentClassification(
                fragment.getKey(),
                getClassification(),
                0,
                0,
                FragmentClassification.GENERATED,
                "regenerated",
                keywords,
                user.getKey(),
                version.getKey(),
                project.getKey(),
                getPattern(),
                getPatternPos(),
                getPattern().length(),
                100, //significance
                "",
                getDate().getISODate());

        classification.store();

        fragment.setClassificatonCount(fragment.getClassificatonCount() + 1);
        fragment.update();



        return new analysis.ParseFeedbackItem(analysis.ParseFeedbackItem.Severity.INFO, "Generated classification of type " + getClassification() + " in fragment " + fragment.getName() + "!", 0);
    }


    public analysis.ParseFeedbackItem remove(Project project, ContractVersionInstance version, PortalUser user, ContractFragment fragment) throws BackOfficeException {

        System.out.println("Body: " + getFragment());
        System.out.println("Located fragment: " + fragment.getOrdinal() + "(" + fragment.getName() + ")");

        FragmentClassificationTable existingClassifications = new FragmentClassificationTable(new LookupList()
                .addFilter(new ReferenceFilter(FragmentClassificationTable.Columns.Fragment.name(), fragment.getKey()))
                .addFilter(new ColumnFilter(FragmentClassificationTable.Columns.ClassTag.name(), getClassification())));


        int existingClassificationCount = existingClassifications.getCount();

        if(existingClassificationCount == 0){

            System.out.println("No existing classification of type " + getClassification() + " in fragment " + fragment.getName() + "found  Aborting!");
            return new analysis.ParseFeedbackItem(analysis.ParseFeedbackItem.Severity.WARNING,
                    "No existing classification of type " + getClassification() + " in fragment " + fragment.getName() + "found  Aborting!", 0);
        }

        for (DataObjectInterface object : existingClassifications.getValues()) {

            FragmentClassification classification = (FragmentClassification)object;

            classification.delete();
        }




        fragment.setClassificatonCount(fragment.getClassificatonCount() - existingClassificationCount);
        fragment.update();

        return new analysis.ParseFeedbackItem(analysis.ParseFeedbackItem.Severity.INFO, "Removed "+ existingClassificationCount+" classification(s) of type " + getClassification() + " in fragment " + fragment.getName() + "!", 0);
    }


    public String getPostRequest() throws java.io.UnsupportedEncodingException {

        String fragmentText = getFragment();
        fragmentText = fragmentText.replaceAll("&", "").replaceAll("/", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\}", "").replaceAll("\\{", "").replaceAll("\\]", "").replaceAll("\\[", "");


        return "&type=classification"+
                "&magicKey=" + SessionManagement.MagicKey +
                "&class=" + getClassification() +
                "&creating=" + getAdd() +
                "&project=" + getProject() +
                "&document=" + getDocument() +
                "&ordinal=" + getFragmentNo() +
                "&fragment=" + java.net.URLEncoder.encode(fragmentText, "UTF-8") +
                "&pattern=" + getPattern() +
                "&pos=" + getPatternPos() +
                "&user=" + getUser();
    }




}
