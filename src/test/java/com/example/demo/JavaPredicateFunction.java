package com.example.demo;

import org.junit.Test;

import java.util.function.Predicate;

public class JavaPredicateFunction {
    Predicate<Integer> isOver = (num) -> {
        return num > 100;
    };

    @Test
    public void runPredicate(){

        System.out.println(isOver.test(101));
        System.out.println(isOver.and(num -> num < 200).test(101));
        System.out.println(isOver.and(num -> num < 200).test(201));
        System.out.println(isOver.or(num -> num == 9).test(9));
        System.out.println(isOver.negate().test(101));
    }
}
