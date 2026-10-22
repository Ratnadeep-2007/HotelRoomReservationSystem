/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exception_dsa;

import java.util.LinkedList;

/**
 *
 * @author ratna
 */
public class LinkList {
 
    public static void main(String[] args){
    LinkedList<String> x = new LinkedList<>();
    x.addFirst("4");

    x.addFirst("5");
    System.out.println(x);
    x.addLast("6");
    System.out.println(x);
    x.removeFirst();
   x.add(1, "element");
    System.out.println(x);
    
    }
    
    
}
