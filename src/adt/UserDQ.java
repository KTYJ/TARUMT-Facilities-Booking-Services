/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

import java.util.Iterator;
import java.util.NoSuchElementException;
import model.User;

/**
 *
 * @author KTYJ
 */
public class UserDQ<T> extends LinkedDeque<T> implements Iterable<T> {
    
    /**
     * Finds a user object in the ADT by ID.
     * @param id The ID of the user to find.
     * @return The user object if found, otherwise null.
     */
    public T find(String id) {
        if (id == null || isEmpty()) {
            return null;
        }
        Node<T> current = head;
        while (current != null) {
            if (current.data instanceof User s) {
                if (id.equals(s.getId())) {
                    return current.data;
                }
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<T> {
        private Node<T> current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if (!hasNext())
                throw new NoSuchElementException("No more elements");
            T data = current.data;
            current = current.next;
            return data;
        }
    }

    /** Returns an iterator that traverses from back to front. */
    public Iterator<T> reverseIterator() {
        return new ReverseDequeIterator();
    }

    private class ReverseDequeIterator implements Iterator<T> {
        private Node<T> current = tail;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if (!hasNext()) 
                throw new NoSuchElementException("No more elements");
            T data = current.data;
            current = current.prev;
            return data;        
        }
    }

}
