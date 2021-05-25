package com.ildong.batch.job.step;

import com.ildong.batch.config.TestBatchConfig;
import com.ildong.batch.config.TestJpaBatchConfig;
import com.ildong.batch.job.JpaItemWriterJobConfiguration;
import com.ildong.batch.model.Pay;
import com.ildong.batch.model.Pay2;
import com.ildong.batch.repository.Pay2Repository;
import com.ildong.batch.repository.PayRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBatchTest
@SpringBootTest(classes = {JpaItemWriterJobConfiguration.class, TestJpaBatchConfig.class})
public class BatchUnitTest1 {

    @Autowired
    JpaPagingItemReader<Pay> reader;

    @Autowired
    ItemProcessor<Pay, Pay2> jpaItemProcessor;

    @Autowired
    JpaItemWriter<Pay2> jpaItemWriter;

    @Autowired
    PayRepository payRepository;

    @Autowired
    Pay2Repository pay2Repository;

    @AfterEach
    void tearDown() {
        payRepository.deleteAllInBatch();
        pay2Repository.deleteAllInBatch();
    }

    public StepExecution getStepExecution() {
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("orderDate", orderDate.format(FORMATTER))
//                .toJobParameters();
        return MetaDataInstanceFactory.createStepExecution();
    }

    @Test
    public void reader_유닛테스트() throws Exception {
        //given
        Pay test4 = new Pay(9000L, "test4", LocalDateTime.now());
        Pay test1 = new Pay(10000L, "test1", LocalDateTime.now());
        Pay test2 = new Pay(20000L, "test2", LocalDateTime.now());
        Pay test3 = new Pay(30000L, "test3", LocalDateTime.now());
        payRepository.save(test4);
        payRepository.save(test1);
        payRepository.save(test2);
        payRepository.save(test3);

        reader.open(new ExecutionContext());

        //when
        Pay read = reader.read();
        checkEqualPay(test1, read);

        read = reader.read();
        checkEqualPay(test2, read);

        read = reader.read();
        checkEqualPay(test3, read);

        read = reader.read();
        assertThat(read).isNull();
    }

    @Test
    void processorUnitTest() throws Exception {
        //given
        Pay test1 = new Pay(10000L, "test1", LocalDateTime.now());

        //when
        Pay2 process = jpaItemProcessor.process(test1);

        //then
        assertThat(test1.getTxName()).isEqualTo(process.getTxName());
        assertThat(test1.getTxDateTime()).isEqualTo(process.getTxDateTime());
        assertThat(test1.getAmount() + 100L).isEqualTo(process.getAmount());
    }

    @Test
    @Transactional
    void writerTest() {

        //given
        Pay2 test1 = new Pay2(10000L, "test1", LocalDateTime.now());
        Pay2 test2 = new Pay2(20000L, "test2", LocalDateTime.now());
        Pay2 test3 = new Pay2(30000L, "test3", LocalDateTime.now());
        List<Pay2> items = new ArrayList<>(Arrays.asList(test1, test2, test3));

        //when
        jpaItemWriter.write(items);


        //then
        List<Pay2> all = pay2Repository.findAll();
        assertThat(all.size()).isEqualTo(3);
        checkEqualPay2(test1,all.get(0));
        checkEqualPay2(test2,all.get(1));
        checkEqualPay2(test3,all.get(2));

    }

    private void checkEqualPay(Pay pay1, Pay pay2) {
        assertThat(pay2.getAmount()).isEqualTo(pay1.getAmount());
        assertThat(pay2.getTxDateTime()).isEqualTo(pay1.getTxDateTime());
        assertThat(pay2.getTxName()).isEqualTo(pay1.getTxName());
        assertThat(pay2.getId()).isEqualTo(pay1.getId());
    }

    private void checkEqualPay2(Pay2 pay1, Pay2 pay2) {
        assertThat(pay2.getAmount()).isEqualTo(pay1.getAmount());
        assertThat(pay2.getTxDateTime()).isEqualTo(pay1.getTxDateTime());
        assertThat(pay2.getTxName()).isEqualTo(pay1.getTxName());
    }

}
