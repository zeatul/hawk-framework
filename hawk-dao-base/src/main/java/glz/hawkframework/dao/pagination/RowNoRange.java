package glz.hawkframework.dao.pagination;

/**
 * This interface describes the paging params for oracle
 *
 * @author Hawk
 */
public interface RowNoRange {

    /**
     * minRowNo start from 1
     * @return
     */
    long getMinRowNo();

    /**
     * maxRowNo must be greater than minRowNo
     * @return
     */
    long getMaxRowNo();

}
