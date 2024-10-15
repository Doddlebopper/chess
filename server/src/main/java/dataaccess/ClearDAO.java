package dataaccess;

public interface ClearDAO {
    void clearAuthDAO() throws DataAccessException;
    void clearUserDAO() throws DataAccessException;
    void clearGameDAO() throws DataAccessException;
    void clearAll() throws DataAccessException;
}
