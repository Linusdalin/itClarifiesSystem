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

                    new ClassificationOverviewNode(FeatureTypeTree.Requirements, new ClassificationOverviewNode[]{

                            new ClassificationOverviewNode(FeatureTypeTree.RightsAndObligations, new ClassificationOverviewNode[]{

                                    new ClassificationOverviewNode(FeatureTypeTree.Right, LEAF),
                                    new ClassificationOverviewNode(FeatureTypeTree.Restriction, LEAF),

                            }),
                    }),



                    new ClassificationOverviewNode(FeatureTypeTree.Highlight, new ClassificationOverviewNode[]{

                            new ClassificationOverviewNode(FeatureTypeTree.Content, new ClassificationOverviewNode[]{

                                    new ClassificationOverviewNode(FeatureTypeTree.Definition, new ClassificationOverviewNode[]{

                                            new ClassificationOverviewNode(FeatureTypeTree.DefinitionDef, LEAF),
                                            new ClassificationOverviewNode(FeatureTypeTree.DefinitionUsage, LEAF),

                                    }),
                            }),

                            new ClassificationOverviewNode(FeatureTypeTree.Risk, new ClassificationOverviewNode[]{

                                    new ClassificationOverviewNode(FeatureTypeTree.Unspecific, LEAF),
                                    new ClassificationOverviewNode(FeatureTypeTree.Exclusivity, LEAF),
                                    new ClassificationOverviewNode(FeatureTypeTree.Penalties, LEAF),
                                    new ClassificationOverviewNode(FeatureTypeTree.Compensation, LEAF),

                            }),



                            new ClassificationOverviewNode(FeatureTypeTree.Structure, new ClassificationOverviewNode[]{

                                    new ClassificationOverviewNode(FeatureTypeTree.Date, LEAF),
                                    new ClassificationOverviewNode(FeatureTypeTree.Percentage, LEAF),
                                    new ClassificationOverviewNode(FeatureTypeTree.Email, LEAF),
                            }),

                    }),

            });

}
