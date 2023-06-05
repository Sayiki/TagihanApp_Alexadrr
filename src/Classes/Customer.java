/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;
import java.time.LocalDate;
/**
 *
 * @author arzaq
 */
public class Customer extends User {
    private static int customerIdCounter = 1;
    private int customerId;

    public Customer(String name, String email, String gender, LocalDate dateOfBirth, String password, String phone, String address) {
        super(name, email, gender, dateOfBirth, password, phone, address);
        this.customerId = customerIdCounter;
        customerIdCounter++;
    }

    public int getCustomerId() {
        return customerId;
    }
}