package jmind.base.util.concurrent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 基于Treiber 算法实现无阻赛Stack
 * @author wbxie
 * 2014年7月4日
 * @param <E>
 */
public class ConcurrentStack<E> {
    static class Node<E> {
        final E item;
        Node<E> next;

        public Node(E item) {
            this.item = item;
        }
    }

    AtomicReference<Node<E>> head = new AtomicReference<ConcurrentStack.Node<E>>();

    public void push(E item) {
        Node<E> newHead = new Node<E>(item);
        Node<E> oldHead;
        do {
            oldHead = head.get();
            newHead.next = oldHead;
        } while (!head.compareAndSet(oldHead, newHead));
    }

    public E pop() {
        Node<E> oldHead, newHead;
        do {
            oldHead = head.get();
            if (oldHead == null)
                return null;
            newHead = oldHead.next;
        } while (!head.compareAndSet(oldHead, newHead));
        return oldHead.item;
    }

}
