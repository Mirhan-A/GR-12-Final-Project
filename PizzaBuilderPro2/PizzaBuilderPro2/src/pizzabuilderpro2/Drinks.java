/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pizzabuilderpro2;

public class Drinks {
    
    private String name; 
    private double price; 
    
    
    public Drinks(String name, double price)
    {
        this.name = name; 
        this.price = price;
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public double getPrice() { 
        return price; 
    }
    
    // Setters 
   public void setName(String name)
   {
       this.name = name; 
   }
   
   public void setPrice(double price)
   {
       this.price = price; 
   }
   
   @Override 
   public String toString()
   {
       return name;
   }           
    
}
