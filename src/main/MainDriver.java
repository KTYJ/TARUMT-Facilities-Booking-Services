/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

import adt.UserDQ;
import java.util.Iterator;

/**
 *
 * @author KTYJ
 */
public class MainDriver {

    /**
     * @param args the command line arguments
     */
    private static void testADT() {
        // this one gpt ya ltr just for testing the LinkedDeque implementation
        UserDQ<Integer> deque = new UserDQ<>();

        System.out.println("===  / addLast ===");
        deque.addLast(2);
        deque.addLast(3);
        deque.addFirst(1);
        deque.addFirst(0);
        System.out.println(deque); // [0, 1, 2, 3]
        System.out.println("size: " + deque.size());

        System.out.println("\n=== peek ===");
        System.out.println("peekFirst: " + deque.peekFirst()); // 0
        System.out.println("peekLast:  " + deque.peekLast()); // 3

        System.out.println("\n=== removeFirst / removeLast ===");
        System.out.println("removeFirst: " + deque.removeFirst()); // 0
        System.out.println("removeLast:  " + deque.removeLast()); // 3
        System.out.println(deque); // [1, 2]

        System.out.println("\n=== contains ===");
        System.out.println("contains(2): " + deque.contains(2)); // true
        System.out.println("contains(5): " + deque.contains(5)); // false

        System.out.println("\n=== reverse iterator ===");
        Iterator<Integer> rev = deque.reverseIterator();

        while (rev.hasNext()) {
            System.out.print(rev.next() + " "); // 2 1
        }
        System.out.println();

        System.out.println("\n=== clear ===");
        deque.clear();
        System.out.println("isEmpty: " + deque.isEmpty()); // true

    }

    public static void main(String[] args) {
        testADT();
        System.out.println("\nYou should be able to read this");
        
    }

}
