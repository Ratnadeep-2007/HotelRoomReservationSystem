/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package exception_dsa;

import java.util.ArrayList;

/**
 *
 * @author ratna
 */
public class dsa {
    
    public static void main (String[] Args){
       ArrayList<String> name=new ArrayList<>();
       
       name.add("hi");
       name.add("bye");
       name.add("see u soon");
       
//       String[] g= name.clone();
       System.out.println(name);
      
       int l=name.size();
       for(int i=0;i<l;i++){
         System.out.println(name.get(i));
        }
       
       
       name.add(2,"yes: replaced");
       
        
       
    
    }
}
