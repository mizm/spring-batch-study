package com.ildong.batch.totalJob;

import com.ildong.batch.model.Pay;
import com.ildong.batch.model.TotalPay;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomCreateTotalPayReader implements ItemReader<TotalPay> {

    private static int count;
    private String requestDate;
    private Long total;

    public CustomCreateTotalPayReader(String requestDate, Long total) {
        this.requestDate = requestDate;
        this.total = total;
    }

    @Override
    public TotalPay read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        TotalPay totalPay = null;
        if(count < 1) {
            totalPay = new TotalPay(total, requestDate);
            count++;
        }
        return totalPay;
    }
}
