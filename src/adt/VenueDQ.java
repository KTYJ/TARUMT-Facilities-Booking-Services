/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

import java.util.Iterator;
import java.util.NoSuchElementException;
import model.Venue;

/**
 *
 * @author User
 */
public class VenueDQ<T> extends LinkedDeque<T> {
    public T find(String venueId) {
        if (venueId == null || isEmpty()) {
            return null;
        }

        Node<T> current = head;
        while (current != null) {
            if (current.data instanceof Venue v) {
                if (venueId.equals(v.getVenueId())) {
                    return current.data;
                }
            }
            current = current.next;
        }
        return null;
    }

    private class QuickSort {

        /**
         * Extracts the sort key from a Venue based on the given criterion.
         * Supported: "ID", "NAME", "CAPACITY", "STATUS"
         */
        private String getKey(Venue v, String criterion) {
            if (v == null)
                return "";
            switch (criterion.toUpperCase()) {
                case "NAME" -> {
                    return v.getVenueName().toLowerCase();
                }
                case "CAPACITY" -> {
                    return String.format("%010d", v.getCapacity());
                } // zero-padded for correct numeric string sort
                case "STATUS" -> {
                    return v.getStatus().toString().toLowerCase();
                }
                default -> {
                    return v.getVenueId();
                } // "ID" and fallback
            }
        }

        /**
         * Partitions the deque segment [low, high] around the pivot (high node).
         * Sorts by the given criterion using getKey().
         * Returns the node where the pivot ends up.
         */
        private Node<T> partition(Node<T> low, Node<T> high, String criterion) {
            if (low == null || high == null)
                return low;

            // Pivot key from the high node
            String pivotKey = getKey((Venue) high.data, criterion);

            // i starts one position BEFORE low
            Node<T> i = low.prev;

            for (Node<T> j = low; j != high; j = j.next) {
                String currentKey = getKey((Venue) j.data, criterion);

                // if capcity is selected, sort in descending order
                // else sort in ascending order
                if (criterion.equalsIgnoreCase("CAPACITY")
                        ? currentKey.compareTo(pivotKey) >= 0
                        : currentKey.compareTo(pivotKey) <= 0) {
                    // Advance i safely
                    i = (i == null) ? low : i.next;

                    // Swap data between i and j
                    T temp = i.data;
                    i.data = j.data;
                    j.data = temp;
                }
            }

            // Place pivot in its correct sorted position
            i = (i == null) ? low : i.next;
            T temp = i.data;
            i.data = high.data;
            high.data = temp;

            return i;
        }

        /**
         * Recursively sorts the deque segment between low and high.
         */
        private void quickSort(Node<T> low, Node<T> high, String criterion) {
            if (low != null && high != null && low != high && low != high.next) {
                Node<T> pivot = partition(low, high, criterion);

                quickSort(low, pivot.prev, criterion);
                quickSort(pivot.next, high, criterion);
            }
        }

    }

    /**
     * Sorts venues by the given criterion.
     * 
     * @param criterion one of: "ID", "NAME", "CAPACITY", "STATUS"
     *                  (case-insensitive)
     */
    public void sortBy(String criterion) {
        if (head == null || head.next == null)
            return;

        // Find tail
        Node<T> tail = head;
        while (tail.next != null) {
            tail = tail.next;
        }

        new QuickSort().quickSort(head, tail, criterion);
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
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }

            T data = current.data;
            current = current.next;
            return data;
        }
    }

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
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }

            T data = current.data;
            current = current.prev;
            return data;
        }
    }
}
