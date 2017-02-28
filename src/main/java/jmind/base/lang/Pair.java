package jmind.base.lang;

/**
 * pair数据结构，用于临时传数据等<br>
 * 未考虑成员的修改，equals()、hashCode()等操作未考虑空指针，请谨慎使用
 */
public final class Pair<A, B> {

    private final A first;
    private final B second;

    private Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair<A, B> of(A first, B second) {
        return new Pair<A, B>(first, second);
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    /* ------------------------- commons ------------------------- */

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Pair<?, ?>))
            return false;

        Pair<?, ?> p = (Pair<?, ?>) o;
        return this.first.equals(p.first) && this.second.equals(p.second);
    }

    @Override
    public int hashCode() {
        return this.first.hashCode() + this.second.hashCode();
    }

    /***
     * 这里兼容jqpolt曲线图，不要轻易修改
     */
    @Override
    public String toString() {
        return "[" + this.first + ", " + this.second + "]";
    }

}
