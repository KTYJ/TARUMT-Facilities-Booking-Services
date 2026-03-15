/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package adt;
import java.util.Iterator;

/**
 *
 * @author KTYJ
 * @param <T>
 */
public interface DequeInt<T> {

    public Iterator<T> getIterator();
    
    /**
     * * Task: Adds a new entry to the queue. * * @param newEntry
     * an object to be added * @param addFront true to add to the front, false to add to the back
     */
    public void enqueue(T newEntry, boolean front);

    /**
     * * Task: Removes and returns the entry from the queue.
     *
     * * @param takeFront true to remove from the front, false to remove from the back
     * @return either the object at the front or back of the queue or, if the queue is *
     * empty before the operation, null
     */
    public T dequeue(boolean takeFront);

    /**
     * * Task: Retrieves the entry at the front of the queue. * * @return
     * either the object at the front of the queue or, if the queue is * empty,
     * null
     */
    public T getFront();
    
    /**
     * * Task: Retrieves the entry at the back of the queue. * * @return
     * either the object at the back of the queue or, if the queue is * empty,
     * null
     */
    public T getBack();

    /**
     * * Task: Detects whether the queue is empty. * * @return true if the
     * queue is empty, or false otherwise
     */
    public boolean isEmpty();

    /**
     * * Task: Removes all entries from the queue.
     */
    public void clear();

}
