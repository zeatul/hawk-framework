package glz.hawkframework.core.id.snowflake;

import glz.hawkframework.core.helper.IpHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * test the class {@link SnowflakeIdGeneratorForBigInteger}
 * @author Leavy
 */

public class SnowflakeIdGeneratorForBigIntegerTest {

    /**
     * the following default values could be an important reference for user to config
     */
    private static final long DEFAULT_DATA_CENTER_ID = 1;
    private static final long DEFAULT_MACHINE_ID = IpHelper.ipv4ToLong("128.192.38.38");
    private static final LocalDate DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID = LocalDate.of(2024,11,19);
    private static final long DEFAULT_DATA_CENTER_ID_BITS = 3;
    private static final long DEFAULT_MACHINE_ID_BITS = 32;
    private static final long DEFAULT_SEQUENCE_BITS = 16;
    private static final long DEFAULT_MILLISECOND_BITS = 44;

    private SnowflakeIdGeneratorForBigInteger defaultSnowflakeIdGeneratorForBigInteger;
    private SnowflakeIdConfig defaultSnowflakeIdConfig;

    @BeforeEach
    void setup(){
        defaultSnowflakeIdConfig = SnowflakeIdConfig.builder()
            .setDataCenterId(DEFAULT_DATA_CENTER_ID)
            .setMachineId(DEFAULT_MACHINE_ID)
            .setInitialDateOfSnowflakeId(DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID)
            .setDataCenterIdBits(DEFAULT_DATA_CENTER_ID_BITS)
            .setMachineIdBits(DEFAULT_MACHINE_ID_BITS)
            .setSequenceBits(DEFAULT_SEQUENCE_BITS)
            .setMillisecondBits(DEFAULT_MILLISECOND_BITS)
            .setExpectedTotalBits(95)
            .build();
        defaultSnowflakeIdGeneratorForBigInteger = new SnowflakeIdGeneratorForBigInteger(defaultSnowflakeIdConfig);
    }

    @Test
    void shouldBeOkWhenGenerateSingleIdIfUsingDefaultBean(){
        BigInteger id = defaultSnowflakeIdGeneratorForBigInteger.next();
        //id should be a positive number
        assertThat(id).isPositive();
        //id should be less than 30-digit
        assertThat(id.toString().length()).isLessThan(30);
    }

    @Test
    void testHashSetRemoveDuplicateForBigInteger(){
        List<BigInteger> idList = new ArrayList<>();
        idList.add(BigInteger.TEN);
        idList.add(BigInteger.TEN);
        idList.add(BigInteger.TEN);
        Set<BigInteger> idSet = new HashSet<>(idList);
        assertThat(idSet).hasSize(1);
    }

    @Test
    void shouldBeOkWhenGenerateBatchOfIdsIfUsingDefaultBean(){
        assertThatIllegalArgumentException().isThrownBy(()->defaultSnowflakeIdGeneratorForBigInteger.next(0));
        int size = 9999;
        List<BigInteger> idList = defaultSnowflakeIdGeneratorForBigInteger.next(size);
        //id list should have exact number of id
        assertThat(idList).hasSize(size);
        //and should not have duplicated values
        Set<BigInteger> idSet = new HashSet<>(idList);
        assertThat(idSet).hasSize(size);
    }

