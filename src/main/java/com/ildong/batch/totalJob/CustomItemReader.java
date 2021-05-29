package com.ildong.batch.totalJob;

import com.ildong.batch.model.Pay;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomItemReader implements ItemReader<Pay> {

    private static int count;
    private String requestDate;

    public CustomItemReader(String requestDate) {
        this.requestDate = requestDate;
    }

    @Override
    public Pay read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        Pay pay = null;
        if(count < 100000) {
            int amount = (int) (Math.random() * 1000000);
            int hour = (int) (Math.random() * 100000) % 24;
            int min = (int) (Math.random() * 100000) % 60;
            LocalDate date = LocalDate.parse(requestDate);
            LocalDateTime requestTime = LocalDateTime.of(date.getYear(),date.getMonth(),date.getDayOfMonth(),hour,min);
            pay = new Pay((long) amount,"tx"+count, requestTime);
            count++;
        }
        return pay;
    }
}
