/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tagihanapp_alexadrr;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author arzaq
 */
public class AppController implements ActionListener {
    private LoginForm loginForm;
    private RegisterForm registerForm;

    public AppController(LoginForm loginForm, RegisterForm registerForm) {
        this.loginForm = loginForm;
        this.registerForm = registerForm;

        if (loginForm != null) {
            this.loginForm.getjLogin().addActionListener(this);
        }

        if (registerForm != null) {
            this.registerForm.getjRegister().addActionListener(this);
        }
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

        String query = "SELECT * FROM `customer` WHERE `Email` =? AND `Password` =?";

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
                dashboard.setDisplayNameText(rs.getString(1));

                // Show the dashboard
                dashboard.setVisible(true);

            } else {
                JOptionPane.showMessageDialog(null, "Incorrect Email Or Password", "Login Failed", 2);
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



    
}

