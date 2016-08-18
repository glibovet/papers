package ua.com.papers.utils.exceptions;

/**
 * Created by Andrii on 18.08.2016.
 */
public class NoSuchEntityException extends Exception{

    private static final String DEFAULT_MESSAGE = "No entity of type %s and params %s";
    private static final String NO_ENTITY_OF_TYPE = "No entity of type %s";

    public NoSuchEntityException(String className){
        super(String.format(NO_ENTITY_OF_TYPE , className));
    }
    public NoSuchEntityException(String className, String params){
        super(String.format(DEFAULT_MESSAGE, className, params));
    }

}
