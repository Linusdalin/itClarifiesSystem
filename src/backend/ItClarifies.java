package backend;

import actions.ActionStatusTable;
import actions.ActionTable;
import actions.ChecklistItemTable;
import actions.ChecklistTable;
import cache.ServiceCache;
import classification.FragmentClassTable;
import classification.FragmentClassificationTable;
import module.ModuleOrganizationTable;
import module.ModuleProjectTable;
import module.ModuleTable;
import module.ModuleTagTable;
import overviewExport.ExtractionStateTable;
import overviewExport.ExtractionStatusTable;
import overviewExport.ExtractionTable;
import project.ProjectTable;
import project.ProjectTypeTable;
import reclassification.ReannotationTable;
import reclassification.ReclassificationTable;
import contractManagement.*;
import crossReference.DefinitionTable;
import crossReference.ReferenceTable;
import crossReference.ReferenceTypeTable;
import dataRepresentation.DataObjectInterface;
import dataRepresentation.DataTableInterface;
import log.PukkaLogger;
import pukkaBO.acs.ACS_User;
import pukkaBO.acs.ACS_UserTable;
import pukkaBO.password.PasswordManager;
import reclassification.RedefinitionTable;
import reclassification.ReriskTable;
import risk.ContractRiskTable;
import risk.RiskClassificationTable;
import search.KeywordTable;
import versioning.SnapshotTable;
import pukkaAnalysis.DashboardPage;
import pukkaBO.Charts.ChartInterface;
import pukkaBO.GenericPage.PageInterface;
import pukkaBO.acs.SSOLoginInterface;
import pukkaBO.backOffice.*;
import pukkaBO.condition.LookupList;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.form.FormInterface;
import pukkaBO.formsPredefined.LoginForm;
import pukkaBO.links.LinkElement;
import pukkaBO.list.ListInterface;
import pukkaBO.pivot.PivotView;
import pukkaBO.renderer.TableRendererStarlightStatic;
import userManagement.*;

import java.io.Serializable;
import java.util.Arrays;


/***************************************************************************'
 *
 *          It clarifies system
 *
 *           TODO:
 *
 *
 *             Performance
 *
 *              - zip json in cache
 *              - cache checklists and checklist items
 *
 *
 *             References
 *
 *               - Verify that it is possible to add a reference in back office
 *               - Chapter references as references
 *               - Handle internal reference (links in word document)
 *               - Handle page references as reference (as described on page X)
 *
 *
 *
 *
 */


public class ItClarifies extends AppBackOffice implements BackOfficeInterface, Serializable {


    public ItClarifies(){

        this("backoffice.config");  // Default Configuration if no file is given

    }

    /*********************************************************
     *
     *          This is an example of how to set up a back office structure.
     *          The main data is passed to the menu.
     *
     */

