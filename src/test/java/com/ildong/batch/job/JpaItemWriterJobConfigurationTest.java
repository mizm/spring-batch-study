package com.ildong.batch.job;

import com.ildong.batch.BatchApplication;
import com.ildong.batch.config.TestBatchConfig;
import com.ildong.batch.model.Pay;
import com.ildong.batch.repository.PayRepository;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBatchTest
//@SpringBootTest
@SpringBootTest
class JpaItemWriterJobConfigurationTest {

    @Autowired
    EntityManager em;
//    PayRepository payRepository;

    @Qualifier("jpaPagingItemReaderJob")
    Job jpaPagingItemReaderJob;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private ApplicationContext applicationContext;


    @BeforeEach
    void beforeEach() {
        List<Pay> select_p_from_pay_p = em.createQuery("select p from Pay p", Pay.class).getResultList();
        for (Pay pay : select_p_from_pay_p) {
            System.out.println("pay = " + pay);
        }
//        Object pay = applicationContext.getBean("payRepository");
//        System.out.println("pay = " + pay);
    }
    @After
    public void tearDown() throws Exception {
//        pay2Repository.deleteAllInBatch();
    }

    @Test
    void batchSampleTest() throws Exception {
        jobLauncherTestUtils.setJob(jpaPagingItemReaderJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        ExitStatus exitStatus = jobExecution.getExitStatus();
        System.out.println("exitStatus = " + exitStatus);
    }


}