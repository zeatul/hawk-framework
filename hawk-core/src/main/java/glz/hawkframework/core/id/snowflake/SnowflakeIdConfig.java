package glz.hawkframework.core.id.snowflake;

import java.time.LocalDate;

/**
 * {@link SnowflakeIdConfig} defines the configuration for snowflake id generator
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

    public long getDataCenterId() {
        return dataCenterId;
    }

    public void setDataCenterId(long dataCenterId) {
        this.dataCenterId = dataCenterId;
    }

    public long getMachineId() {
        return machineId;
    }

    public void setMachineId(long machineId) {
        this.machineId = machineId;
    }

    public LocalDate getInitialDateOfSnowflakeId() {
        return initialDateOfSnowflakeId;
    }

    public void setInitialDateOfSnowflakeId(LocalDate initialDateOfSnowflakeId) {
        this.initialDateOfSnowflakeId = initialDateOfSnowflakeId;
    }

    public long getDataCenterIdBits() {
        return dataCenterIdBits;
    }

    public void setDataCenterIdBits(long dataCenterIdBits) {
        this.dataCenterIdBits = dataCenterIdBits;
    }

    public long getMachineIdBits() {
        return machineIdBits;
    }

    public void setMachineIdBits(long machineIdBits) {
        this.machineIdBits = machineIdBits;
    }

    public long getSequenceBits() {
        return sequenceBits;
    }

    public void setSequenceBits(long sequenceBits) {
        this.sequenceBits = sequenceBits;
    }

    public long getMillisecondBits() {
        return millisecondBits;
    }

    public void setMillisecondBits(long millisecondBits) {
        this.millisecondBits = millisecondBits;
    }
}