    public ItClarifies(String configFile){

        // 1. Call super to load properties

        super(configFile);



        // 2. Set the style config with settings for the back-office

        styleConfig = new StyleConfiguration(
                "bootstrap",
                //properties.style,                                 // Style
                "test/pukkaLogo.png",                               // TODO: Improvement Usability: (bo) Change logo to itClarifies logo
                "ItClarifies Backoffice",                           // Caption
                "Welcome to itClarifies backoffice ",               // Welcome text
                new TableRendererStarlightStatic(this)              // Table render logic to render all the tables
        );

        // Access rights

        whiteList.allow("217.13.245.*");        // Office IP
        whiteList.allow("127.0.0.1");           // Local host for testing
        whiteList.allow("213.89.59.*");         // Linus Home
        whiteList.allow("83.251.28.*");         // Linus Home
        whiteList.allow("83.253.*.*");         // Linus Home
        whiteList.allow("213.185.250.*");       // Ulf Home
        whiteList.allow("95.198.*.*");        // New office
        whiteList.allow("95.192.*.*");        // New office

        // Set the menu structure

        //PukkaLogger.log(PukkaLogger.Level.INFO, "!!!!!Configuring!!!!");

        menu = new Menu(

                // The menu is an array of sections.

                new Section[] {

                        // Home section
                        // A section is built by a section group list, link elements, lists and pivot tables

                    new Section("Home", "login.jsp", "table.jsp", Icon.Home,

                            new SectionGroups(

                                new SectionGroup[]{

                                   new SectionGroup("Contracts",

                                        new DataTableInterface[] {
                                        }
                                   )
                                }
                            ),

                            new LinkElement[] {


                            },
                            new ListInterface[] {

                            },
                            new PivotView[] {

                            }

                    ),


                        new Section("Organizations", "login.jsp", "table.jsp", Icon.Dollartag,

                                new SectionGroups(

                                    new SectionGroup[]{

                                            new SectionGroup("Customers",

                                                 new DataTableInterface[] {

                                                         new OrganizationTable( ),
                                                         new OrganizationConfTable( ),
                                                 }
                                             ),


                                    }
                                ),

                                new LinkElement[] {

                                        new LinkElement("Dashboard",            new DashboardPage()),
                                        //new LinkElement("Create Reference",     new CreateReferenceLightbox(null)),

                                },
                                new ListInterface[] {

                                        new OrganizationList(this),

                                },
                                new PivotView[] {

                                }

                        ),


                        new Section("Projects", "login.jsp", "table.jsp", Icon.Dollartag,

                                new SectionGroups(

                                    new SectionGroup[]{


                                            new SectionGroup("Projects",

                                                 new DataTableInterface[] {

                                                         new contractManagement.ContractTypeTable( ),
                                                         new ProjectTable( ),
                                                         new ProjectTypeTable( ),

                                                 }
                                             ),

                                    }
                                ),

                                new LinkElement[] {

                                        new LinkElement("Dashboard",            new DashboardPage()),
                                        //new LinkElement("Create Reference",     new CreateReferenceLightbox(null)),

                                },
                                new ListInterface[] {

                                        new ProjectList(this),
                                        new DefinitionList(this),

                                },
                                new PivotView[] {

                                }

                        ),


                            new Section("Users", "login.jsp", "table.jsp", Icon.Users,

                                    new SectionGroups(

                                        new SectionGroup[]{

                                                new SectionGroup("Activity",

                                                     new DataTableInterface[] {

                                                             new ChecklistTable( ),
                                                             new ChecklistItemTable( ),
                                                             new ActionTable( ),
                                                             new ActionStatusTable( ),

                                                     }
                                                 ),

                                                new SectionGroup("Users",

                                                     new DataTableInterface[] {

                                                            new PortalUserTable( ),
                                                            new GroupTable( ),
                                                     }
                                                 ),
                                                new SectionGroup("Access",

                                                     new DataTableInterface[] {

                                                            new PortalSessionTable( ),
                                                            new SessionStatusTable( ),
                                                            new VisibilityTable( ),

                                                            new AccessRightTable( ),
                                                            new AccessGrantTable( ),
                                                     }
                                                 ),
                                        }
                                    ),

                                    new LinkElement[] {

                                    },
                                    new ListInterface[] {

                                            new UserList(this),   // LIst of users
                                    },
                                    new PivotView[] {

                                    }

                            ),



                    new Section("Invoicing", "login.jsp", "table.jsp", Icon.Dollartag,

                            new SectionGroups(

                                new SectionGroup[]{

                                   new SectionGroup("Contracts",

                                        new DataTableInterface[] {
                                        }
                                   )
                                }
                            ),

                            new LinkElement[] {

                            },
                            new ListInterface[] {

                            },
                            new PivotView[] {

                            }

                    ),


                    new Section("Documents", "login.jsp", "table.jsp", Icon.Document,

                            new SectionGroups(

                                new SectionGroup[]{

                                   new SectionGroup("User Documents",

                                        new DataTableInterface[] {

                                                new ContractTable( ),
                                                new ContractVersionInstanceTable( ),
                                                new ContractViewTable( ),
                                                new DocumentSectionTable( ),


                                        }
                                    ),

                                        new SectionGroup("Analysis",

                                             new DataTableInterface[] {


                                                     new ContractStatusTable(),
                                                     new ContractFragmentTable( ),
                                                     new FragmentClassTable( ),
                                                     new FragmentClassificationTable( ),
                                                     new RiskClassificationTable( ),
                                                     new ContractAnnotationTable( ),
                                                     new ContractFragmentTypeTable( ),
                                                     new StructureItemTable( ),
                                                     new StructureItemTypeTable( ),
                                                     new ContractClauseTable( ),
                                                     new ContractRiskTable( ),
                                                     new KeywordTable( ),
                                                     new ReclassificationTable( ),
                                                     new ReriskTable( ),
                                                     new ReannotationTable( ),
                                                     new RedefinitionTable( ),
                                                     new ExtractionTable( ),
                                                     new ExtractionStatusTable( ),
                                                     new ExtractionStateTable( ),


                                             }
                                         ),

                                        new SectionGroup("Version Control",

                                             new DataTableInterface[] {

                                                     new SnapshotTable( ),

                                             }
                                         ),

                                        new SectionGroup("Cross Reference",

                                             new DataTableInterface[] {

                                                     new ReferenceTable( ),
                                                     new ReferenceTypeTable( ),
                                                     new DefinitionTable( ),

                                             }
                                         ),

                                        new SectionGroup("Modules",

                                             new DataTableInterface[] {

                                                     new ModuleTable( ),
                                                     new ModuleOrganizationTable( ),
                                                     new ModuleProjectTable( ),
                                                     new ModuleTagTable()

                                             }
                                         ),


                                }
                            ),

                        new LinkElement[] {

                                new LinkElement("Import Reclassifications",            new ReclassificationImportPage()),

                                //new LinkElement("Form Example", "form.jsp?form=ExampleForm2&section=Home", "An example form"),
                                //new LinkElement("Form Example",     new ExampleForm(this), ACS_Product.GENERAL),
                                //new LinkElement("Service Overview",     new ServiceContractPivot()),
                                //new LinkElement("PivotTest",            new ExamplePivotPage()),

                                //new LinkElement("Test Light box",   new TestLightbox(), ACS_Product.GENERAL),

                        },
                        new ListInterface[] {

                                new DocumentList(this),
                                new ChecklistList(this),
                                new ActionList(this),
                                new RiskList(this),
                                new ClassificationList(this),
                                new ReclassificationList(this),

                        },
                        new PivotView[] {

                            /*    new PivotView("examplePivot", "An example pivot table", (DataTableInterface)new ExamplePivotObject(),
                                        1, 2, 4, 2,
                                        "Showing pivot functions"),
                             */
                        }

                    ),





                /*
                new Section("Notifications", "login.jsp", "table.jsp", Icon.DEFAULT,

                        new SectionGroups(


                            new SectionGroup[]{

                               new SectionGroup("Transactions",

                                    new DataTableInterface[] {

                                            //new TransactionTable( ),
                                            //new ScheduledJobTable( ),
                                            //new SchedulerLogTable( ),
                                            //new ScheduledJobStatusTable( ),
                                            //new SelfTestTable( ),
                                    }
                                ),
                            }
                        ),

                    new LinkElement[] {

                            //new LinkElement( "Start new jobs",  "startScheduler.jsp"),

                    },
                    new ListInterface[] {
                            new SchedulerTool(this),

                    },
                    new PivotView[] {

                    }

                ),
                */

            },

                properties);


        // Add forms to the backoffice. These will be possible to lookup the same way as lists and tables.

        forms = new FormInterface[] {

                new LoginForm(),
                new EditFragmentDetailsForm( this, ""),
                new AddDefinitionForm( this, ""),
                new AddReferenceForm( this, ""),
                new ExportForm( this, null, ""),
        };


        charts = new ChartInterface[] {

                 //       new TestChart(),        // Test bar chart
                 //       new TestChart2(),       // Test pie chart

                };

        pages = new PageInterface[] {

                //new ReviewPage(),

                new DashboardPage(),
                new ReclassificationImportPage(),
                new EditFragmentDetailsLightbox(  ),

                //new TestLightbox(),
                //new TestPage(),        // Test bar chart
                //new SchedulerPage(),
                //new ExamplePivotPage(),
                //new UploadPage(),

                //new ACS_AdminPage(),
        };



        //startScheduler();

    }

