package com.ildong.batch.totalJob;

import com.ildong.batch.model.Pay;
import com.ildong.batch.model.TotalPay;
import com.ildong.batch.repository.TotalPayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.format.DateTimeFormatter;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PayTotalJobThirdConfiguration {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final String JOB_NAME = "PayTotalThirdJob";
    public static final String BEAN_PREFIX = JOB_NAME + "_";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final TotalPayRepository totalPayRepository;
    private final DataShareBean<Long> dataShareBean;

    private final static int chunkSize = 1000;

    @Bean(JOB_NAME)
    public Job job() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step(null))
                .next(step2(null))
                .build();
    }

    @Bean(BEAN_PREFIX + "step")
    @JobScope
    public Step step(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get(BEAN_PREFIX + "step")
                .<Pay, Pay>chunk(chunkSize)
                .reader(reader(null))
                .writer(writer())
                .build();
    }

    @Bean(BEAN_PREFIX + "reader")
    @StepScope
    public JpaPagingItemReader<Pay> reader(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return new JpaPagingItemReaderBuilder<Pay>()
                .name(BEAN_PREFIX + "reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("select p from Pay p where to_char(tx_date_time,'yyyy-mm-dd') = '" + requestDate + "'")
                .build();
    }

    @Bean(BEAN_PREFIX + "writer")
    @StepScope
    public ItemWriter<Pay> writer() {
        return list -> {
            for(Pay pay : list) {
                log.info("Current = {}", pay);
                dataShareBean.addData("totalAmount", pay.getAmount());
            }
        };
    }

    @Bean(BEAN_PREFIX + "step2")
    @JobScope
    public Step step2(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get(BEAN_PREFIX + "step2")
                .tasklet((contribution, chunkContext) -> {

                    TotalPay totalPay = new TotalPay(dataShareBean.getData("totalAmount"),requestDate);

                    totalPayRepository.save(totalPay);

                    return RepeatStatus.FINISHED;
                }).build();
    }


}
