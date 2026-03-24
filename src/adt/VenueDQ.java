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
public class VenueDQ<T> extends LinkedDeque<T> implements Iterable<T> {
    public T find(String venueId){
        if (venueId == null || isEmpty()){
            return null;
        }
        
        Node<T> current = head;
        while (current != null){
            if (current.data instanceof Venue v){
                if (venueId.equals(v.getVenueId())){
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
        public boolean hasNext(){
            return current != null;
        }
        
        @Override
        public T next(){
            if(!hasNext()){
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
    
    private class ReverseDequeIterator implements Iterator<T>{
        private Node<T> current = tail;
        
        @Override
        public boolean hasNext(){
            return current != null;
        }
        
        @Override
        public T next(){
            if(!hasNext()){
                throw new NoSuchElementException("No more elements");
            }
            
            T data = current.data;
            current = current.prev;
            return data;
        }
    }
}
