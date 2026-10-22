/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exception_dsa;

/**
 *
 * @author ratna
 */
public class stiatic_nonstatic {
    
    void disp(){
      System.out.println("Welcome disp");
        show();
        demo();
    }
    
    public void show(){
    System.out.println("welcome show");
    }
    
    public static void demo(){
   System.out.println("welcome demo");
     
   
    }
    
    public static void main(String[] args){
    stiatic_nonstatic sn = new stiatic_nonstatic();
    sn.disp();
    demo();
    }
    
    
    
}
