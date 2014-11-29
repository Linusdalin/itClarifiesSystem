package contractManagement;

import risk.*;
import contractManagement.*;
import userManagement.*;
import versioning.*;
import actions.*;
import search.*;
import crossReference.*;
import dataRepresentation.*;
import databaseLayer.DBKeyInterface;
import java.util.List;
import java.util.Map;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.condition.*;
import pukkaBO.database.*;

import pukkaBO.acs.*;

/********************************************************
 *
 *    FragmentClass - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class FragmentClass extends DataObject implements DataObjectInterface{

    private static FragmentClass Unknown = null;  
    private static FragmentClass NoParent = null;  
    private static FragmentClass Link = null;  
    private static FragmentClass LegalEntity = null;  
    private static FragmentClass Date = null;  
    private static FragmentClass Amount = null;  
    private static FragmentClass Percentage = null;  
    private static FragmentClass Number = null;  
    private static FragmentClass Email = null;  
    private static FragmentClass Definition = null;  
    private static FragmentClass DefinitionS = null;  
    private static FragmentClass DefinitionU = null;  
    private static FragmentClass ContractTerm = null;  
    private static FragmentClass Payment = null;  
    private static FragmentClass Finance = null;  
    private static FragmentClass Regulation = null;  
    private static FragmentClass Arbitration = null;  
    private static FragmentClass Organizational = null;  
    private static FragmentClass Risk = null;  
    private static FragmentClass Ambiguity = null;  
    private static FragmentClass Compensation = null;  
    private static FragmentClass AwardCriteria = null;  
    private static FragmentClass Operations = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new FragmentClassTable();

    public FragmentClass(){

        super();         if(table == null)
            table = TABLE;
    }

    public FragmentClass(String name, String type, String keywords, String description, DataObjectInterface organization, DataObjectInterface parent) throws BackOfficeException{

        this(name, type, keywords, description, organization.getKey(), parent.getKey());
    }


    public FragmentClass(String name, String type, String keywords, String description, DBKeyInterface organization, DBKeyInterface parent) throws BackOfficeException{

        this();
        ColumnStructureInterface[] columns = getColumnFromTable();


        data = new ColumnDataInterface[columns.length];

        data[0] = new StringData(name);
        data[1] = new StringData(type);
        data[2] = new TextData(keywords);
        data[3] = new TextData(description);
        data[4] = new ReferenceData(organization, columns[4].getTableReference());
        data[5] = new ReferenceData(parent, columns[5].getTableReference());

        exists = true;


    }
    /*********************************************************************''
     *
     *          Load from database
     *
     * @param condition - the SQL condition for selecting ONE UNIQUE object
     */

    public FragmentClass(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        FragmentClass o = new FragmentClass();
        o.data = data;
        o.exists = true;
        return o;
    }

    public String getName(){

        StringData data = (StringData) this.data[0];
        return data.getStringValue();
    }

    public void setName(String name){

        StringData data = (StringData) this.data[0];
        data.setStringValue(name);
    }



    public String getType(){

        StringData data = (StringData) this.data[1];
        return data.getStringValue();
    }

    public void setType(String type){

        StringData data = (StringData) this.data[1];
        data.setStringValue(type);
    }



    public String getKeywords(){

        TextData data = (TextData) this.data[2];
        return data.getStringValue();
    }

    public void setKeywords(String keywords){

        TextData data = (TextData) this.data[2];
        data.setStringValue(keywords);
    }



    public String getDescription(){

        TextData data = (TextData) this.data[3];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[3];
        data.setStringValue(description);
    }



    public DBKeyInterface getOrganizationId(){

        ReferenceData data = (ReferenceData)this.data[4];
        return data.value;
    }

    public Organization getOrganization(){

        ReferenceData data = (ReferenceData)this.data[4];
        return new Organization(new LookupByKey(data.value));
    }

    public void setOrganization(DBKeyInterface organization){

        ReferenceData data = (ReferenceData)this.data[4];
        data.value = organization;
    }



    public DBKeyInterface getParentId(){

        ReferenceData data = (ReferenceData)this.data[5];
        return data.value;
    }

    public FragmentClass getParent(){

        ReferenceData data = (ReferenceData)this.data[5];
        return new FragmentClass(new LookupByKey(data.value));
    }

    public void setParent(DBKeyInterface parent){

        ReferenceData data = (ReferenceData)this.data[5];
        data.value = parent;
    }



    public static FragmentClass getUnknown( ) throws BackOfficeException{

       if(FragmentClass.Unknown == null)
          FragmentClass.Unknown = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "unknown")));
       if(!FragmentClass.Unknown.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Unknown is missing (db update required?)");

       return FragmentClass.Unknown;
     }

    public static FragmentClass getNoParent( ) throws BackOfficeException{

       if(FragmentClass.NoParent == null)
          FragmentClass.NoParent = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "top")));
       if(!FragmentClass.NoParent.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant NoParent is missing (db update required?)");

       return FragmentClass.NoParent;
     }

    public static FragmentClass getLink( ) throws BackOfficeException{

       if(FragmentClass.Link == null)
          FragmentClass.Link = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Url")));
       if(!FragmentClass.Link.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Link is missing (db update required?)");

       return FragmentClass.Link;
     }

    public static FragmentClass getLegalEntity( ) throws BackOfficeException{

       if(FragmentClass.LegalEntity == null)
          FragmentClass.LegalEntity = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Entitet")));
       if(!FragmentClass.LegalEntity.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant LegalEntity is missing (db update required?)");

       return FragmentClass.LegalEntity;
     }

    public static FragmentClass getDate( ) throws BackOfficeException{

       if(FragmentClass.Date == null)
          FragmentClass.Date = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Datum")));
       if(!FragmentClass.Date.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Date is missing (db update required?)");

       return FragmentClass.Date;
     }

    public static FragmentClass getAmount( ) throws BackOfficeException{

       if(FragmentClass.Amount == null)
          FragmentClass.Amount = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Siffra")));
       if(!FragmentClass.Amount.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Amount is missing (db update required?)");

       return FragmentClass.Amount;
     }

    public static FragmentClass getPercentage( ) throws BackOfficeException{

       if(FragmentClass.Percentage == null)
          FragmentClass.Percentage = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Procent")));
       if(!FragmentClass.Percentage.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Percentage is missing (db update required?)");

       return FragmentClass.Percentage;
     }

    public static FragmentClass getNumber( ) throws BackOfficeException{

       if(FragmentClass.Number == null)
          FragmentClass.Number = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Nummer")));
       if(!FragmentClass.Number.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Number is missing (db update required?)");

       return FragmentClass.Number;
     }

    public static FragmentClass getEmail( ) throws BackOfficeException{

       if(FragmentClass.Email == null)
          FragmentClass.Email = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Email")));
       if(!FragmentClass.Email.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Email is missing (db update required?)");

       return FragmentClass.Email;
     }

    public static FragmentClass getDefinition( ) throws BackOfficeException{

       if(FragmentClass.Definition == null)
          FragmentClass.Definition = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Definition")));
       if(!FragmentClass.Definition.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Definition is missing (db update required?)");

       return FragmentClass.Definition;
     }

    public static FragmentClass getDefinitionS( ) throws BackOfficeException{

       if(FragmentClass.DefinitionS == null)
          FragmentClass.DefinitionS = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Definition Source")));
       if(!FragmentClass.DefinitionS.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant DefinitionS is missing (db update required?)");

       return FragmentClass.DefinitionS;
     }

    public static FragmentClass getDefinitionU( ) throws BackOfficeException{

       if(FragmentClass.DefinitionU == null)
          FragmentClass.DefinitionU = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Definition Referens")));
       if(!FragmentClass.DefinitionU.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant DefinitionU is missing (db update required?)");

       return FragmentClass.DefinitionU;
     }

    public static FragmentClass getContractTerm( ) throws BackOfficeException{

       if(FragmentClass.ContractTerm == null)
          FragmentClass.ContractTerm = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Avtalsperiod")));
       if(!FragmentClass.ContractTerm.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant ContractTerm is missing (db update required?)");

       return FragmentClass.ContractTerm;
     }

    public static FragmentClass getPayment( ) throws BackOfficeException{

       if(FragmentClass.Payment == null)
          FragmentClass.Payment = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Betalning")));
       if(!FragmentClass.Payment.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Payment is missing (db update required?)");

       return FragmentClass.Payment;
     }

    public static FragmentClass getFinance( ) throws BackOfficeException{

       if(FragmentClass.Finance == null)
          FragmentClass.Finance = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Finansiering")));
       if(!FragmentClass.Finance.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Finance is missing (db update required?)");

       return FragmentClass.Finance;
     }

    public static FragmentClass getRegulation( ) throws BackOfficeException{

       if(FragmentClass.Regulation == null)
          FragmentClass.Regulation = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Lagar")));
       if(!FragmentClass.Regulation.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Regulation is missing (db update required?)");

       return FragmentClass.Regulation;
     }

    public static FragmentClass getArbitration( ) throws BackOfficeException{

       if(FragmentClass.Arbitration == null)
          FragmentClass.Arbitration = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Tvist")));
       if(!FragmentClass.Arbitration.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Arbitration is missing (db update required?)");

       return FragmentClass.Arbitration;
     }

    public static FragmentClass getOrganizational( ) throws BackOfficeException{

       if(FragmentClass.Organizational == null)
          FragmentClass.Organizational = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Organisation")));
       if(!FragmentClass.Organizational.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Organizational is missing (db update required?)");

       return FragmentClass.Organizational;
     }

    public static FragmentClass getRisk( ) throws BackOfficeException{

       if(FragmentClass.Risk == null)
          FragmentClass.Risk = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Granska Risk")));
       if(!FragmentClass.Risk.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Risk is missing (db update required?)");

       return FragmentClass.Risk;
     }

    public static FragmentClass getAmbiguity( ) throws BackOfficeException{

       if(FragmentClass.Ambiguity == null)
          FragmentClass.Ambiguity = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Tvetydighet")));
       if(!FragmentClass.Ambiguity.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Ambiguity is missing (db update required?)");

       return FragmentClass.Ambiguity;
     }

    public static FragmentClass getCompensation( ) throws BackOfficeException{

       if(FragmentClass.Compensation == null)
          FragmentClass.Compensation = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Kompensation")));
       if(!FragmentClass.Compensation.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Compensation is missing (db update required?)");

       return FragmentClass.Compensation;
     }

    public static FragmentClass getAwardCriteria( ) throws BackOfficeException{

       if(FragmentClass.AwardCriteria == null)
          FragmentClass.AwardCriteria = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "Tilldelningsgrund")));
       if(!FragmentClass.AwardCriteria.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant AwardCriteria is missing (db update required?)");

       return FragmentClass.AwardCriteria;
     }

    public static FragmentClass getOperations( ) throws BackOfficeException{

       if(FragmentClass.Operations == null)
          FragmentClass.Operations = new FragmentClass(new LookupItem().addFilter(new ColumnFilter("Name", "IT-drift")));
       if(!FragmentClass.Operations.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Operations is missing (db update required?)");

       return FragmentClass.Operations;
     }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

        FragmentClass.Unknown = null;
        FragmentClass.NoParent = null;
        FragmentClass.Link = null;
        FragmentClass.LegalEntity = null;
        FragmentClass.Date = null;
        FragmentClass.Amount = null;
        FragmentClass.Percentage = null;
        FragmentClass.Number = null;
        FragmentClass.Email = null;
        FragmentClass.Definition = null;
        FragmentClass.DefinitionS = null;
        FragmentClass.DefinitionU = null;
        FragmentClass.ContractTerm = null;
        FragmentClass.Payment = null;
        FragmentClass.Finance = null;
        FragmentClass.Regulation = null;
        FragmentClass.Arbitration = null;
        FragmentClass.Organizational = null;
        FragmentClass.Risk = null;
        FragmentClass.Ambiguity = null;
        FragmentClass.Compensation = null;
        FragmentClass.AwardCriteria = null;
        FragmentClass.Operations = null;
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
