package com.pokeverse.scribble.components;

import java.util.LinkedList;
import java.util.Queue;
import java.util.HashSet;

public class NonDuplicateQueue<T> {
    private Queue<T> queue;
    private HashSet<T> set;

    public NonDuplicateQueue() {
        this.queue = new LinkedList<>();
        this.set = new HashSet<>();
    }

    public void enqueue(T element) {
        if (!set.contains(element)) {
            queue.offer(element);
            set.add(element);
        }
    }

    public void enqueue(Iterable<T> elements) {
        for (T element : elements) {
            enqueue(element);
        }
    }

    public T dequeue() {
        if (!queue.isEmpty()) {
            T element = queue.poll();
            set.remove(element);
            return element;
        }
        return null;
    }

    public T peek() {
        return queue.peek();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }
}