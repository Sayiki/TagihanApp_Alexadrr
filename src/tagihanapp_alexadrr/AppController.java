/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tagihanapp_alexadrr;
import Classes.Customer;
import java.awt.Frame;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.time.LocalDate;


/**
 *
 * @author arzaq
 */
public class AppController implements ActionListener {
    private LoginForm loginForm;
    private RegisterForm registerForm;
    private int loggedInCustomerId;

    public AppController(LoginForm loginForm, RegisterForm registerForm) {
        this.loginForm = loginForm;
        this.registerForm = registerForm;

    }
    
    public int getLoggedInCustomerId() {
        return loggedInCustomerId;
    }
    
    public void setLoggedInCustomerId(int customerId) {
        loggedInCustomerId = customerId;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginForm.getjLogin()) {
            performLogin();
        } else if (e.getSource() == registerForm.getjRegister()) {
            performRegister();
        }
    }
    
    public void performLogin() {
        String email = loginForm.getjEmail().getText();
        String password = String.valueOf(loginForm.getjPasswordField1().getPassword());

        String query = "SELECT * FROM `customer` WHERE `Email` = ? AND `Password` = ?";

        try {
            PreparedStatement ps = MyConnection.getConnection().prepareStatement(query);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Login Success!");
                loginForm.dispose();

                Dashboard dashboard = new Dashboard();
                dashboard.setLocationRelativeTo(null);


                // Set the text of the displayname JLabel
                dashboard.setDisplayNameText(rs.getString("Name"));
                dashboard.setDisplayCID(rs.getInt("id"));
                
                setLoggedInCustomerId(rs.getInt("id"));

                // Show the dashboard
                dashboard.setVisible(true);

            } else {
                JOptionPane.showMessageDialog(null, "Incorrect Email or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void performRegister() {
        RegisterForm rgf = new RegisterForm();
        rgf.setVisible(true);
        rgf.pack();
        rgf.setLocationRelativeTo(null);
        rgf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String email = registerForm.getjEmail().getText();
        String name = registerForm.getjName().getText();
        String phone = registerForm.getjPhone().getText();
        String address = registerForm.getjAddress().getText();
        String password = String.valueOf(registerForm.getjPassword().getPassword());
        String confirmPassword = String.valueOf(registerForm.getjConfirmPassword().getPassword());
        String bdate = null;
        String gender;

        if (registerForm.getjMale().isSelected()) {
            gender = "Male";
        } else if (registerForm.getjFemale().isSelected()) {
            gender = "Female";
        } else {
            gender = "Unknown";
        }

        if (name.equals("")) {
            JOptionPane.showMessageDialog(null, "Add A name");
            return; // Return if there is an error
        } else if (password.equals("")) {
            JOptionPane.showMessageDialog(null, "Add A Password");
            return; // Return if there is an error
        } else if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(null, "Retype The Password Again");
            return; // Return if there is an error
        } else if (registerForm.getjDateChooser().getDate() != null) {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            bdate = dateformat.format(registerForm.getjDateChooser().getDate());
        }

        Customer customer = new Customer(name, email, gender, LocalDate.parse(bdate), password, phone, address);

        // Insert the customer into the database
        if (customer.insertIntoDatabase()) {
            JOptionPane.showMessageDialog(null, "New User Added");
            rgf.dispose(); 
            LoginForm lgf = new LoginForm(); 
            lgf.setVisible(true);
            lgf.pack();
            lgf.setLocationRelativeTo(null);
            lgf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }


    
    public void openListBillForm() {
        ListBill listBillForm = new ListBill();
        listBillForm.setLocationRelativeTo(null);

        try {
            Connection con = MyConnection.getConnection();
            String query = "SELECT customer_id, bill_type, due_date FROM bill WHERE customer_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, loggedInCustomerId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int customerId = rs.getInt("customer_id");
                listBillForm.setDisplayCID(customerId);
                listBillForm.setDisplayBillText(rs.getString("bill_type"));
                listBillForm.setDisplayDueDateText(rs.getString("due_date"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        listBillForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        listBillForm.setVisible(true);
    }


    
    public void openProfileForm(String name) {
        Profile profileForm = new Profile();
        profileForm.setLocationRelativeTo(null);
    
        profileForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        profileForm.setNameText(name);
        profileForm.setVisible(true);
    }
    
    public void performLogout() {
        Frame[] frames = Frame.getFrames();
        LoginForm lf = new LoginForm();
        
        for (Frame frame : frames) {
            if (frame instanceof JFrame && frame != lf) {
                ((JFrame) frame).dispose();
            }
        }
    }

    
    public void performAddBill(String password, String billType, int amount, Date dueDate) {
        // Get the customer ID of the logged-in customer
    
        int customerId = getCustomerID(password);
        

        // Check if the customer ID is valid
        if (!isCustomerIdValid(customerId)) {
            JOptionPane.showMessageDialog(null, "Invalid customer ID");
            return;
        }

        String query = "INSERT INTO bill (customer_id, bill_type, amount, due_date, paid) " +
                        "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = MyConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, customerId);
            ps.setString(2, billType);
            ps.setInt(3, amount);
            ps.setString(4, new SimpleDateFormat("yyyy-MM-dd").format(dueDate));
            ps.setBoolean(5, false); // Assuming 'paid' is a boolean column and setting it to false initially

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int billId = generatedKeys.getInt(1);
                    JOptionPane.showMessageDialog(null, "Successfully added. Bill ID: " + billId);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to retrieve the generated bill ID");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add bill");
            }

            ps.close();
            MyConnection.getConnection().close();

        } catch (SQLException ex) {
            Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public void performOpenAddBillForm(){
        NewBill nb = new NewBill();
        nb.setLocationRelativeTo(null);
        nb.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        nb.setVisible(true);
        
        Dashboard db = new Dashboard();
        nb.setDisplayCID(loggedInCustomerId);
        
        
        
        
    }
    
    public void openPayment() {
        Payment paymentForm = new Payment();
        paymentForm.setLocationRelativeTo(null);

        // Retrieve the bill price and bill ID from the database
        int billPrice = getBillPriceFromDatabase();

        // Set the bill price in the JPrice label
        paymentForm.getJPrice().setText(String.valueOf(billPrice));
        paymentForm.getjLabel12().setText(String.valueOf(billPrice));
        paymentForm.getjLabel8().setText(String.valueOf(billPrice));

        paymentForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        paymentForm.setVisible(true);
    }
    
    
    
    public int getBillPriceFromDatabase() {
        int billPrice = 0;
        int billId = 0;
        int customerId = getLoggedInCustomerId(); // Assuming you have a method to retrieve the logged-in customer ID

        String query = "SELECT id, amount FROM bill WHERE customer_id = ?"; // Modify the query to retrieve the bill ID

        try {
            PreparedStatement ps = MyConnection.getConnection().prepareStatement(query);
            ps.setInt(1, customerId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                billId = rs.getInt("id"); // Retrieve the bill ID
                billPrice = rs.getInt("amount");
            }

            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Do something with the bill ID if needed

        return billPrice;
    }
    
    public void performPay(int id) {
        int billId = 1; // Assuming you have a method to retrieve the bill ID

        try {
            // Update the paid column to true
            String query = "UPDATE bill SET paid = 1 WHERE id = ?";
            PreparedStatement ps = MyConnection.getConnection().prepareStatement(query);

            // Set the bill ID for the update
            ps.setInt(1, billId);

            // Execute the update query
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                // Payment update successful
                System.out.println("Payment successful!");
            } else {
                // Payment update failed
                System.out.println("Payment failed!");
            }

            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }



    



    
    public int getCustomerID(String password) {
        String query = "SELECT * FROM `customer` WHERE `Password` = ?";
        int customerID = 0;

        try {
            PreparedStatement ps = MyConnection.getConnection().prepareStatement(query);
            ps.setString(1, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                customerID = rs.getInt("id");
            }

            rs.close();
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return customerID;
    }






    private boolean isCustomerIdValid(int customerId) {
        String query = "SELECT customer_id FROM bill WHERE customer_id = ?";

        try {
            PreparedStatement ps = MyConnection.getConnection().prepareStatement(query);
            ps.setInt(1, customerId);

            ResultSet rs = ps.executeQuery();

            // If a row is returned, the customer ID is valid
            boolean isValid = rs.next();

            rs.close();
            ps.close();
            return isValid;

        } catch (SQLException ex) {
            Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }
}

    

