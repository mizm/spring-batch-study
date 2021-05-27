package com.ildong.batch.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class testService {

    public void grand() {
        father();
    }

    public void father() {
        sun();

        sun();

        sun();
    }

    @Transactional
    public void sun() {
        log.info("hi");
    }
}
