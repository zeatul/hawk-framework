package glz.hawkframework.core.id.snowflake;

import java.math.BigInteger;

/**
 * {@link SnowflakeIdGeneratorForLong} is the subclass for {@code SnowflakeIdGenerator<T>} with the specific java type: Long.
 * Snowflake id for Long is a 64-bit number, if you want to store id into a database, make sure the data type should be
 * NUMERIC(19,0) or larger, or, you will encounter problems a year or two. Then you must modify the length of the column.
 * The id can be divided into five segments:
 * Segment 1: fixed for sign bit. Spares 1-bit. Configured manually is not allowed.
 * Segment 2: milliseconds number, default set to spare 41-bit.
 * Segment 3: data center id, default set to spare 2-bit.
 * Segment 4: machine id, default set to spare 9-bit.
 * Segment 5: sequence in a millisecond, default set to 11-bit.
 * We may not change the bit number of segment 1.
 * However, sometimes we may modify the bit number of segment 2,3,4 and 5 to get a better composition.
 * Remember, the length of this composite is 63.
 * Notice that the default value of segment 2 indicates the id will use up 69 years(2^41/60/60/24/365) later.
 * If your system exists for more than 100 years, You should use {@code SnowflakeIdGeneratorForBigInteger}
 * Or else, you will encounter an uncomfortable experience.
 * @author Leavy
 * @author Hawk
 */
public class SnowflakeIdGeneratorForLong extends AbstractSnowflakeIdGenerator<Long> {

    private static final Integer SNOWFLAKE_ID_LENGTH = 64;
    private static final Integer MODIFIABLE_SEGMENTS_LENGTH = SNOWFLAKE_ID_LENGTH - 1;

    public SnowflakeIdGeneratorForLong(SnowflakeIdConfig config) {
        super(config);
    }

    @Override
    protected Long convert(BigInteger b) {
        return b.longValue();
    }

    @Override
    protected int getModifiableSnowflakeIdSegmentLength() {
        return MODIFIABLE_SEGMENTS_LENGTH;
    }

}
