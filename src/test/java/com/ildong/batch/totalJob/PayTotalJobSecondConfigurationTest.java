package com.ildong.batch.totalJob;

import com.ildong.batch.config.TestJpaBatchConfig;
import com.ildong.batch.model.Pay;
import com.ildong.batch.model.TotalPay;
import com.ildong.batch.repository.PayRepository;
import com.ildong.batch.repository.TotalPayRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

@SpringBatchTest
@ActiveProfiles("h2")
@SpringBootTest(classes = {TestJpaBatchConfig.class, PayTotalJobSecondConfiguration.class})
class PayTotalJobSecondConfigurationTest {

    @Autowired
    PayRepository payRepository;

    @Autowired
    TotalPayRepository totalPayRepository;

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @AfterEach
    void afterEach() {
        totalPayRepository.deleteAllInBatch();
    }

    @Test
    void 실행테스트() throws Exception {

        String requestDate = "2021-05-24";

        UUID uuid = UUID.randomUUID();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("requestDate", requestDate)
                .addString("version", uuid.toString())
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        List<Pay> forTest = payRepository.findForTest(requestDate);
        long sum = forTest.stream()
                .mapToLong(p -> p.getAmount())
                .sum();
        TotalPay totalPay = totalPayRepository.findForTest(requestDate).get();

        Assertions.assertThat(sum).isEqualTo(totalPay.getSum());

    }
}