package com.ildong.batch;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class transactionTest {

    @Test
    void test() {
        String s1 = "xyZA";
        String s2 = "ABCxy";
        List<String> answers = new ArrayList<>();
        solution(s1,s2);
//        System.out.println("b.substring(0,3) = " + b.substring(2,3));

    }

    static class Coffee implements Comparable<Coffee>{
        int time;
        int order;

        Coffee(int time,int order) {
            this.time = time;
            this.order = order;
        }

        @Override
        public int compareTo(Coffee o) {
            return time == o.time ? order - o.order : time - o.time;
        }

        void minusTime(int time) {
            this.time -= time;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    @Test
    void test3() {
        int[] coffe_times = {100,1,50,1,1};
        int N = 1;
        List<Integer> answers = new ArrayList<>();
        PriorityQueue<Coffee> queue = new PriorityQueue<>();
        for(int i = 0; i < coffe_times.length; i++) {
            queue.add(new Coffee(coffe_times[i], i+1));
            if(queue.size() == N) {
                queue = checkCoffee(queue,answers);
            }
        }
        if(!queue.isEmpty()) checkCoffee(queue,answers);
        System.out.println("answers = " + answers);
        int[] answer = {};
    }

    private PriorityQueue<Coffee> checkCoffee(PriorityQueue<Coffee> queue, List<Integer> answers) {
        Coffee firstOut = queue.poll();
        PriorityQueue<Coffee> res = new PriorityQueue<>();
        answers.add(firstOut.order);
        while(!queue.isEmpty()) {
            Coffee out = queue.poll();
            out.minusTime(firstOut.time);
            if(out.time == 0) {
                answers.add(out.order);
            } else {
                res.add(out);
            }
        }
        return res;
    }

    @Test
    void test2() {
        int[] grade = new int [] {1,1,3,3,2};
        List<Integer> newGrade = Arrays.stream(grade).boxed().collect(Collectors.toList());
        newGrade.sort(Collections.reverseOrder());
        System.out.println("newGrade = " + newGrade);
        int[] ints = Arrays.stream(grade).map(g -> newGrade.indexOf(g) + 1).toArray();
        for (int anInt : ints) {
            System.out.println("anInt = " + anInt);
        }
    }

    public String solution(String s1, String s2) {
        String answer = "";
        List<String> answers = new ArrayList<>();
        answers.add(s1+s2);
        int size = s1.length() + s2.length();
        check(s1,s2,answers);
        check(s2,s1,answers);
        System.out.println("answers = " + answers);
        return answer;
    }

    private void check(String s1, String s2, List<String> answers) {
        for(int i = 1; i <= s1.length(); i++) {
            String firstSub = s1.substring(0,i);
            if(s2.length() - i < 0) break;
            String secondSub = s2.substring(s2.length() - i ,s2.length());
            if(firstSub.equals(secondSub)) {
                answers.add(s2 + s1.substring(i,s1.length()));
            }
        }
    }

    @Test
    void test4() {
        int n = 99;
        int res = -1;
        for(int i = 0; 5* i <= n; i++) {
            if((n - ( 5 * i)) - ((n - (5*i)) / 3) * 3 == 0) {
                res = i;
            }
        }
        System.out.println("res = " + (res == -1 ? res : (n - (5 * res)) /3 + res));
    }
}
