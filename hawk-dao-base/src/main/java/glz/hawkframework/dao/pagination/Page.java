package glz.hawkframework.dao.pagination;

/**
 * {@link Page} supports pagination query.
 *
 * @author Hawk
 */
public interface Page {

    /**
     * Obtains the index of page. The index starts from {@code 1}.
     *
     * @return the index of page
     */
    long getPageIndex();

    /**
     * Obtains the size of page. It must be greater than zero.
     *
     * @return the size of page
     */
    long getPageSize();

    OffsetAndLimit toOffsetAndLimit();

    RowNoRange toRowNoRage();

}
