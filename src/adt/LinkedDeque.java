package adt;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A generic doubly-linked deque (double-ended queue) ADT.

 * Supports O(1) insertion and removal at both ends.
 *
 * @param <T> the type of elements held in this deque
 */
public class LinkedDeque<T> implements LinkedDequeInt<T>, Iterable<T> {

    // Define node
    protected static class Node<T> {
        T data;
        Node<T> prev, next;

        Node(T data) {
            this.data = data;
        }
    }

    protected Node<T> head;   
    protected Node<T> tail;   
    protected int size;

    public LinkedDeque() {
        head = null;
        tail = null;
        size = 0;
    }
    
    public void addFirst(T item) {
        if (item == null) 
            throw new IllegalArgumentException("Null items are not permitted.");
        Node<T> node = new Node<>(item);
        if (isEmpty()) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
    }

    public void addLast(T item) {
        if (item == null) 
            throw new IllegalArgumentException("Null items are not permitted");
        Node<T> node = new Node<>(item);
        if (isEmpty()) {
            head = tail = node; //Assign both head and tail to the new node
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        size++;
    }

    /**
     * Removes and returns the element at the front of the deque.
     *
     * @throws NoSuchElementException if the deque is empty
     */
    public T removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty.");
        T data = head.data;
        if (size == 1) {
            head = tail = null;
        } else {
            head = head.next;
            head.prev = null;
        }
        size--;
        return data;
    }

    /**
     * Removes and returns the element at the back of the deque.
     *
     * @throws NoSuchElementException if the deque is empty
     */
    public T removeLast() {
        if (isEmpty()) 
            throw new NoSuchElementException("Deque is empty.");
        T data = tail.data;
        if (size == 1) {
            head = tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
        size--;
        return data;
    }

    /**
     * Returns (without removing) the element at the front.
     *
     * @throws NoSuchElementException if the deque is empty
     */
    public T peekFirst() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty.");
        return head.data;
    }

    /**
     * Returns (without removing) the element at the back.
     *
     * @throws NoSuchElementException if the deque is empty
     */
    public T peekLast() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty.");
        return tail.data;
    }

    /** Returns true if the deque contains no elements. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns the number of elements in the deque. */
    public int size() {
        return size;
    }

    /** Removes all elements from the deque. */
    public void clear() {
        // Help GC by unlinking nodes
        Node<T> current = head;
        while (current != null) {
            Node<T> next = current.next;
            current.prev = null;
            current.next = null;
            current = next;
        }
        head = tail = null;
        size = 0;
    }

    /** Returns true if the deque contains the specified item. O(n). */
    public boolean contains(T item) {
        if (item == null) return false;
        Node<T> current = head;
        while (current != null) {
            if (current.data.equals(item)) return true;
            current = current.next;
        }
        return false;
    }

    @Override
    public String toString() {
        if (isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = head;
        while (current != null) {
            sb.append(current.data).append(", ");
            current = current.next;
        }
        sb.setLength(sb.length() - 2); //remove last two characters
        
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }

}