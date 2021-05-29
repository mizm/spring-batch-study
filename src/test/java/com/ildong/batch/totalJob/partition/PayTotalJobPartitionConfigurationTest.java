package com.ildong.batch.totalJob.partition;

import com.ildong.batch.config.TestJpaBatchConfig;
import com.ildong.batch.model.Pay;
import com.ildong.batch.model.TotalPay;
import com.ildong.batch.repository.PayRepository;
import com.ildong.batch.repository.TotalPayRepository;
import com.ildong.batch.totalJob.DataShareBean;
import com.ildong.batch.totalJob.partition.PayTotalJobPartitionConfiguration;
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
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.UUID;

@SpringBatchTest
@ActiveProfiles("h2")
@SpringBootTest(classes = {TestJpaBatchConfig.class, PayTotalJobPartitionConfiguration.class, DataShareBean.class})
class PayTotalJobPartitionConfigurationTest {

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

        StopWatch stopWatch = new StopWatch("single");
        stopWatch.start();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        stopWatch.stop();
        System.out.println("stopWatch = " + stopWatch.prettyPrint());
        Long sum = payRepository.findForTest(requestDate);
        TotalPay totalPay = totalPayRepository.findForTest(requestDate).get();
//        5648810L 6580134L
        System.out.println("totalPay = " + totalPay.getSum());
        Assertions.assertThat(sum).isEqualTo(totalPay.getSum());

        //gripsize 5 multi
        //8366703292  100%
        //poolsize 10 multi
//        8139994125  100%
        //signle
//        323053831084  100%

        //백만개
        // 20개 병렬
        //21484110792  100%
        // 20개 로깅제외
        // 12726939792  100%
        // 5개 병렬
        //25323267917  100%
        // 5개 병렬 로깅제외
        //18277477625  100%

    }
}