    /*******************************************************************'
     *
     *      Values that will be set upon starting the system.
     *
     *
     *
     */


    public void populateSpecificValues() {

        PukkaLogger.log(PukkaLogger.Level.INFO, "Adding special values");

        // Generate encrypted passwords for the test and default users on the site

        PasswordManager pwdManager = new PasswordManager();
        ACS_UserTable allAdminUsers = new ACS_UserTable(new LookupList());

        try {


            // Do the same thing for the ACS_Admin


            for(DataObjectInterface object : allAdminUsers.getValues()){

                ACS_User user = (ACS_User)object;

                byte[] salt = pwdManager.generateSalt();
                byte[] encodedPassword = pwdManager.getEncryptedPassword(user.getPassword(), salt);

                PukkaLogger.log(PukkaLogger.Level.INFO, "Encoding password " + user.getPassword() + " for user " + user.getName() + " to " + Arrays.toString(encodedPassword));


                user.setPassword(new String(encodedPassword, "ISO-8859-1"));
                user.setSalt(new String(salt, "ISO-8859-1"));
                user.update();




            }

            // Store the pre-loggedin demo users

            ServiceCache cache = new ServiceCache("Token");
            cache.store("DummyAdminToken", "admin@2020-01-01 00:00:00#127.0.0.1", "");
            cache.store("DummySessionToken", "demo@2020-01-01 00:00:00#127.0.0.1", "");



        } catch (Exception e) {

            PukkaLogger.log(PukkaLogger.Level.ERROR, "Failed to generate passwords for test users");
            e.printStackTrace(System.out);  //To change body of catch statement use File | Settings | File Templates.
        }


    }


    /****************************************************************************************
     *
     *              Create the database and populate the default values
     *
     * @param args -
     */


    public static void main(String[] args){

        BackOfficeInterface bo = new ItClarifies();
        bo.init();


    }

    public void init(){

        createDb();
        populateValues(false);
        populateSpecificValues();

    }


    public EventHandlerInterface getEventHandler() throws BackOfficeException{

        return null;
    }

    public SSOLoginInterface getSSO(){

        // Google SSO

        //return new GoogleSSO();
        return new EmptySSOService();
    }


    public String getCopyright() {
        return "2011 - 2014 ItClarifies";
    }

}




