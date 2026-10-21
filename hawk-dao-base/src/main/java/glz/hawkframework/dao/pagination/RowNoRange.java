package glz.hawkframework.dao.pagination;

/**
 * This interface describes the paging params for oracle
 *
 * @author Zhang Peng
 */
public interface RowNoRange {

    /**
     * minRowNo start from 1
     * @return
     */
    long getMinRowNo();

    /**
     * maxRowNo must be greater than or equal to minRowNo
     * @return
     */
    long getMaxRowNo();

}
