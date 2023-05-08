/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;
import java.util.Date;
import java.util.ArrayList;
/**
 *
 * @author arzaq
 */
public class Bill {
    private int id;
    private User customer;
    private Date date;
    private List<Item> items;
    private double totalAmount;
    private Category category;

    public Bill(int id, User customer, Date date, Category category) {
        this.id = id;
        this.customer = customer;
        this.date = date;
        this.items = new ArrayList<>();
        this.category = category;
    }

    public void addItem(Item item) {
        items.add(item);
        totalAmount += item.getPrice();
    }

    // getters and setters
}