    @Test
    void shouldThrowExceptionsWhenGenerateIdIfInvalidConfig(){
        // data center id beyond allowed range
        assertThatIllegalArgumentException().isThrownBy(() ->
            new SnowflakeIdGeneratorForBigInteger(SnowflakeIdConfig.builder()
                .setDataCenterId(5)
                .setDataCenterIdBits(2)
                .setMachineIdBits(DEFAULT_MACHINE_ID_BITS)
                .setSequenceBits(DEFAULT_SEQUENCE_BITS)
                .setMillisecondBits(DEFAULT_MILLISECOND_BITS)
                .setInitialDateOfSnowflakeId(DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID)
                .build()).next());

        // machine id beyond allowed range
        assertThatIllegalArgumentException().isThrownBy(() ->
            new SnowflakeIdGeneratorForBigInteger(SnowflakeIdConfig.builder()
                .setDataCenterId(DEFAULT_DATA_CENTER_ID)
                .setDataCenterIdBits(11)
                .setMachineId(2500)
                .setMachineIdBits(DEFAULT_MACHINE_ID_BITS)
                .setSequenceBits(DEFAULT_SEQUENCE_BITS)
                .setMillisecondBits(DEFAULT_MILLISECOND_BITS)
                .setInitialDateOfSnowflakeId(DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID)
                .build()).next());

        // initial date before today
        assertThatIllegalArgumentException().isThrownBy(() ->
            new SnowflakeIdGeneratorForBigInteger(SnowflakeIdConfig.builder()
                .setDataCenterId(DEFAULT_DATA_CENTER_ID)
                .setDataCenterIdBits(DEFAULT_DATA_CENTER_ID_BITS)
                .setMachineId(DEFAULT_MACHINE_ID)
                .setMachineIdBits(DEFAULT_MACHINE_ID_BITS)
                .setSequenceBits(DEFAULT_SEQUENCE_BITS)
                .setMillisecondBits(DEFAULT_MILLISECOND_BITS)
                .setInitialDateOfSnowflakeId(LocalDate.now().plusDays(1))
                .build()).next());

        // each segment length less than 0
        assertThatIllegalArgumentException().isThrownBy(() ->
            new SnowflakeIdGeneratorForBigInteger(SnowflakeIdConfig.builder()
                .setDataCenterId(DEFAULT_DATA_CENTER_ID)
                .setDataCenterIdBits(DEFAULT_DATA_CENTER_ID_BITS)
                .setMachineId(DEFAULT_MACHINE_ID)
                .setMachineIdBits(DEFAULT_MACHINE_ID_BITS)
                .setSequenceBits(-1)
                .setMillisecondBits(DEFAULT_MILLISECOND_BITS)
                .setInitialDateOfSnowflakeId(DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID)
                .build()).next());
        assertThatIllegalArgumentException().isThrownBy(() ->
            new SnowflakeIdGeneratorForBigInteger(SnowflakeIdConfig.builder()
                .setDataCenterId(DEFAULT_DATA_CENTER_ID)
                .setDataCenterIdBits(-1)
                .setMachineId(DEFAULT_MACHINE_ID)
                .setMachineIdBits(DEFAULT_MACHINE_ID_BITS)
                .setSequenceBits(DEFAULT_SEQUENCE_BITS)
                .setMillisecondBits(DEFAULT_MILLISECOND_BITS)
                .setInitialDateOfSnowflakeId(DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID)
                .build()).next());
        assertThatIllegalArgumentException().isThrownBy(() ->
            new SnowflakeIdGeneratorForBigInteger(SnowflakeIdConfig.builder()
                .setDataCenterId(DEFAULT_DATA_CENTER_ID)
                .setDataCenterIdBits(DEFAULT_DATA_CENTER_ID_BITS)
                .setMachineId(DEFAULT_MACHINE_ID)
                .setMachineIdBits(-1)
                .setSequenceBits(DEFAULT_SEQUENCE_BITS)
                .setMillisecondBits(DEFAULT_MILLISECOND_BITS)
                .setInitialDateOfSnowflakeId(DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID)
                .build()).next());
        assertThatIllegalArgumentException().isThrownBy(() ->
            new SnowflakeIdGeneratorForBigInteger(SnowflakeIdConfig.builder()
                .setDataCenterId(DEFAULT_DATA_CENTER_ID)
                .setDataCenterIdBits(DEFAULT_DATA_CENTER_ID_BITS)
                .setMachineId(DEFAULT_MACHINE_ID)
                .setMachineIdBits(DEFAULT_MACHINE_ID_BITS)
                .setSequenceBits(DEFAULT_SEQUENCE_BITS)
                .setMillisecondBits(-1)
                .setInitialDateOfSnowflakeId(DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID)
                .build()).next());

        // the sum of each segment length should be 63
        assertThatIllegalArgumentException().isThrownBy(() ->
            new SnowflakeIdGeneratorForBigInteger(SnowflakeIdConfig.builder()
                .setDataCenterId(DEFAULT_DATA_CENTER_ID)
                .setDataCenterIdBits(1)
                .setMachineId(DEFAULT_MACHINE_ID)
                .setMachineIdBits(1)
                .setSequenceBits(1)
                .setMillisecondBits(1)
                .setInitialDateOfSnowflakeId(DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID)
                .build()).next());
    }

