package module;

import featureTypes.FeatureTypeInterface;

/*********************************************************************'''
 *
 *      A module Node is a node in a classification tree in a module
 *
 */

public class ModuleNode {

    public FeatureTypeInterface type;
    public ModuleNode[] children;

    ModuleNode(FeatureTypeInterface type, ModuleNode[] children){

        this.type = type;
        this.children = children;
    }


}
