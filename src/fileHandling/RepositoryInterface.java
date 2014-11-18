package fileHandling;

import pukkaBO.exceptions.BackOfficeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2014-09-03
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */
public interface RepositoryInterface {

    @Deprecated
    RepositoryFileHandler getFileHandler(String fileName, HttpServletRequest req);

    @Deprecated
    void serveFile(RepositoryFileHandler fileHandler, HttpServletResponse res) throws IOException;

    boolean existsFile(RepositoryFileHandler fileHandler);

    RepositoryFileHandler getEmptyFileHandler();

    InputStream getInputStream(RepositoryFileHandler fileHandler) throws IOException;
    OutputStream getOutputStream(RepositoryFileHandler fileHandler) throws IOException;

    RepositoryFileHandler saveFile(String fileName, InputStream uploaded) throws BackOfficeException;

    public String getBucket();

}
