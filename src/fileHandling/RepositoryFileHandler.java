package fileHandling;

/******************************************************************
 *
 *
 *          This is the key to store a reference to the actual file
 *
 */

public class RepositoryFileHandler {

    private String fileName;

    public RepositoryFileHandler(String name) {

        this.fileName = name;
    }


    public String getFileName(){

        return fileName;
    }


}
