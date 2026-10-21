package glz.hawkframework.core.id.snowflake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * test the class {@link SnowflakeIdGeneratorForLong}
 * @author Leavy
 */

public class SnowflakeIdGeneratorForLongTest {

    /**
     * the following default values could be an important reference for user to config
     */
    private static final int DEFAULT_DATA_CENTER_ID = 1;
    private static final int DEFAULT_MACHINE_ID = 1;
    private static final LocalDate DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID = LocalDate.of(2024,11,19);
    private static final int DEFAULT_DATA_CENTER_ID_BITS = 2;
    private static final int DEFAULT_MACHINE_ID_BITS = 9;
    private static final int DEFAULT_SEQUENCE_BITS = 11;
    private static final int DEFAULT_MILLISECOND_BITS = 41;

    private SnowflakeIdGeneratorForLong defaultSnowflakeIdGeneratorForLong;
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
            .build();
        defaultSnowflakeIdGeneratorForLong = new SnowflakeIdGeneratorForLong(defaultSnowflakeIdConfig);
    }

    @Test
    void shouldBeOkWhenGenerateSingleIdIfUsingDefaultBean(){
        Long id = defaultSnowflakeIdGeneratorForLong.next();
        //id should be a positive number
        assertThat(id).isPositive();
        //id should be between 15-digit and 19-digit
        assertThat(id.toString().length()).isGreaterThan(14);
        assertThat(id.toString().length()).isLessThan(20);
    }

    @Test
    void shouldBeOkWhenGenerateBatchOfIdsIfUsingDefaultBean(){
        assertThatIllegalArgumentException().isThrownBy(()->defaultSnowflakeIdGeneratorForLong.next(0));
        int size = 9999;
        List<Long> idList = defaultSnowflakeIdGeneratorForLong.next(size);
        //id list should have exact number of id
        assertThat(idList).hasSize(size);
        //and should not have duplicated values
        Set<Long> idSet = new HashSet<>(idList);
        assertThat(idSet).hasSize(size);
    }

    @Test
    void shouldThrowExceptionsWhenGenerateIdIfInvalidConfig(){
        // data center id beyond allowed range
        assertThatIllegalArgumentException().isThrownBy(() ->
            new SnowflakeIdGeneratorForLong(SnowflakeIdConfig.builder()
                .setDataCenterId(5)
                .setDataCenterIdBits(2)
                .setMachineIdBits(DEFAULT_MACHINE_ID_BITS)
                .setSequenceBits(DEFAULT_SEQUENCE_BITS)
                .setMillisecondBits(DEFAULT_MILLISECOND_BITS)
                .setInitialDateOfSnowflakeId(DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID)
                .build()).next());

        // machine id beyond allowed range
        assertThatIllegalArgumentException().isThrownBy(() ->
            new SnowflakeIdGeneratorForLong(SnowflakeIdConfig.builder()
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
            new SnowflakeIdGeneratorForLong(SnowflakeIdConfig.builder()
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
            new SnowflakeIdGeneratorForLong(SnowflakeIdConfig.builder()
                .setDataCenterId(DEFAULT_DATA_CENTER_ID)
                .setDataCenterIdBits(DEFAULT_DATA_CENTER_ID_BITS)
                .setMachineId(DEFAULT_MACHINE_ID)
                .setMachineIdBits(DEFAULT_MACHINE_ID_BITS)
                .setSequenceBits(-1)
                .setMillisecondBits(DEFAULT_MILLISECOND_BITS)
                .setInitialDateOfSnowflakeId(DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID)
                .build()).next());
        assertThatIllegalArgumentException().isThrownBy(() ->
            new SnowflakeIdGeneratorForLong(SnowflakeIdConfig.builder()
                .setDataCenterId(DEFAULT_DATA_CENTER_ID)
                .setDataCenterIdBits(-1)
                .setMachineId(DEFAULT_MACHINE_ID)
                .setMachineIdBits(DEFAULT_MACHINE_ID_BITS)
                .setSequenceBits(DEFAULT_SEQUENCE_BITS)
                .setMillisecondBits(DEFAULT_MILLISECOND_BITS)
                .setInitialDateOfSnowflakeId(DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID)
                .build()).next());
        assertThatIllegalArgumentException().isThrownBy(() ->
            new SnowflakeIdGeneratorForLong(SnowflakeIdConfig.builder()
                .setDataCenterId(DEFAULT_DATA_CENTER_ID)
                .setDataCenterIdBits(DEFAULT_DATA_CENTER_ID_BITS)
                .setMachineId(DEFAULT_MACHINE_ID)
                .setMachineIdBits(-1)
                .setSequenceBits(DEFAULT_SEQUENCE_BITS)
                .setMillisecondBits(DEFAULT_MILLISECOND_BITS)
                .setInitialDateOfSnowflakeId(DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID)
                .build()).next());
        assertThatIllegalArgumentException().isThrownBy(() ->
            new SnowflakeIdGeneratorForLong(SnowflakeIdConfig.builder()
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
            new SnowflakeIdGeneratorForLong(SnowflakeIdConfig.builder()
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
        class SnowflakeIdGenerateTask extends RecursiveTask<List<Long>>{

            private static final int THRESHOLD = 10000;
            private final SnowflakeIdGeneratorForLong generatorForLong;
            private final int num;
            private final List<Long> idList;

            SnowflakeIdGenerateTask(SnowflakeIdGeneratorForLong generatorForLong, int num) {
                this.generatorForLong = generatorForLong;
                this.num = num;
                idList = new ArrayList<>(num);
            }

            @Override
            protected List<Long> compute() {
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
                    dividedTasks.add(new SnowflakeIdGenerateTask(defaultSnowflakeIdGeneratorForLong, THRESHOLD));
                }
                if(remainder > 0){
                    dividedTasks.add(new SnowflakeIdGenerateTask(defaultSnowflakeIdGeneratorForLong, remainder));
                }
                return dividedTasks;
            }

            private List<Long> processing(int num) {
                return defaultSnowflakeIdGeneratorForLong.next(num);
            }
        }

        Integer numberOfProcessors = Runtime.getRuntime().availableProcessors();
        ForkJoinPool forkJoinPool = new ForkJoinPool(numberOfProcessors);
        int size = 1000000;
        SnowflakeIdGenerateTask task = new SnowflakeIdGenerateTask(defaultSnowflakeIdGeneratorForLong, size);
        List<Long> idList = forkJoinPool.invoke(task);
        assertThat(idList).hasSize(size);
        Set<Long> idSet = new HashSet<>(idList);
        assertThat(idSet).hasSize(size);

        //mock 2 different machines with different machine id, and expected no duplicated value
        SnowflakeIdGenerateTask task1 = new SnowflakeIdGenerateTask(defaultSnowflakeIdGeneratorForLong, size);
        SnowflakeIdConfig configOfAnotherMachine = SnowflakeIdConfig.builder()
            .setDataCenterId(DEFAULT_DATA_CENTER_ID)
            .setDataCenterIdBits(DEFAULT_DATA_CENTER_ID_BITS)
            .setMachineId(2)
            .setMachineIdBits(DEFAULT_MACHINE_ID_BITS)
            .setSequenceBits(DEFAULT_SEQUENCE_BITS)
            .setMillisecondBits(DEFAULT_MILLISECOND_BITS)
            .setInitialDateOfSnowflakeId(DEFAULT_INITIAL_DATE_OF_SNOWFLAKE_ID)
            .build();
        SnowflakeIdGenerateTask task2 = new SnowflakeIdGenerateTask(new SnowflakeIdGeneratorForLong(configOfAnotherMachine), size);
        ForkJoinPool forkJoinPool1 = new ForkJoinPool(numberOfProcessors/2);
        ForkJoinPool forkJoinPool2 = new ForkJoinPool(numberOfProcessors/2);
        List<Long> idList1 = forkJoinPool1.invoke(task1);
        List<Long> idList2 = forkJoinPool2.invoke(task2);
        idList1.addAll(idList2);
        assertThat(idList1).hasSize(size*2);
        assertThat(new HashSet<>(idList1)).hasSize(size * 2);
        idList1.forEach(id-> assertThat(id).isPositive());
    }
}
