package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class UnauthorizedException extends Exception{
    public UnauthorizedException(String invalidUsernameOrPassword) {

    }
}
