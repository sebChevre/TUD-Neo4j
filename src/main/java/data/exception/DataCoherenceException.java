package data.exception;

/**
 * Created by seb on 13.08.14.
 */
public class DataCoherenceException extends RuntimeException {

    public DataCoherenceException(String msg){
        super(msg);
    }
}
