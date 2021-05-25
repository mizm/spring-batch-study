package com.ildong.batch.totalJob;

import com.ildong.batch.config.TestBatchConfig;
import com.ildong.batch.config.TestJpaBatchConfig;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.hibernate.type.LocalDateTimeType.FORMATTER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBatchTest
@SpringBootTest(classes = {TestJpaBatchConfig.class, PayWriteJobConfiguration.class})
class PayWriteJobConfigurationTest {

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void 생성테스트() throws Exception {


        JobParameters jobParameters = new JobParametersBuilder()
                .addString("requestDate", "2021-05-24")
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
    }
}