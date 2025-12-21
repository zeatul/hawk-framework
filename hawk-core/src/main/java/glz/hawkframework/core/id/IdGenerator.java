package glz.hawkframework.core.id;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This interface is used to get an id or a list of ids
 *
 * @param <T> T is the type of id, Long, BigInteger, for example
 * @author Hawk
 */
public interface IdGenerator<T> {

    /**
     * Obtains next id.
     * <p>
     * This method must be thread safe.
     *
     * @return the next id (never {@code null})
     */
    @Nonnull
    T next();

    /**
     * Obtains a list of next ids.
     * <p>
     * This method must be thread safe.
     * <P>
     * The size of list is same as the input {@code count}.
     * Throws an exception if count is less than or equal to {@code 0}.
     *
     * @param count the count of required ids
     * @return a list of next ids (never {@code null})
     */
    @Nonnull
    List<T> next(int count);

}
