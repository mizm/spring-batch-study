package com.ildong.batch.totalJob;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class DataShareBean <T> {

    private Map<String, T> shareDataMap;

    public DataShareBean() {
        this.shareDataMap = new ConcurrentHashMap<>();
    }

    public void putData(String key, T data) {
        if(shareDataMap == null) {
            log.debug("map is not initialize");
            return;
        }

        shareDataMap.put(key, data);
    }

    public T getData (String key) {

        if (shareDataMap == null) {
            return null;
        }

        return shareDataMap.get(key);
    }

    public void addData(String key, T data) {
        if(shareDataMap == null) {
            log.debug("map is not initialize");
            return;
        }
        T t = shareDataMap.get(key);
        if(t == null) {
            shareDataMap.put(key,data);
            return;
        }

        shareDataMap.put(key, (T) Long.valueOf((Long) data + (Long) t));
    }
}
