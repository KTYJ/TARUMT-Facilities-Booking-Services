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

public class BookingDQ<T> extends LinkedDeque<T> {

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
