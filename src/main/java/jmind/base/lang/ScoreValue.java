package jmind.base.lang;

import java.io.Serializable;

public class ScoreValue<V> implements Comparable<ScoreValue<V>>, Serializable {
    public final double score;
    private final V value;

    public ScoreValue(double score, V value) {
        this.score = score;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        ScoreValue<?> that = (ScoreValue<?>) o;
        return Double.compare(that.score, score) == 0 && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        long temp = score != +0.0d ? Double.doubleToLongBits(score) : 0L;
        int result = (int) (temp ^ (temp >>> 32));
        return 31 * result + (value != null ? value.hashCode() : 0);
    }

    @Override
    public String toString() {
        return String.format("(%f, %s)", score, value);
    }

    @Override
    public int compareTo(ScoreValue<V> o) {
        return (int) (o.score - this.score);
    }

    public V getValue() {
        return value;
    }

    public int getScore() {
        return (int) score;
    }
}
