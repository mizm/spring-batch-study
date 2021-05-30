package com.ildong.batch.totalJob.listener;

import com.ildong.batch.totalJob.DataShareBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

@RequiredArgsConstructor
@Slf4j
public class TotalJobListener extends JobExecutionListenerSupport {

    private final DataShareBean dataShareBean;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.STARTED) {
            log.info("start Job & initial dataShareBean.totalAmount");
            dataShareBean.putData("totalAmount",0L);
        }
    }
}
