package module;

import featureTypes.FeatureTypeTree;

/*************************************************************************************
 *
 *              This is the default structure for presentation
 *
 *
 */


public class ContractingModule extends AbstractModule implements ModuleInterface{


    /**********************************************************
     *
     *          Creating a Contracting Module
     *
     *
     */

    public ContractingModule(){

        super(root);
    }


    /*********************************************************************************
     *
     *              This is the tree structure for display.
     *
     *
     */


    public static final ModuleNode root =

            new ModuleNode(FeatureTypeTree.Root, new ModuleNode[]{

                    new ModuleNode(FeatureTypeTree.ContractDelivery, new ModuleNode[]{

                            new ModuleNode(FeatureTypeTree.General, new ModuleNode[]{

                                new ModuleNode(FeatureTypeTree.TERM_AND_TERMINATION, new ModuleNode[]{

                                        new ModuleNode(FeatureTypeTree.TERM, LEAF),
                                        new ModuleNode(FeatureTypeTree.TERMINATION, LEAF),

                                }),

                                    new ModuleNode(FeatureTypeTree.RIGHTS_AND_OBLIGATIONS, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.RIGHT, LEAF),
                                            new ModuleNode(FeatureTypeTree.RESTRICTION, LEAF),
                                            new ModuleNode(FeatureTypeTree.RESPONSIBILITY, LEAF),
                                            new ModuleNode(FeatureTypeTree.EXEMPTION, LEAF),

                                    }),


                            }),

                            new ModuleNode(FeatureTypeTree.Legal, new ModuleNode[]{

                                    new ModuleNode(FeatureTypeTree.IPR, LEAF),
                                    new ModuleNode(FeatureTypeTree.LegalEntity, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.PARTY, LEAF),
                                            //new ClassificationOverviewNode(FeatureTypeTree.PartyUsage, LEAF),

                                    }),
                                    new ModuleNode(FeatureTypeTree.GeneralProvisions, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.ARBITRATION, LEAF),
                                            new ModuleNode(FeatureTypeTree.PRECEDENCE, LEAF),

                                    }),

                            }),
                            new ModuleNode(FeatureTypeTree.GOVERNANCE, new ModuleNode[]{

                                    new ModuleNode(FeatureTypeTree.REPORTING, LEAF),
                                    new ModuleNode(FeatureTypeTree.CHANGE_MGMNT, LEAF),
                                    new ModuleNode(FeatureTypeTree.AUDIT, LEAF),
                                    new ModuleNode(FeatureTypeTree.DISCLOSURE, LEAF),

                            }),

                            new ModuleNode(FeatureTypeTree.Financial, new ModuleNode[]{

                                    new ModuleNode(FeatureTypeTree.TERMS, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.COMPENSATION, LEAF),
                                            new ModuleNode(FeatureTypeTree.EXPENSES, LEAF),
                                            new ModuleNode(FeatureTypeTree.PAYMENT, LEAF),
                                            new ModuleNode(FeatureTypeTree.INVOICING, LEAF),


                                    }),
                                    new ModuleNode(FeatureTypeTree.DAMAGES, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.PENALTIES, LEAF),
                                            new ModuleNode(FeatureTypeTree.LIQUIDATED_DAMAGES, LEAF),
                                            new ModuleNode(FeatureTypeTree.LIMITATION_OF_LIABILITY, LEAF),
                                            new ModuleNode(FeatureTypeTree.INDEMNIFICATION, LEAF),

                                    }),
                                    new ModuleNode(FeatureTypeTree.WARRANTY, LEAF),

                            }),

                            new ModuleNode(FeatureTypeTree.COMPLIANCE, new ModuleNode[]{

                                    new ModuleNode(FeatureTypeTree.LEGAL_COMPLIANCE, LEAF),
                                    new ModuleNode(FeatureTypeTree.REGULATORY_COMPLIANCE, LEAF),
                                    new ModuleNode(FeatureTypeTree.STANDARDS_COMPLIANCE, LEAF),

                            }),

                            new ModuleNode(FeatureTypeTree.SOLUTION, new ModuleNode[]{

                                    new ModuleNode(FeatureTypeTree.Scope, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.Preconditions, LEAF),
                                            new ModuleNode(FeatureTypeTree.SupportMaint, LEAF),
                                            new ModuleNode(FeatureTypeTree.SolutionReq, new ModuleNode[]{

                                                    new ModuleNode(FeatureTypeTree.SLA, new ModuleNode[]{

                                                            new ModuleNode(FeatureTypeTree.Delays, LEAF),
                                                            new ModuleNode(FeatureTypeTree.Defects, LEAF),
                                                            new ModuleNode(FeatureTypeTree.BusinessContinuity, LEAF),

                                                    }),
                                            }),
                                            new ModuleNode(FeatureTypeTree.Delivery, LEAF),

                                    }),

                                    new ModuleNode(FeatureTypeTree.RESOURCES, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.STAFFING, LEAF),
                                            new ModuleNode(FeatureTypeTree.SUBCONTRACTORS, LEAF),

                                    }),
                                    new ModuleNode(FeatureTypeTree.ACCEPTANCE, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.ACCEPTANCE_CRITERIA, LEAF),

                                    }),

                                    new ModuleNode(FeatureTypeTree.RiskMgmnt, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.Security, LEAF),
                                    }),




                            }),


                    }),



                    new ModuleNode(FeatureTypeTree.Highlight, new ModuleNode[]{

                            new ModuleNode(FeatureTypeTree.Content, new ModuleNode[]{

                                    new ModuleNode(FeatureTypeTree.DEFINITION_CONCEPT, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.DEFINITION, LEAF),
                                            new ModuleNode(FeatureTypeTree.DEFINITION_USAGE, LEAF),
                                            new ModuleNode(FeatureTypeTree.Background, LEAF),

                                    }),
                            }),

                            new ModuleNode(FeatureTypeTree.Risk, new ModuleNode[]{

                                    new ModuleNode(FeatureTypeTree.Unspecific, LEAF),
                                    new ModuleNode(FeatureTypeTree.Exclusivity, LEAF),

                            }),



                            new ModuleNode(FeatureTypeTree.Structure, new ModuleNode[]{

                                    new ModuleNode(FeatureTypeTree.Date, LEAF),
                                    new ModuleNode(FeatureTypeTree.Percentage, LEAF),
                                    new ModuleNode(FeatureTypeTree.EMAIL, LEAF),
                            }),

                    }),

            });


}
