/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 *
 * @author ratna
 */
public class AddServlet extends HttpServlet {

   
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int f=0;
        
        String prod= request.getParameter("Pname");
        int qty=Integer.parseInt(request.getParameter("pqty"));
        HttpSession session = request.getSession();
        ArrayList<Cart> x= (ArrayList<Cart>)session.getAttribute("x");
        
        if(x==null){
        x=new ArrayList<>();
        }
        
        for(Cart item:x)   // check product name ....
        {
         if(item.getPname().equals(prod)){
           item.qty=item.getQty()+qty;
           f=0;
           break;
           
           
           
          } 
        }
        
        if(f==0){
        
          int rate;
          if(prod.equals("Orange"))
          {
           rate=20;
          } 
          
          else if(prod.equals("banana")){
           rate= 5;
          }
          else {
            rate=60;
          }
          
          Cart y= new Cart(prod, qty,rate);
          x.add(y);
        }
        session.setAttribute("x", x);
        
        
        
        
        
        PrintWriter pw= response.getWriter();
        
        
        
        pw.print("Product added succesfullyl");
       pw.print("<a href='index.html'>Continue shopping</a>");
//        pw.print("Product"+prod);
//        pw.print("Product"+qty);
        
        
        
    }

}
