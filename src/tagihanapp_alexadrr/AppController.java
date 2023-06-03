/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tagihanapp_alexadrr;
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
        } else if (password.equals("")) {
            JOptionPane.showMessageDialog(null, "Add A Password");
        } else if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(null, "Retype The Password Again");
        } else if (registerForm.getjDateChooser().getDate() != null) {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            bdate = dateformat.format(registerForm.getjDateChooser().getDate());
        }

        PreparedStatement ps;
        String query = "INSERT INTO customer (Name, Email, Gender, DOB, Password, Phone, Alamat) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            ps = MyConnection.getConnection().prepareStatement(query);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, gender);
            if (bdate != null) {
                ps.setString(4, bdate);
            } else {
                ps.setNull(4, 0);
            }
            ps.setString(5, password);
            ps.setString(6, phone);
            ps.setString(7, address);

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "New User Added");
            }
        } catch (SQLException ex) {
            Logger.getLogger(RegisterForm.class.getName()).log(Level.SEVERE, null, ex);
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

        listBillForm.setVisible(true);
    }


    
    public void openProfileForm(String name) {
        Profile profileForm = new Profile();
        profileForm.setLocationRelativeTo(null);
    
        profileForm.setNameText(name);
        profileForm.setVisible(true);
    }
    
    public void performLogout(){
        LoginForm loginForm = new LoginForm();
        loginForm.setLocationRelativeTo(null);
        loginForm.setVisible(true);
    }
    
    public void performAddBill(String billType, double amount, Date dueDate, Date paymentDate) {
    // Get the customer ID of the logged-in customer
    
        int customerId = 1;

        // Check if the customer ID is valid
        if (!isCustomerIdValid(customerId)) {
            JOptionPane.showMessageDialog(null, "Invalid customer ID");
            return;
        }

        String query = "INSERT INTO bill (customer_id, bill_type, amount, due_date, paid, payment_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = MyConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, customerId);
            ps.setString(2, billType);
            ps.setDouble(3, amount);
            ps.setString(4, new SimpleDateFormat("yyyy-MM-dd").format(dueDate));
            ps.setBoolean(5, false); // Assuming 'paid' is a boolean column and setting it to false initially
            ps.setString(6, new SimpleDateFormat("yyyy-MM-dd").format(paymentDate));

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

    

