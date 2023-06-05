/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;
import java.util.Date;

/**
 *
 * @author arzaq
 */
public class Bill {
    private int id;
    private int customer_id;
    private String bill_type;
    private int amount;
    private Date due_date;
    private boolean paid;

    public Bill(int id, int customer_id, String bill_type, int amount, Date due_date, boolean paid, Date payment_date) {
        this.id = id;
        this.customer_id = customer_id;
        this.bill_type = bill_type;
        this.amount = amount;
        this.due_date = due_date;
        this.paid = paid;
    }

}