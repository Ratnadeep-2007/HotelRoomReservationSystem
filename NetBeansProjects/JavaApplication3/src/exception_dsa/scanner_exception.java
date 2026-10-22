/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exception_dsa;

import java.io.*;
import java.util.Scanner;

/**
 *
 * @author ratna
 */
public class scanner_exception {
      
    public static void main(String[] Args){
       System.out.println("enter 2 nums");
        try{
           Scanner sc=new Scanner(System.in);
      
       int a,b;
       String str;
       a= sc.nextInt();
       b=sc.nextInt();
       }
       
        catch(NumberFormatException e){
        System.out.print(e.toString());
        }
        
        
           catch(ArithmeticException e){
        System.out.print(e.toString());
        }
        
        catch (Exception e){
        System.out.println(e.toString());
        }
       
      
       
       
       
    }
}
