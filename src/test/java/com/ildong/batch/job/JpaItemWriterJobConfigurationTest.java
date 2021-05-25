package com.ildong.batch.job;

import com.ildong.batch.config.TestBatchConfig;
import com.ildong.batch.model.Pay;
import com.ildong.batch.model.Pay2;
import com.ildong.batch.repository.Pay2Repository;
import com.ildong.batch.repository.PayRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.batch.core.BatchStatus.COMPLETED;

@SpringBatchTest
@ActiveProfiles("h2")
@SpringBootTest(classes = {TestBatchConfig.class, JpaItemWriterJobConfiguration.class})
class JpaItemWriterJobConfigurationTest {

    @Autowired
    PayRepository payRepository;

    @Autowired
    Pay2Repository pay2Repository;

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;


    @BeforeEach
    void beforeEach() throws SQLException {

    }
    @AfterEach
    public void tearDown() throws Exception {
        payRepository.deleteAllInBatch();
        pay2Repository.deleteAllInBatch();
    }

    @Test
    void batchSampleTest() throws Exception {
        //given
        payRepository.save(new Pay(9000L, "test4", LocalDateTime.now()));
        payRepository.save(new Pay(10000L, "test1", LocalDateTime.now()));
        payRepository.save(new Pay(20000L, "test2", LocalDateTime.now()));
        payRepository.save(new Pay(30000L, "test3", LocalDateTime.now()));

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        ExitStatus exitStatus = jobExecution.getExitStatus();
        assertThat(exitStatus.getExitCode()).isEqualTo(COMPLETED.toString());

        List<Pay2> findList = pay2Repository.findAll();
        assertThat(findList.size()).isEqualTo(3);
        assertThat(findList.get(0).getAmount()).isEqualTo(10100L);
        assertThat(findList.get(1).getAmount()).isEqualTo(20100L);
        assertThat(findList.get(2).getAmount()).isEqualTo(30100L);

    }


}