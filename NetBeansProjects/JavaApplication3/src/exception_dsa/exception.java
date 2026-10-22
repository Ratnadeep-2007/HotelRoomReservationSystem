/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package exception_dsa;
import java.io.*;
import java.util.*;
/**
 *
 * @author ratna
 */
public class exception {
    
    public static void main(String[] Args){
       System.out.println("enter 2 nums");
        try{
            InputStreamReader isr=new InputStreamReader(System.in);
       BufferedReader br = new BufferedReader(isr); 
      
       int a,b;
       String str;
       a=Integer.parseInt(br.readLine());
       b=Integer.parseInt(br.readLine());
       
       }
       
        catch(NumberFormatException e){
        System.out.print(e.toString());
        }
        
        
           catch(ArithmeticException e){
        System.out.print(e.toString());
        }
        
          catch(IOException e){
        System.out.print(e.toString());
        }
        
        catch (Exception e){
        System.out.println(e.toString());
        }
       
      
       
       
       
    }
    
}
