package glz.hawkframework.dao.pagination;

/**
 * This interface describes the paging params for mysql
 *
 * @author Hawk
 */
public interface OffsetAndLimit {

    /**
     * offset must be greater than or equal to {@code 0}.
     *
     * @return offset
     */
    long getOffset();

    /**
     * limit must be greater than or equal to {@code 1}.
     *
     * @return limit
     */
    long getLimit();

}
