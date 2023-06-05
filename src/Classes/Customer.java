/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;


import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import tagihanapp_alexadrr.MyConnection;



/**
 *
 * @author arzaq
 */
public class Customer extends User {

    public Customer(String name, String email, String gender, LocalDate dateOfBirth, String password, String phone, String address) {
        super(name, email, gender, dateOfBirth, password, phone, address);
    }

    public boolean insertIntoDatabase() {
    String query = "INSERT INTO customer (Name, Email, Gender, DOB, Password, Phone, Alamat) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement ps = MyConnection.getConnection().prepareStatement(query)) {
        ps.setString(1, getName());
        ps.setString(2, getEmail());
        ps.setString(3, getGender());
        ps.setObject(4, getDateOfBirth());
        ps.setString(5, getPassword());
        ps.setString(6, getPhone());
        ps.setString(7, getAddress());

        return ps.executeUpdate() > 0;
    } catch (SQLException ex) {
        Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        return false;
    }
}

}