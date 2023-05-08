/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author arzaq
 */
public class CustomerService {
    private List<User> customers;

    public CustomerService() {
        this.customers = new ArrayList<>();
    }

    public void addCustomer(User customer) {
        customers.add(customer);
    }

    // getters and setters
}
