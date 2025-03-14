package org.ironriders.lib.data;

public class Range<T> {

    public T low;
    public T high;

    public Range(T low, T high) {
        this.low = low;
        this.high = high;
    }

    public static <T> Range<T> as(Class<? extends T> klass, Range<?> range) {
        return new Range<T>(klass.cast(range.low), klass.cast(range.high));
    }
}
