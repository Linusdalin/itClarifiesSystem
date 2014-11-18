package contractManagement;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2014-02-15
 * Time: 10:38
 * To change this template use File | Settings | File Templates.
 */
public interface DocumentInterface {

    String getHtml( String filter);
    String getCSSFile();
    String getScriptFile();
}