    @Test
    void shouldHaveNoDuplicatedIdWhenMultiThreadGenerateId(){
        class SnowflakeIdGenerateTask extends RecursiveTask<List<BigInteger>>{

            private static final int THRESHOLD = 10000;
            private final SnowflakeIdGeneratorForBigInteger generatorForBigInteger;
            private final int num;
            private final List<BigInteger> idList;

            SnowflakeIdGenerateTask(SnowflakeIdGeneratorForBigInteger generatorForBigInteger, int num) {
                this.generatorForBigInteger = generatorForBigInteger;
                this.num = num;
                idList = new ArrayList<>(num);
            }

            @Override
            protected List<BigInteger> compute() {
                if(num  <= THRESHOLD) {
                    return processing(num);
                } else {
                    return ForkJoinTask.invokeAll(createSubtasks()).stream()
                            .map(ForkJoinTask::join)
                            .reduce(new ArrayList<>(), (partialList, element) -> {partialList.addAll(element); return partialList;});
                }
            }

            private Collection<SnowflakeIdGenerateTask> createSubtasks() {
                int size = num / THRESHOLD;
                int remainder = num % THRESHOLD;
                List<SnowflakeIdGenerateTask> dividedTasks = new ArrayList<>(size);
                for(int i = 0; i < size; i++){
                    dividedTasks.add(new SnowflakeIdGenerateTask(defaultSnowflakeIdGeneratorForBigInteger, THRESHOLD));
                }
                if(remainder > 0){
                    dividedTasks.add(new SnowflakeIdGenerateTask(defaultSnowflakeIdGeneratorForBigInteger, remainder));
                }
                return dividedTasks;
            }

            private List<BigInteger> processing(int num) {
                return defaultSnowflakeIdGeneratorForBigInteger.next(num);
            }
        }

        int numberOfProcessors = Runtime.getRuntime().availableProcessors();
        ForkJoinPool forkJoinPool = new ForkJoinPool(numberOfProcessors);
        int size = 1000000;
        SnowflakeIdGenerateTask task = new SnowflakeIdGenerateTask(defaultSnowflakeIdGeneratorForBigInteger, size);
        List<BigInteger> idList = forkJoinPool.invoke(task);
        assertThat(idList).hasSize(size);
        Set<BigInteger> idSet = new HashSet<>(idList);
        assertThat(idSet).hasSize(size);

        //mock 2 different machines with different machine id, and expected no duplicated value
        SnowflakeIdGenerateTask task1 = new SnowflakeIdGenerateTask(defaultSnowflakeIdGeneratorForBigInteger, size);
        SnowflakeIdConfig configOfAnotherMachine = SnowflakeIdConfig.builder()
            .setDataCenterId(DEFAULT_DATA_CENTER_ID)
            .setDataCenterIdBits(DEFAULT_DATA_CENTER_ID_BITS)
            .setMachineId(2)
            .setMachineIdBits(DEFAULT_MACHINE_ID_BITS)
            .setSequenceBits(DEFAULT_SEQUENCE_BITS)
            .setMillisecondBits(DEFAULT_MILLISECOND_BITS)
            .setInitialDateOfSnowflakeId(DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID)
            .setExpectedTotalBits(95)
            .build();
        SnowflakeIdGenerateTask task2 = new SnowflakeIdGenerateTask(new SnowflakeIdGeneratorForBigInteger(configOfAnotherMachine), size);
        ForkJoinPool forkJoinPool1 = new ForkJoinPool(numberOfProcessors/2);
        ForkJoinPool forkJoinPool2 = new ForkJoinPool(numberOfProcessors/2);
        List<BigInteger> idList1 = forkJoinPool1.invoke(task1);
        List<BigInteger> idList2 = forkJoinPool2.invoke(task2);
        idList1.addAll(idList2);
        assertThat(idList1).hasSize(size*2);
        assertThat(new HashSet<>(idList1)).hasSize(size * 2);
        idList1.forEach(id-> assertThat(id).isPositive());
    }
}
