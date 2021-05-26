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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.hibernate.type.LocalDateTimeType.FORMATTER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBatchTest
@ActiveProfiles("h2")
@SpringBootTest(classes = {TestJpaBatchConfig.class, PayWriteJobConfiguration.class})
class PayWriteJobConfigurationTest {

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void 생성테스트() throws Exception {
        UUID uuid = UUID.randomUUID();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("requestDate", "2021-05-24")
                .addString("random",uuid.toString())
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
    }
}