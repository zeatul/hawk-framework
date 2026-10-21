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
import java.util.concurrent.atomic.AtomicLong;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argument;


/**
 * {@link AbstractSnowflakeIdGenerator} provides a skeletal implementation of well-known snowflake algorithm.
 * <p>Optimized version: uses CAS (AtomicLong) for lock-free sequence increment, locking only on clock rollback.</p>
 *
 * @author Leavy
 * @author Zhang Peng
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
    // the maximum of timestamp segment
    private BigInteger maxMillisecondOffset;
    // initial system millis
    private long initialSystemMillis;

    // Use AtomicLong for lock-free concurrency: the high 48 bits store the timestamp, the low 16 bits store the sequence
    // This allows atomically updating timestamp+sequence with a single CAS operation
    private final AtomicLong timestampAndSequence = new AtomicLong();

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

        // Initialize timestampAndSequence
        long initialTimestamp = System.currentTimeMillis() - initialSystemMillis;
        timestampAndSequence.set(initialTimestamp << sequenceBits);

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
        maxMillisecondOffset = BigInteger.ONE.shiftLeft((int) millisecondBits).subtract(BigInteger.ONE);
        leftShiftBitsForMachineId = sequenceBits;
        leftShiftBitsForDataCenterId = leftShiftBitsForMachineId + machineIdBits;
        leftShiftBitsForTimestamp = leftShiftBitsForDataCenterId + dataCenterIdBits;
        initialSystemMillis = initialDateOfSnowflakeId.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Nonnull
    @Override
    public T next() {
        while (true) {
            long currentMillis = System.currentTimeMillis();
            long timestampOffset = currentMillis - initialSystemMillis;

            // Check for timestamp overflow
            if (timestampOffset < 0) {
                throw new IdGeneratingException("time clock moved back");
            }
            BigInteger millisecondOffset = BigInteger.valueOf(timestampOffset);
            if (millisecondOffset.compareTo(maxMillisecondOffset) > 0) {
                throw new IdGeneratingException("The timestamp segment of the snowflake id has overflowed, the offset from the initial date exceeds the available " + millisecondBits + " bits.");
            }

            long current = timestampAndSequence.get();
            long currentTimestamp = current >> sequenceBits;
            long currentSequence = current & maxSequenceInMills;

            long newTimestamp;
            long newSequence;

            if (timestampOffset == currentTimestamp) {
                // Within the same millisecond, try to increment the sequence
                if (currentSequence >= maxSequenceInMills) {
                    // The sequence is exhausted; wait for the next millisecond
                    // Use a spin wait to avoid long blocking
                    continue;
                }
                newTimestamp = currentTimestamp;
                newSequence = currentSequence + 1;
            } else if (timestampOffset > currentTimestamp) {
                // New millisecond, reset the sequence to 0
                newTimestamp = timestampOffset;
                newSequence = 0;
            } else {
                // timestampOffset < currentTimestamp: another thread has already advanced the timestamp, the local clock lags slightly
                // Do not throw; just retry (spin), to avoid false "clock rollback" during multithreaded contention
                continue;
            }

            long newValue = (newTimestamp << sequenceBits) | newSequence;
            if (timestampAndSequence.compareAndSet(current, newValue)) {
                // CAS succeeded, build the ID
                BigInteger b = millisecondOffset.shiftLeft((int) leftShiftBitsForTimestamp) // timestamp segment
                    .or(BigInteger.valueOf(dataCenterId).shiftLeft((int) leftShiftBitsForDataCenterId))// data center id segment
                    .or(BigInteger.valueOf(machineId).shiftLeft((int) leftShiftBitsForMachineId))// machine id segment
                    .or(BigInteger.valueOf(newSequence));// sequence segment
                return convert(b);
            }
            // CAS failed, retry
        }
    }

    protected abstract T convert(BigInteger b);

    @Nonnull
    @Override
    public List<T> next(int count) {
        argument(count, c -> c > 0, c -> "count should be greater than 0");
        List<T> idList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            idList.add(next());
        }
        return idList;
    }


}
