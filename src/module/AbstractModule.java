package module;

import java.util.ArrayList;
import java.util.List;

/**********************************************************************************
 *
 *                  Base functionality for a module
 */


public abstract class AbstractModule {

    protected static final ModuleNode[] LEAF = new ModuleNode[]{};
    private ModuleNode root;


    /*********************************************************'
     *
     *          Create a module with the static definition (rood)
     *
     *
     * @param root      - static definition from super class
     */

    AbstractModule(ModuleNode root){

        this.root = root;
    }


    /*************************************************************************
     *
     *          Lookup traversal
     *
     * @param tag       - name of tag
     * @return          - the corresponding node in the tree
     *
     */


    public ModuleNode getNodeForTag(String tag){

        return getNodeForTag(root, tag);

    }


    private ModuleNode getNodeForTag(ModuleNode node,  String tag){

        if(node.type.getName().equals(tag))
            return node;

        for (ModuleNode child : node.children) {

            ModuleNode found = getNodeForTag(child, tag);
            if(found != null)
                return found;

        }

        return null;


    }

    /**********************************************************************'
     *
     *          Get all children as a list
     *
     *
     * @param node        - A node
     * @return            - list of the names of all children
     */

    public List<String> getChildren(ModuleNode node) {

        List<String> childTags = new ArrayList<String>();

        if(node == null)
            return childTags;

        for (ModuleNode child : node.children) {

            childTags.add(child.type.getName());
            childTags.addAll(getChildren(child));

        }

        return childTags;


    }

}
