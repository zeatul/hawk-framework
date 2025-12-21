package glz.hawkframework.core.id.snowflake;

import java.math.BigInteger;

/**
 * {@link SnowflakeIdGeneratorForBigInteger} is the subclass for {@code SnowflakeIdGenerator<T>},
 * with the specific java type: BigInteger.
 * Snowflake id for BigInteger is a 96-bit number,
 * if you want to store id into a database, make sure the data type should be
 * NUMERIC(30,0) or larger, or, you will encounter problems sooner or later.
 * Then you must modify the length of the column.
 * The id can be divided into five segments:
 * Segment 1: fixed for sign bit, Spares 1-bit.
 * Configured manually is not allowed.
 * Segment 2: the number of milliseconds, default set to spare 44-bit (more than 500 years).
 * Segment 3: data center id, default set to spare 3-bit (8 at most).
 * Segment 4: machine id, default set to spare 32-bit (could store ipv4 as a long value, 256^4).
 * Segment 5: sequence in a millisecond, default set to 16-bit (65536 at most).
 *
 * @author Leavy
 * @author Hawk
 */
public class SnowflakeIdGeneratorForBigInteger extends AbstractSnowflakeIdGenerator<BigInteger> {


    private static final Integer SNOWFLAKE_ID_LENGTH = 96;
    private static final Integer MODIFIABLE_SEGMENTS_LENGTH = SNOWFLAKE_ID_LENGTH - 1;


    public SnowflakeIdGeneratorForBigInteger(SnowflakeIdConfig config) {
        super(config);
    }

    @Override
    protected BigInteger convert(BigInteger b) {
        return b;
    }

    @Override
    protected int getModifiableSnowflakeIdSegmentLength() {
        return MODIFIABLE_SEGMENTS_LENGTH;
    }

}
