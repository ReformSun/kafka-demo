package com.test.consumer;

public class Tuple2<T0, T1> {
    public T0 f0;
    public T1 f1;
    public Tuple2() {}
    public Tuple2(T0 value0, T1 value1) {
        this.f0 = value0;
        this.f1 = value1;
    }

    public static <T0, T1> Tuple2<T0, T1> of(T0 value0, T1 value1) {
        return new Tuple2<>(value0,
                value1);
    }
}
