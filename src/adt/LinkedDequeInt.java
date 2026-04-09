/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

/**
 *
 * @author TAN JIN YUAN
 */

/**
 * Deque (double-ended queue) ADT.
 *
 * Defines the public contract for a deque — a linear collection that supports
 * element insertion and removal at both ends. This interface specifies only the
 * *what* (observable behaviour and preconditions/postconditions); it says
 * nothing about the underlying data structure used to fulfil it.
 *
 * Mathematical model: a finite sequence of elements <e₀, e₁, …,
 * e_{n-1}> where e₀ is the front and e_{n-1} is the back.
 * and n represents the number of elements in the queue
 *
 * Preconditions / postconditions are documented per method.
 *
 * @param <T> the type of elements held in this deque
 */
public interface LinkedDequeInt<T> {

    // -----------------------------------------------------------------------
    // Insertion — O(1) required
    // -----------------------------------------------------------------------
    /**
     * Inserts {@code item} at the front of this deque.
     *
     * Pre: {@code item != null}
     * Post: {@code peekFirst() == item}, {@code size()} increases by 1
     *
     * @param item the element to insert
     * @throws IllegalArgumentException if {@code item} is null
     */
    void addFirst(T item);

    /**
     * Inserts {@code item} at the back of this deque.
     *
     * Pre: {@code item != null}
     * Post: {@code peekLast() == item}, {@code size()} increases by 1
     *
     * @param item the element to insert
     * @throws IllegalArgumentException if {@code item} is null
     */
    void addLast(T item);

    // -----------------------------------------------------------------------
    // Removal — O(1) required
    // -----------------------------------------------------------------------
    /**
     * Removes and returns the element at the front of this deque.
     *
     * Pre: {@code !isEmpty()}
     * Post: the former second element becomes the new front,
     * {@code size()} decreases by 1
     *
     * @return the element that was at the front
     * @throws java.util.NoSuchElementException if this deque is empty
     */
    T removeFirst();

    /**
     * Removes and returns the element at the back of this deque.
     *
     * Pre: {@code !isEmpty()}
     * Post: the former second-to-last element becomes the new back,
     * {@code size()} decreases by 1
     *
     * @return the element that was at the back
     * @throws java.util.NoSuchElementException if this deque is empty
     */
    T removeLast();

    // -----------------------------------------------------------------------
    // Inspection — O(1) required, non-mutating
    // -----------------------------------------------------------------------
    /**
     * Returns the element at the front without removing it.
     *
     * Pre: {@code !isEmpty()}
     * Post: this deque is unchanged
     *
     * @return the front element
     * @throws java.util.NoSuchElementException if this deque is empty
     */
    T peekFirst();

    /**
     * Returns the element at the back without removing it.
     *
     * Pre: {@code !isEmpty()}
     * Post: this deque is unchanged
     *
     * @return the back element
     * @throws java.util.NoSuchElementException if this deque is empty
     */
    T peekLast();

    // -----------------------------------------------------------------------
    // State queries — O(1) required
    // -----------------------------------------------------------------------
    /**
     * Returns {@code true} if this deque contains no elements.
     *
     * Post: this deque is unchanged. Equivalent to {@code size() == 0}.
     */
    boolean isEmpty();

    /**
     * Returns the number of elements currently in this deque.
     *
     * Post: this deque is unchanged.
     */
    int size();

    // -----------------------------------------------------------------------
    // Bulk / search operations
    // -----------------------------------------------------------------------
    /**
     * Returns {@code true} if this deque contains at least one element equal to
     * {@code item} (by {@link Object#equals}).
     *
     * Pre: {@code item != null} (returns {@code false} for null input)
     * Post: this deque is unchanged
     * Time: O(n)
     *
     * @param item the element to search for
     */
    boolean contains(T item);

    /**
     * Removes all elements from this deque.
     *
     * Post: {@code isEmpty() == true}, {@code size() == 0}
     */
    void clear();

}
