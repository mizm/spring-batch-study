package com.ildong.batch.totalJob.partition;

import com.ildong.batch.model.Pay;
import com.ildong.batch.model.TotalPay;
import com.ildong.batch.repository.PayRepository;
import com.ildong.batch.repository.TotalPayRepository;
import com.ildong.batch.totalJob.DataShareBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PayTotalJobPartitionConfiguration {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final String JOB_NAME = "PayTotalJobPartitionJob";
    public static final String BEAN_PREFIX = JOB_NAME + "_";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final PayRepository payRepository;
    private final TotalPayRepository totalPayRepository;
    private final DataShareBean<Long> dataShareBean;

    private final static int chunkSize = 1000;
    private final static int poolSize = 20;

    @Bean(name = JOB_NAME+"_taskPool")
    public TaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setThreadNamePrefix("partition-thread");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        executor.initialize();
        return executor;
    }

    @Bean(name = JOB_NAME+"_partitionHandler")
    public TaskExecutorPartitionHandler partitionHandler() {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler(); // (1)
        partitionHandler.setStep(step1()); // (2)
        partitionHandler.setTaskExecutor(executor()); // (3)
        partitionHandler.setGridSize(poolSize); // (4)
        return partitionHandler;
    }

    @Bean(JOB_NAME)
    public Job job() {
        dataShareBean.putData("totalAmount", 0L);
        return jobBuilderFactory.get(JOB_NAME)
                .start(step1Manager())
                .next(step2(null))
                //.preventRestart()
                .build();
    }

    @Bean(name = JOB_NAME +"_step1Manager")
    public Step step1Manager() {
        return stepBuilderFactory.get("step1.manager") // (1)
                .partitioner("step1", partitioner(null)) // (2)
                .step(step1()) // (3)
                .partitionHandler(partitionHandler()) // (4)
                .build();
    }

    @Bean(name = JOB_NAME +"_partitioner")
    @StepScope
    public PayIdRangePartitioner partitioner(
            @Value("#{jobParameters[requestDate]}") String requestDate) {

        return new PayIdRangePartitioner(payRepository,requestDate);
    }

    @Bean(BEAN_PREFIX + "step")
    public Step step1() {
        return stepBuilderFactory.get(BEAN_PREFIX + "step")
                .<Pay, Pay>chunk(chunkSize)
                .reader(reader(null,null))
                .writer(writer())
                .build();
    }

    @Bean(BEAN_PREFIX + "reader")
    @StepScope
    public JpaPagingItemReader<Pay> reader(
        @Value("#{stepExecutionContext[minId]}") Long minId,
        @Value("#{stepExecutionContext[maxId]}") Long maxId
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("minId", minId);
        params.put("maxId", maxId);
//        log.info("{},{}",maxId,maxId);
        return new JpaPagingItemReaderBuilder<Pay>()
                .name(BEAN_PREFIX + "reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("select p from Pay p where p.id BETWEEN :minId AND :maxId")
                .parameterValues(params)
                .build();
    }

    @Bean(BEAN_PREFIX + "writer")
    public ItemWriter<Pay> writer() {
        return list -> {
            for(Pay pay : list) {
//                log.info("Current = {}", pay);
//                dataShareBean.addTotal(pay.getAmount());
                dataShareBean.addData("totalAmount",pay.getAmount());
//                log.info("total = {}",dataShareBean.getData("totalAmount"));
            }
        };
    }

    @Bean(BEAN_PREFIX + "step2")
    @JobScope
    public Step step2(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get(BEAN_PREFIX + "step2")
                .tasklet((contribution, chunkContext) -> {
//                    log.info("total {}" , dataShareBean.getTotal());
//                    TotalPay totalPay = new TotalPay(dataShareBean.getTotal(),requestDate);
                    TotalPay totalPay = new TotalPay(dataShareBean.getData("totalAmount"),requestDate);

                    totalPayRepository.save(totalPay);

                    return RepeatStatus.FINISHED;
                }).build();
    }


}
