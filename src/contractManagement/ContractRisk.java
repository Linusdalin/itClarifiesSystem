package contractManagement;

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
 *    ContractRisk - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractRisk extends DataObject implements DataObjectInterface{

    private static ContractRisk Black = null;  
    private static ContractRisk Red = null;  
    private static ContractRisk Amber = null;  
    private static ContractRisk Unknown = null;  
    private static ContractRisk None = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new ContractRiskTable();

    public ContractRisk(){

        super();         if(table == null)
            table = TABLE;
    }

    public ContractRisk(String name, DataObjectInterface organization, long severity, String description) throws BackOfficeException{

        this(name, organization.getKey(), severity, description);
    }


    public ContractRisk(String name, DBKeyInterface organization, long severity, String description) throws BackOfficeException{

        this();
        ColumnStructureInterface[] columns = getColumnFromTable();


        data = new ColumnDataInterface[columns.length];

        data[0] = new StringData(name);
        data[1] = new ReferenceData(organization, columns[1].getTableReference());
        data[2] = new IntData(severity);
        data[3] = new TextData(description);

        exists = true;


    }
    /*********************************************************************''
     *
     *          Load from database
     *
     * @param condition - the SQL condition for selecting ONE UNIQUE object
     */

    public ContractRisk(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ContractRisk o = new ContractRisk();
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



    public DBKeyInterface getOrganizationId(){

        ReferenceData data = (ReferenceData)this.data[1];
        return data.value;
    }

    public Organization getOrganization(){

        ReferenceData data = (ReferenceData)this.data[1];
        return new Organization(new LookupByKey(data.value));
    }

    public void setOrganization(DBKeyInterface organization){

        ReferenceData data = (ReferenceData)this.data[1];
        data.value = organization;
    }



    public long getSeverity(){

        IntData data = (IntData) this.data[2];
        return data.value;
    }

    public void setSeverity(long severity){

        IntData data = (IntData) this.data[2];
        data.value = severity;
    }



    public String getDescription(){

        TextData data = (TextData) this.data[3];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[3];
        data.setStringValue(description);
    }



    public static ContractRisk getBlack( ) throws BackOfficeException{

       if(ContractRisk.Black == null)
          ContractRisk.Black = new ContractRisk(new LookupItem().addFilter(new ColumnFilter("Name", "Black")));
       if(!ContractRisk.Black.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Black is missing (db update required?)");

       return ContractRisk.Black;
     }

    public static ContractRisk getRed( ) throws BackOfficeException{

       if(ContractRisk.Red == null)
          ContractRisk.Red = new ContractRisk(new LookupItem().addFilter(new ColumnFilter("Name", "Red")));
       if(!ContractRisk.Red.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Red is missing (db update required?)");

       return ContractRisk.Red;
     }

    public static ContractRisk getAmber( ) throws BackOfficeException{

       if(ContractRisk.Amber == null)
          ContractRisk.Amber = new ContractRisk(new LookupItem().addFilter(new ColumnFilter("Name", "Amber")));
       if(!ContractRisk.Amber.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Amber is missing (db update required?)");

       return ContractRisk.Amber;
     }

    public static ContractRisk getUnknown( ) throws BackOfficeException{

       if(ContractRisk.Unknown == null)
          ContractRisk.Unknown = new ContractRisk(new LookupItem().addFilter(new ColumnFilter("Name", "Not Set")));
       if(!ContractRisk.Unknown.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant Unknown is missing (db update required?)");

       return ContractRisk.Unknown;
     }

    public static ContractRisk getNone( ) throws BackOfficeException{

       if(ContractRisk.None == null)
          ContractRisk.None = new ContractRisk(new LookupItem().addFilter(new ColumnFilter("Name", "No risk")));
       if(!ContractRisk.None.exists())
          throw new BackOfficeException(BackOfficeException.TableError, "Constant None is missing (db update required?)");

       return ContractRisk.None;
     }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

        ContractRisk.Black = null;
        ContractRisk.Red = null;
        ContractRisk.Amber = null;
        ContractRisk.Unknown = null;
        ContractRisk.None = null;
    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
