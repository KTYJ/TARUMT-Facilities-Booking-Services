/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

/**
 *
 * @author User
 */
import java.util.Iterator;
import java.util.NoSuchElementException;
import model.Booking;

public class BookingDQ<T> extends LinkedDeque<T>{

    public T find(String bookingId) {
        if (bookingId == null || isEmpty()) {
            return null;
        }

        Node<T> current = head;
        while (current != null) {
            if (current.data instanceof Booking b) {
                if (bookingId.equals(b.getBookingId())) {
                    return current.data;
                }
            }
            current = current.next;
        }
        return null;
    }

    /**
     * Private inner class that implements Heap Sort on the deque's nodes.
     * Reuses Booking.compareTo() for all ordering decisions.
     */
    private class HeapSort {
        
        private final boolean ascending;
        
        public HeapSort(boolean ascending) {
            this.ascending = ascending;
        }

        /**
         * Restores the heap property at index i within the first n elements.
         * If ascending is true, builds a max-heap (largest bubbles up, resulting in ascending array).
         * If ascending is false, builds a min-heap (smallest bubbles up, resulting in descending array).
         */
        @SuppressWarnings("unchecked")
        private void heapify(Node<T>[] arr, int n, int i) {
            int target = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;

            // Compare left child with current target
            if (left < n) {
                Booking bLeft = (Booking) arr[left].data;
                Booking bTarget = (Booking) arr[target].data;
                if (ascending) {
                    if (bLeft.compareTo(bTarget) > 0) target = left;
                } else {
                    if (bLeft.compareTo(bTarget) < 0) target = left;
                }
            }

            // Compare right child with current target
            if (right < n) {
                Booking bRight = (Booking) arr[right].data;
                Booking bTarget = (Booking) arr[target].data;
                if (ascending) {
                    if (bRight.compareTo(bTarget) > 0) target = right;
                } else {
                    if (bRight.compareTo(bTarget) < 0) target = right;
                }
            }

            // If target changed, swap and recurse down
            if (target != i) {
                T temp = arr[i].data;
                arr[i].data = arr[target].data;
                arr[target].data = temp;

                heapify(arr, n, target);
            }
        }

        /**
         * Copies deque nodes into an array, heap-sorts them,
         * then the sorted values are written back in-place via the node references.
         */
        @SuppressWarnings("unchecked")
        private void sort() {
            // Step 1: Copy all node references into an array
            Node<T>[] arr = new Node[size];
            Node<T> current = head;
            for (int i = 0; i < size; i++) {
                arr[i] = current;
                current = current.next;
            }

            int n = arr.length;

            // Step 2: Build heap (bottom-up from last non-leaf)
            for (int i = n / 2 - 1; i >= 0; i--)
                heapify(arr, n, i);

            // Step 3: Extract elements one by one
            // Swap root to end, shrink heap, re-heapify
            for (int i = n - 1; i > 0; i--) {
                T temp = arr[0].data;
                arr[0].data = arr[i].data;
                arr[i].data = temp;

                heapify(arr, i, 0);
            }
        }
    }

    /**
     * Sorts all bookings by endDateTime.
     * @param ascending true for chronological order (earliest first), false for reverse chronological.
     */
    public void sortByDateTime(boolean ascending) {
        if (head == null || head.next == null)
            return;

        new HeapSort(ascending).sort();
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
