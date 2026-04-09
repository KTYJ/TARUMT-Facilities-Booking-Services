/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

/**
 *
 * @author TAN JIN YUAN
 */
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A specialised LinkedDeque of String arrays.
 * Supports merge-sort on the LAST element of each array,
 * treating it as a numeric (double) value — ascending or descending.
 *
 * Usage example:
 *   SorterDQ dq = new SorterDQ();
 *   dq.sort(false);   // ascending
 *   dq.sort(true);    // descending
 */
public class SorterDQ extends LinkedDeque<String[]> {

    // ------------------------------------------------------------------ merge sort

    protected class MergeSort {

        private final boolean descending;

        MergeSort(boolean descending) {
            this.descending = descending;
        }

        /** Split the singly-linked chain at its midpoint; returns the second half. */
        private Node<String[]> split(Node<String[]> start) {
            Node<String[]> fast = start;
            Node<String[]> slow = start;

            while (fast != null && fast.next != null) {
                fast = fast.next.next;
                if (fast != null) {
                    slow = slow.next;
                }
            }

            Node<String[]> second = slow.next;
            slow.next = null;   // sever the chain
            return second;
        }

        /**
         * Returns the numeric value stored in the last slot of a row.
         * Falls back to 0 if the array is null/empty or the value cannot be parsed.
         */
        private double lastValue(String[] row) {
            if (row == null || row.length == 0) return 0;
            try {
                return Double.parseDouble(row[row.length - 1].trim());
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        /** Merge two sorted singly-linked chains. */
        private Node<String[]> merge(Node<String[]> a, Node<String[]> b) {
            if (a == null) return b;
            if (b == null) return a;

            double va = lastValue(a.data);
            double vb = lastValue(b.data);

            // pickA = true  → a comes first (ascending: smaller first)
            boolean pickA = descending ? (va >= vb) : (va <= vb);

            if (pickA) {
                a.next = merge(a.next, b);
                return a;
            } else {
                b.next = merge(a, b.next);
                return b;
            }
        }

        /** Recursively sort the singly-linked chain starting at {@code start}. */
        private Node<String[]> sort(Node<String[]> start) {
            if (start == null || start.next == null) return start;

            Node<String[]> second = split(start);

            start  = sort(start);
            second = sort(second);

            return merge(start, second);
        }
    }

    // ------------------------------------------------------------------ public API

    /**
     * Sorts the deque by the numeric value of each row's last element.
     *
     * @param descending {@code true}  → highest value first (e.g. most active users on top)
     *                   {@code false} → lowest value first
     */
    public void sort(boolean descending) {
        if (head == null || head.next == null) return;  // 0 or 1 element — already sorted

        // Run merge sort on the singly-linked chain (prev pointers ignored during sort)
        head = new MergeSort(descending).sort(head);

        // Rebuild prev pointers and update tail for the doubly-linked structure
        Node<String[]> current = head;
        current.prev = null;
        while (current.next != null) {
            current.next.prev = current;
            current = current.next;
        }
        tail = current;
    }

    // ------------------------------------------------------------------ iterators

    @Override
    public Iterator<String[]> iterator() {
        return new Iterator<String[]>() {
            private Node<String[]> current = head;

            @Override public boolean hasNext() { return current != null; }

            @Override
            public String[] next() {
                if (!hasNext()) throw new NoSuchElementException("No more elements");
                String[] data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    /** Traverse from tail to head. */
    public Iterator<String[]> reverseIterator() {
        return new Iterator<String[]>() {
            private Node<String[]> current = tail;

            @Override public boolean hasNext() { return current != null; }

            @Override
            public String[] next() {
                if (!hasNext()) throw new NoSuchElementException("No more elements");
                String[] data = current.data;
                current = current.prev;
                return data;
            }
        };
    }
}
