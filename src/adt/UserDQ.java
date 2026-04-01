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
public class UserDQ extends LinkedDeque<User> {

    /**
     * Finds a user object in the ADT by ID.
     * 
     * @param id The ID of the user to find.
     * @return The user object if found, otherwise null.
     *         Linear Search - O(n)
     */

    public User find(String id) {
        if (id == null || isEmpty()) {
            return null;
        }
        Node<User> current = head;
        while (current != null) {
            if (current.data != null) {
                if (id.equals(current.data.getStudentId())) {
                    return current.data;
                }
            }
            current = current.next;
        }
        return null;
    }

    //
    public User get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        Node<User> current = head;
        int currentIndex = 0;

        while (currentIndex < index) {
            current = current.next;
            currentIndex++;
        }

        return current.data;
    }

    public void set(int index, User newValue) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        Node<User> current = head;
        int currentIndex = 0;

        while (currentIndex < index) {
            current = current.next;
            currentIndex++;
        }

        current.data = newValue;
    }

    /**
     * Extracts the sort key from a User based on a criterion string.
     * Supported criteria: "ID", "NAME", "ROLE", "STATUS"
     */
    private static String getKey(User u, String criterion) {
        if (u == null) return "";
        switch (criterion.toUpperCase()) {
            case "NAME"   -> { return u.getName().toLowerCase(); }
            case "ROLE"   -> { return u.getRole().toString(); }
            case "STATUS" -> { return u.getStatus().toString(); }
            default       -> { return u.getStudentId(); }  // "ID" and fallback
        }
    }

    protected class MergeSort {

        private final String criterion;

        MergeSort(String criterion) {
            this.criterion = criterion;  //which data column do you need to sort
        }

        private Node<User> split(Node<User> start) {
            Node<User> fast = start; // end of the deque
            Node<User> slow = start; // middle of the deque

            // Move fast pointer two steps and slow pointer one step until fast reaches the
            // end
            while (fast != null && fast.next != null) {
                fast = fast.next.next;
                if (fast != null) {
                    slow = slow.next;
                }
            }
            
            // Second = head of second half 
            Node<User> second = slow.next;
            // Split the list into two halves by terminating at the middle
            slow.next = null;
            
            return second;
        }

        // merge two sorted singly linked lists
        private Node<User> merge(Node<User> first, Node<User> second) {
            if (first == null) return second;
            if (second == null) return first;

            boolean pickFirst = true;
            // U1 = first user to compare, U2 = second user to compare
            if (first.data != null && second.data != null) {
                String k1 = getKey(first.data, criterion);
                String k2 = getKey(second.data, criterion);
                pickFirst = k1.compareTo(k2) <= 0;
            }

            if (pickFirst) {
                first.next = merge(first.next, second);
                return first;
            } else {
                second.next = merge(first, second.next);
                return second;
            }
        }

        private Node<User> sort(Node<User> start) {
            if (start == null || start.next == null) return start;

            Node<User> secondHalf = split(start);

            start      = sort(start);
            secondHalf = sort(secondHalf);

            return merge(start, secondHalf);
        }
    }

    /**
     * Sorts users by the given criterion.
     * @param criterion one of: "ID", "NAME", "ROLE", "STATUS" (case-insensitive)
     */
    public void sortBy(String criterion) {
        if (head == null || head.next == null) return;

        head = new MergeSort(criterion).sort(head);

        // Rebuild prev pointers and update tail for the doubly-linked list
        Node<User> current = head;
        current.prev = null;
        while (current.next != null) {
            current.next.prev = current;
            current = current.next;
        }
        tail = current;
    }

    @Override
    public Iterator<User> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<User> {
        private Node<User> current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public User next() {
            if (!hasNext())
                throw new NoSuchElementException("No more elements");
            User data = current.data;
            current = current.next;
            return data;
        }
    }

    /** Returns an iterator that traverses from back to front. */
    public Iterator<User> reverseIterator() {
        return new ReverseDequeIterator();
    }

    private class ReverseDequeIterator implements Iterator<User> {
        private Node<User> current = tail;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public User next() {
            if (!hasNext())
                throw new NoSuchElementException("No more elements");
            User data = current.data;
            current = current.prev;
            return data;
        }
    }
}
