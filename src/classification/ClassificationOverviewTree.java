package classification;

import featureTypes.FeatureTypeTree;

/*************************************************************************************
 *
 *              This is the default structure for presentation
 *
 *
 */


public class ClassificationOverviewTree {

    private static final ClassificationOverviewNode[] LEAF = new ClassificationOverviewNode[]{};


    /*********************************************************************************
     *
     *              This is the tree structure for display.
     *
     *
     */


    public static final ClassificationOverviewNode root =

            new ClassificationOverviewNode(FeatureTypeTree.Root, new ClassificationOverviewNode[]{

                    new ClassificationOverviewNode(FeatureTypeTree.ContractDelivery, new ClassificationOverviewNode[]{

                            new ClassificationOverviewNode(FeatureTypeTree.General, new ClassificationOverviewNode[]{

                                new ClassificationOverviewNode(FeatureTypeTree.TermAndTermination, new ClassificationOverviewNode[]{

                                        new ClassificationOverviewNode(FeatureTypeTree.Term, LEAF),
                                        new ClassificationOverviewNode(FeatureTypeTree.Termination, LEAF),

                                }),

                                    new ClassificationOverviewNode(FeatureTypeTree.RightsAndRestrictions, new ClassificationOverviewNode[]{

                                            new ClassificationOverviewNode(FeatureTypeTree.Right, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.Restriction, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.Responsibility, LEAF),

                                    }),


                            }),

                            new ClassificationOverviewNode(FeatureTypeTree.Legal, new ClassificationOverviewNode[]{

                                    new ClassificationOverviewNode(FeatureTypeTree.IPR, LEAF),
                                    new ClassificationOverviewNode(FeatureTypeTree.Parts, new ClassificationOverviewNode[]{

                                            new ClassificationOverviewNode(FeatureTypeTree.Entity, LEAF),

                                    }),
                                    new ClassificationOverviewNode(FeatureTypeTree.GeneralProvisions, new ClassificationOverviewNode[]{

                                            new ClassificationOverviewNode(FeatureTypeTree.Arbitration, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.Precedence, LEAF),

                                    }),

                            }),
                            new ClassificationOverviewNode(FeatureTypeTree.Governance, new ClassificationOverviewNode[]{

                                    new ClassificationOverviewNode(FeatureTypeTree.Reporting, LEAF),
                                    new ClassificationOverviewNode(FeatureTypeTree.ChangeMgmnt, LEAF),
                                    new ClassificationOverviewNode(FeatureTypeTree.Audit, LEAF),
                                    new ClassificationOverviewNode(FeatureTypeTree.Disclosure, LEAF),

                            }),

                            new ClassificationOverviewNode(FeatureTypeTree.Financial, new ClassificationOverviewNode[]{

                                    new ClassificationOverviewNode(FeatureTypeTree.Terms, new ClassificationOverviewNode[]{

                                            new ClassificationOverviewNode(FeatureTypeTree.Compensation, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.Expenses, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.Payment, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.Invoicing, LEAF),


                                    }),
                                    new ClassificationOverviewNode(FeatureTypeTree.Damages, new ClassificationOverviewNode[]{

                                            new ClassificationOverviewNode(FeatureTypeTree.Penalties, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.LiquidatedDamages, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.LimitationOfLiability, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.Indemnification, LEAF),

                                    }),
                                    new ClassificationOverviewNode(FeatureTypeTree.Warranty, LEAF),

                            }),

                            new ClassificationOverviewNode(FeatureTypeTree.Compliance, new ClassificationOverviewNode[]{

                                    new ClassificationOverviewNode(FeatureTypeTree.LegalCompliance, LEAF),
                                    new ClassificationOverviewNode(FeatureTypeTree.RegulatoryCompliance, LEAF),
                                    new ClassificationOverviewNode(FeatureTypeTree.StandardsCompliance, LEAF),

                            }),

                            new ClassificationOverviewNode(FeatureTypeTree.Solution, new ClassificationOverviewNode[]{

                                    new ClassificationOverviewNode(FeatureTypeTree.Scope, new ClassificationOverviewNode[]{

                                            new ClassificationOverviewNode(FeatureTypeTree.Preconditions, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.SupportMaint, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.SolutionReq, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.Delivery, LEAF),

                                    }),
                                    new ClassificationOverviewNode(FeatureTypeTree.Staffing, LEAF),


                                    new ClassificationOverviewNode(FeatureTypeTree.Resources, new ClassificationOverviewNode[]{

                                            new ClassificationOverviewNode(FeatureTypeTree.Staffing, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.Subcontractors, LEAF),

                                    }),
                                    new ClassificationOverviewNode(FeatureTypeTree.SLA, new ClassificationOverviewNode[]{

                                            new ClassificationOverviewNode(FeatureTypeTree.Delays, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.Defects, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.BusinessContinuity, LEAF),

                                    }),
                                    new ClassificationOverviewNode(FeatureTypeTree.Acceptance, new ClassificationOverviewNode[]{

                                            new ClassificationOverviewNode(FeatureTypeTree.AcceptanceCriteria, LEAF),

                                    }),

                            }),


                    }),

                    new ClassificationOverviewNode(FeatureTypeTree.RiskMgmnt, new ClassificationOverviewNode[]{

                            new ClassificationOverviewNode(FeatureTypeTree.Security, LEAF),
                    }),




                    new ClassificationOverviewNode(FeatureTypeTree.Highlight, new ClassificationOverviewNode[]{

                            new ClassificationOverviewNode(FeatureTypeTree.Content, new ClassificationOverviewNode[]{

                                    new ClassificationOverviewNode(FeatureTypeTree.Definition, new ClassificationOverviewNode[]{

                                            new ClassificationOverviewNode(FeatureTypeTree.DefinitionDef, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.DefinitionUsage, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.Background, LEAF),

                                    }),
                            }),

                            new ClassificationOverviewNode(FeatureTypeTree.Risk, new ClassificationOverviewNode[]{

                                    new ClassificationOverviewNode(FeatureTypeTree.Unspecific, LEAF),
                                    new ClassificationOverviewNode(FeatureTypeTree.Exclusivity, LEAF),

                            }),



                            new ClassificationOverviewNode(FeatureTypeTree.Structure, new ClassificationOverviewNode[]{

                                    new ClassificationOverviewNode(FeatureTypeTree.Date, LEAF),
                                    new ClassificationOverviewNode(FeatureTypeTree.Percentage, LEAF),
                                    new ClassificationOverviewNode(FeatureTypeTree.Email, LEAF),
                            }),

                    }),

            });


    /*************************************************************************
     *
     *          Lookup traversal
     *
     * @param tag
     * @return
     *
     *          //TODO: Create iterator to loop over
     *          //TODO: Optimization: look over this lookup. It is quite inefficient
     */


    public static ClassificationOverviewNode getNodeForTag(String tag){

        return getNodeForTag(root, tag);

    }


    public static ClassificationOverviewNode getNodeForTag(ClassificationOverviewNode node,  String tag){

        if(node.type.getName().equals(tag))
            return node;

        for (ClassificationOverviewNode child : node.children) {

            ClassificationOverviewNode found = getNodeForTag(child, tag);
            if(found != null)
                return found;

        }

        return null;


    }

}
