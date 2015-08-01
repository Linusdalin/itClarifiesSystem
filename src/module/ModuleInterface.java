package module;

import java.util.List;

/***************************************************************'
 *
 *              Generic interface for all modules
 */

public interface ModuleInterface {

    // Find the module
    ModuleNode getNodeForTag(String tag);


    //Get all children as a list. (Exporting all children)
    List<String> getChildren(ModuleNode node);

}

