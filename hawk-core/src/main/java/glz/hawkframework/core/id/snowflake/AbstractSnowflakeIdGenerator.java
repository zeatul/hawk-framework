package glz.hawkframework.core.id.snowflake;

import glz.hawkframework.core.id.IdGeneratingException;
import glz.hawkframework.core.id.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argument;


/**
 * {@link AbstractSnowflakeIdGenerator} provides a skeletal implementation of well-known snowflake algorithm.
 *
 * @author Leavy
 * @author Hawk
 */
public abstract class AbstractSnowflakeIdGenerator<T> implements IdGenerator<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSnowflakeIdGenerator.class);
    private static final Integer SEGMENT_MAX_BITS = 64; // each segment is not allowed to beyond long type range

    private final long dataCenterId;
    private final long machineId;
    private final LocalDate initialDateOfSnowflakeId;
    private final long dataCenterIdBits;
    private final long machineIdBits;
    private final long sequenceBits;
    private final long millisecondBits;

    // the maximum of sequence
    private long maxSequenceInMills;
    // left shift bits needed for machine id part
    private long leftShiftBitsForMachineId;
    // left shift bits needed for data center id part
    private long leftShiftBitsForDataCenterId;
    // left shift bits needed for timestamp id part
    private long leftShiftBitsForTimestamp;
    // current sequence number inner the current millisecond
    private long currentSequenceOfMillis;
    // last millisecond used
    private long lastMillis;
    // initial system millis
    private long initialSystemMillis;

    public AbstractSnowflakeIdGenerator(SnowflakeIdConfig config) {
        argNotNull(config, "config");

        this.dataCenterId = config.getDataCenterId();
        this.machineId = config.getMachineId();
        this.initialDateOfSnowflakeId = argNotNull(config.getInitialDateOfSnowflakeId(), "config's initialDateOfSnowflakeId");
        argument(config.getInitialDateOfSnowflakeId(),
            initialDateOfSnowflakeId -> initialDateOfSnowflakeId.isBefore(LocalDate.now()),
            initialDateOfSnowflakeId -> "The initialDateOfSnowflakeId should be a date before today");

        argument(config.getMillisecondBits(), segmentBits -> segmentBits < SEGMENT_MAX_BITS && segmentBits > 0,
            segmentBits -> "each segment length must be between 1 and " + SEGMENT_MAX_BITS);
        argument(config.getMachineIdBits(), segmentBits -> segmentBits < SEGMENT_MAX_BITS && segmentBits > 0,
            segmentBits -> "each segment is not allowed to beyond " + SEGMENT_MAX_BITS);
        argument(config.getDataCenterIdBits(), segmentBits -> segmentBits < SEGMENT_MAX_BITS && segmentBits > 0,
            segmentBits -> "each segment is not allowed to beyond " + SEGMENT_MAX_BITS);
        argument(config.getSequenceBits(), segmentBits -> segmentBits < SEGMENT_MAX_BITS && segmentBits > 0,
            segmentBits -> "each segment is not allowed to beyond " + SEGMENT_MAX_BITS);
        this.dataCenterIdBits = config.getDataCenterIdBits();
        this.machineIdBits = config.getMachineIdBits();
        this.sequenceBits = config.getSequenceBits();
        this.millisecondBits = config.getMillisecondBits();

        initializeSnowFlakeIdBits();

        LOG.atInfo().log("SnowflakeIdGenerator basic config: dataCenterId = {}, machineId = {}, initialDateOfSnowflakeId = {}",
            dataCenterId, machineId, initialDateOfSnowflakeId);
        LOG.atInfo().log("SnowflakeIdGenerator advanced config: dataCenterIdBits = {}, machineIdBits = {}, sequenceBits = {}, millisecondBits = {}",
            dataCenterIdBits, machineIdBits, sequenceBits, millisecondBits);
    }

    protected abstract int getModifiableSnowflakeIdSegmentLength();

    private void initializeSnowFlakeIdBits() {

        int modifiableSnowflakeIdSegmentLength = getModifiableSnowflakeIdSegmentLength();

        argument(machineIdBits + dataCenterIdBits + sequenceBits + millisecondBits,
            totalBits -> totalBits == modifiableSnowflakeIdSegmentLength,
            totalBits -> "the sum of machineIdBits, dataCenterIdBits, sequenceBits and millisecondBits should be " + modifiableSnowflakeIdSegmentLength);
        long maxDataCenterId = ~(-1L << dataCenterIdBits);
        argument(dataCenterId, dataCenterId -> dataCenterId >= 0 && dataCenterId <= maxDataCenterId,
            dataCenterId -> "data center id should be between 0 and " + maxDataCenterId);
        long maxMachineId = ~(-1L << machineIdBits);
        argument(machineId, machineId -> machineId >= 0 && machineId <= maxMachineId,
            machineId -> "machine id should be between 0 and " + maxMachineId);
        maxSequenceInMills = ~(-1L << sequenceBits);
        leftShiftBitsForMachineId = sequenceBits;
        leftShiftBitsForDataCenterId = leftShiftBitsForMachineId + machineIdBits;
        leftShiftBitsForTimestamp = leftShiftBitsForDataCenterId + dataCenterIdBits;
        currentSequenceOfMillis = 0L;
        lastMillis = System.currentTimeMillis();
        initialSystemMillis = initialDateOfSnowflakeId.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Nonnull
    @Override
    public synchronized T next() {
        long currentMillis = System.currentTimeMillis();
        if (currentMillis == lastMillis) {
            // increment and compare with maxSequenceInMills, and get 0L if equals to maxSequenceInMills
            currentSequenceOfMillis = currentSequenceOfMillis + 1;
            if (currentSequenceOfMillis == maxSequenceInMills) {
                currentSequenceOfMillis = 0L;
                // when arrives the max sequence in current millis, borrow an available millisecond from the future
                while (currentMillis <= lastMillis) {
                    currentMillis = System.currentTimeMillis();
                }
            }
        } else if (currentMillis > lastMillis) {
            // start with 0 if in a different millisecond
            currentSequenceOfMillis = 0L;
        } else {
            throw new IdGeneratingException("time clock moved back");
        }
        lastMillis = currentMillis;
        BigInteger b = BigInteger.valueOf((currentMillis - initialSystemMillis)).shiftLeft((int) leftShiftBitsForTimestamp) // timestamp segment
            .or(BigInteger.valueOf(dataCenterId).shiftLeft((int) leftShiftBitsForDataCenterId))// data center id segment
            .or(BigInteger.valueOf(machineId).shiftLeft((int) leftShiftBitsForMachineId))// machine id segment
            .or(BigInteger.valueOf(currentSequenceOfMillis));// sequence segment
        return convert(b);
    }

    protected abstract T convert(BigInteger b);

    @Nonnull
    @Override
    public synchronized List<T> next(int count) {
        argument(count, c -> c > 0, c -> "count should be greater than 0");
        List<T> idList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            idList.add(next());
        }
        return idList;
    }


}
