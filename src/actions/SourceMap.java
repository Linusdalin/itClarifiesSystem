package actions;

import databaseLayer.DBKeyInterface;

/**
* Created with IntelliJ IDEA.
* User: Linus
* Date: 2015-02-17
* Time: 09:38
* To change this template use File | Settings | File Templates.
*/
class SourceMap {


    protected final DBKeyInterface fragmentKey;
    protected final String sourceText;
    protected final String itemName;

    SourceMap(DBKeyInterface checklistItem, String sourceText, String itemName){

        this.fragmentKey = checklistItem;
        this.sourceText = sourceText;
        this.itemName = itemName;
    }
}
