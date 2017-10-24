package ua.com.papers.utils;

import javax.validation.constraints.NotNull;

/**
 * <p>
 *     Can be used for async calls
 * </p>
 * Created by Максим on 10/21/2017.
 */
public interface ResultCallback<T> {

    default void onResult(@NotNull T t) {}

    default void onException(@NotNull Exception e) {}

}
