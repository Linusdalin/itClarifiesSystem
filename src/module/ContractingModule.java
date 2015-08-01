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

                                new ModuleNode(FeatureTypeTree.TermAndTermination, new ModuleNode[]{

                                        new ModuleNode(FeatureTypeTree.Term, LEAF),
                                        new ModuleNode(FeatureTypeTree.Termination, LEAF),

                                }),

                                    new ModuleNode(FeatureTypeTree.RightsAndRestrictions, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.Right, LEAF),
                                            new ModuleNode(FeatureTypeTree.Restriction, LEAF),
                                            new ModuleNode(FeatureTypeTree.Responsibility, LEAF),

                                    }),


                            }),

                            new ModuleNode(FeatureTypeTree.Legal, new ModuleNode[]{

                                    new ModuleNode(FeatureTypeTree.IPR, LEAF),
                                    new ModuleNode(FeatureTypeTree.LegalEntity, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.Parts, LEAF),
                                            //new ClassificationOverviewNode(FeatureTypeTree.PartyUsage, LEAF),

                                    }),
                                    new ModuleNode(FeatureTypeTree.GeneralProvisions, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.Arbitration, LEAF),
                                            new ModuleNode(FeatureTypeTree.Precedence, LEAF),

                                    }),

                            }),
                            new ModuleNode(FeatureTypeTree.Governance, new ModuleNode[]{

                                    new ModuleNode(FeatureTypeTree.Reporting, LEAF),
                                    new ModuleNode(FeatureTypeTree.ChangeMgmnt, LEAF),
                                    new ModuleNode(FeatureTypeTree.Audit, LEAF),
                                    new ModuleNode(FeatureTypeTree.Disclosure, LEAF),

                            }),

                            new ModuleNode(FeatureTypeTree.Financial, new ModuleNode[]{

                                    new ModuleNode(FeatureTypeTree.Terms, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.Compensation, LEAF),
                                            new ModuleNode(FeatureTypeTree.Expenses, LEAF),
                                            new ModuleNode(FeatureTypeTree.Payment, LEAF),
                                            new ModuleNode(FeatureTypeTree.Invoicing, LEAF),


                                    }),
                                    new ModuleNode(FeatureTypeTree.Damages, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.Penalties, LEAF),
                                            new ModuleNode(FeatureTypeTree.LiquidatedDamages, LEAF),
                                            new ModuleNode(FeatureTypeTree.LimitationOfLiability, LEAF),
                                            new ModuleNode(FeatureTypeTree.Indemnification, LEAF),

                                    }),
                                    new ModuleNode(FeatureTypeTree.Warranty, LEAF),

                            }),

                            new ModuleNode(FeatureTypeTree.Compliance, new ModuleNode[]{

                                    new ModuleNode(FeatureTypeTree.LegalCompliance, LEAF),
                                    new ModuleNode(FeatureTypeTree.RegulatoryCompliance, LEAF),
                                    new ModuleNode(FeatureTypeTree.StandardsCompliance, LEAF),

                            }),

                            new ModuleNode(FeatureTypeTree.Solution, new ModuleNode[]{

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
                                    new ModuleNode(FeatureTypeTree.Staffing, LEAF),


                                    new ModuleNode(FeatureTypeTree.Resources, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.Staffing, LEAF),
                                            new ModuleNode(FeatureTypeTree.Subcontractors, LEAF),

                                    }),
                                    new ModuleNode(FeatureTypeTree.Acceptance, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.AcceptanceCriteria, LEAF),

                                    }),

                                    new ModuleNode(FeatureTypeTree.RiskMgmnt, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.Security, LEAF),
                                    }),




                            }),


                    }),



                    new ModuleNode(FeatureTypeTree.Highlight, new ModuleNode[]{

                            new ModuleNode(FeatureTypeTree.Content, new ModuleNode[]{

                                    new ModuleNode(FeatureTypeTree.Definition, new ModuleNode[]{

                                            new ModuleNode(FeatureTypeTree.DefinitionDef, LEAF),
                                            new ModuleNode(FeatureTypeTree.DefinitionUsage, LEAF),
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
                                    new ModuleNode(FeatureTypeTree.Email, LEAF),
                            }),

                    }),

            });


}
