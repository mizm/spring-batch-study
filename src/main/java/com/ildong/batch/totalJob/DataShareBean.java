package com.ildong.batch.totalJob;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class DataShareBean <T> {

    private Map<String, T> shareDataMap;
    private AtomicLong total = new AtomicLong();

    public DataShareBean() {
        this.shareDataMap = new ConcurrentHashMap<>();
    }

    public void putData(String key, T data) {
        if(shareDataMap == null) {
            log.info("map is not initialize");
            return;
        }

        shareDataMap.put(key, data);
    }

    public T getData (String key) {
        if (shareDataMap == null) {
            log.info("map is not initialize");
            return null;
        }

        return shareDataMap.get(key);
    }

    public void addData(String key, T data) {
        shareDataMap.compute(key,(k,v) -> (T) Long.valueOf((Long) data + (Long) v));
    }

    public void addTotal(Long data) {
        this.total.addAndGet(data);
    }

    public Long getTotal() {
        return this.total.get();
    }
}
