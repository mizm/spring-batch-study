package com.ildong.batch.job;

import com.ildong.batch.model.Pay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ProcessorCompositeJobConfiguration {

    public static final String JOB_NAME = "ProcessorCompositeBatch";
    public static final String BEAN_PREFIX = JOB_NAME + "_";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private static final int chunkSize = 10;


    @Bean(JOB_NAME)
    public Job job() {
        return jobBuilderFactory.get(JOB_NAME)
                .preventRestart()
                .start(step())
                .build();
    }

    @Bean(BEAN_PREFIX + "step")
    @JobScope
    public Step step() {
        return stepBuilderFactory.get(BEAN_PREFIX + "step")
                .<Pay, String>chunk(chunkSize)
                .reader(reader())
                .processor(compositeProcessor())
                .writer(writer())
                .build();
    }

    @Bean(BEAN_PREFIX + "reader")
    public JpaPagingItemReader<Pay> reader() {
        return new JpaPagingItemReaderBuilder<Pay>()
                .name(BEAN_PREFIX + "reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT p from Pay p")
                .build();
    }

    @Bean(BEAN_PREFIX + "compositeProcessor")
    public CompositeItemProcessor<Pay,String> compositeProcessor() {
        // item Processor의 변환을 단계별로 구현하기 위한 프로세서
        List<ItemProcessor> delegate = new ArrayList<>();
        delegate.add(processor1());
        delegate.add(processor2());

        CompositeItemProcessor processor = new CompositeItemProcessor();
        processor.setDelegates(delegate);
        return processor;
    }

    public ItemProcessor<Pay, String> processor1() {
        return Pay::getTxName;
    }

    public ItemProcessor<String, String> processor2() {
        return name -> "안녕하세요. "+ name + "입니다.";
    }

    @Bean(BEAN_PREFIX + "writer")
    public ItemWriter<String> writer() {
        return items -> {
            for(String s : items) {
                log.info("Current = {}", s);
            }
        };
    }
}
