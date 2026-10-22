/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ratna
 */
public class Cart {
    String pname;
    int qty;
    int rate;

    public Cart(String a, int b, int c) {
        this.pname =a;
        this.qty = b;
        this.rate =c;
    }

//    Cart x= new Cart("Orange",10,100);
    public String getPname() {
        return pname;
    }

    public int getQty() {
        return qty;
    }   

    public int getRate() {
        return rate;
    }
}
