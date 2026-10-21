package glz.hawkframework.core.id.snowflake;

import java.time.LocalDate;

/**
 * {@link SnowflakeIdConfig} defines the configuration for snowflake id generator
 * <p>Built using the Builder pattern, with pre-validation performed at build time</p>
 *
 * @author Leavy
 */
public class SnowflakeIdConfig {

    /**
     * data center id
     */
    private long dataCenterId;

    /**
     * machine id
     */
    private long machineId;

    /**
     * initial date of snowflake id
     */
    private LocalDate initialDateOfSnowflakeId;

    /**
     * how many bits to store a data center id
     */
    private long dataCenterIdBits;

    /**
     * how many bits to store machine id
     */
    private long machineIdBits;

    /**
     * how many bits to store a sequence
     */
    private long sequenceBits;

    /**
     * how many bits to store the milliseconds from the initial system millisecond
     */
    private long millisecondBits;

    private SnowflakeIdConfig() {}

    public long getDataCenterId() {
        return dataCenterId;
    }

    public long getMachineId() {
        return machineId;
    }

    public LocalDate getInitialDateOfSnowflakeId() {
        return initialDateOfSnowflakeId;
    }

    public long getDataCenterIdBits() {
        return dataCenterIdBits;
    }

    public long getMachineIdBits() {
        return machineIdBits;
    }

    public long getSequenceBits() {
        return sequenceBits;
    }

    public long getMillisecondBits() {
        return millisecondBits;
    }

    /**
     * Creates a Builder instance.
     * <p>The default total bit count is 63 (Long version); it can be customized via {@link Builder#setExpectedTotalBits(long)} (e.g. 95 for the BigInteger version)</p>
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder pattern, with pre-validation performed at build time.
     */
    public static class Builder {
        private long dataCenterId = 1;
        private long machineId = 1;
        private LocalDate initialDateOfSnowflakeId = LocalDate.of(2024, 1, 1);
        private long dataCenterIdBits = 2;
        private long machineIdBits = 9;
        private long sequenceBits = 11;
        private long millisecondBits = 41;
        // The expected total number of bits (excluding the sign bit), Long=63, BigInteger=95
        private long expectedTotalBits = 63;

        public Builder setDataCenterId(long dataCenterId) {
            this.dataCenterId = dataCenterId;
            return this;
        }

        public Builder setMachineId(long machineId) {
            this.machineId = machineId;
            return this;
        }

        public Builder setInitialDateOfSnowflakeId(LocalDate initialDateOfSnowflakeId) {
            this.initialDateOfSnowflakeId = initialDateOfSnowflakeId;
            return this;
        }

        public Builder setDataCenterIdBits(long dataCenterIdBits) {
            this.dataCenterIdBits = dataCenterIdBits;
            return this;
        }

        public Builder setMachineIdBits(long machineIdBits) {
            this.machineIdBits = machineIdBits;
            return this;
        }

        public Builder setSequenceBits(long sequenceBits) {
            this.sequenceBits = sequenceBits;
            return this;
        }

        public Builder setMillisecondBits(long millisecondBits) {
            this.millisecondBits = millisecondBits;
            return this;
        }

        /**
         * Sets the expected total number of bits (excluding the sign bit).
         * <p>63 for the Long version, 95 for the BigInteger version</p>
         */
        public Builder setExpectedTotalBits(long expectedTotalBits) {
            this.expectedTotalBits = expectedTotalBits;
            return this;
        }

        public SnowflakeIdConfig build() {
            // Pre-validation
            validate();

            SnowflakeIdConfig config = new SnowflakeIdConfig();
            config.dataCenterId = dataCenterId;
            config.machineId = machineId;
            config.initialDateOfSnowflakeId = initialDateOfSnowflakeId;
            config.dataCenterIdBits = dataCenterIdBits;
            config.machineIdBits = machineIdBits;
            config.sequenceBits = sequenceBits;
            config.millisecondBits = millisecondBits;
            config.setExpectedTotalBits(expectedTotalBits);
            return config;
        }

        private void validate() {
            // Validate the bit count ranges
            if (dataCenterIdBits <= 0 || dataCenterIdBits >= 64) {
                throw new IllegalArgumentException("dataCenterIdBits must be between 1 and 63");
            }
            if (machineIdBits <= 0 || machineIdBits >= 64) {
                throw new IllegalArgumentException("machineIdBits must be between 1 and 63");
            }
            if (sequenceBits <= 0 || sequenceBits >= 64) {
                throw new IllegalArgumentException("sequenceBits must be between 1 and 63");
            }
            if (millisecondBits <= 0 || millisecondBits >= 64) {
                throw new IllegalArgumentException("millisecondBits must be between 1 and 63");
            }

            // Validate the total bit count
            long totalBits = dataCenterIdBits + machineIdBits + sequenceBits + millisecondBits;
            if (totalBits != expectedTotalBits) {
                throw new IllegalArgumentException("Sum of dataCenterIdBits, machineIdBits, sequenceBits and millisecondBits must be " + expectedTotalBits + ", but got " + totalBits);
            }

            // Validate the dataCenterId range
            long maxDataCenterId = ~(-1L << dataCenterIdBits);
            if (dataCenterId < 0 || dataCenterId > maxDataCenterId) {
                throw new IllegalArgumentException("dataCenterId must be between 0 and " + maxDataCenterId);
            }

            // Validate the machineId range
            long maxMachineId = ~(-1L << machineIdBits);
            if (machineId < 0 || machineId > maxMachineId) {
                throw new IllegalArgumentException("machineId must be between 0 and " + maxMachineId);
            }

            // Validate the initial date
            if (initialDateOfSnowflakeId == null) {
                throw new IllegalArgumentException("initialDateOfSnowflakeId must not be null");
            }
            if (!initialDateOfSnowflakeId.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("initialDateOfSnowflakeId must be a date before today");
            }
        }
    }

    /**
     * Gets the expected total number of bits (used for internal validation by the generator).
     */
    long getExpectedTotalBits() {
        return expectedTotalBits;
    }

    // Builder needs access to private fields; add wrapper methods
    static class ConfigAccessor {
        static long getExpectedTotalBits(SnowflakeIdConfig config) {
            return config.expectedTotalBits;
        }
    }

    private long expectedTotalBits = 63;

    void setExpectedTotalBits(long expectedTotalBits) {
        this.expectedTotalBits = expectedTotalBits;
    }
}
