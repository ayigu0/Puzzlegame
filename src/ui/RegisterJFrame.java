package ui;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import domain.User;
import tool.DataAccess;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class RegisterJFrame extends JFrame implements ActionListener {
    private DataAccess dataAccess = LoginJFrame.dataAccess;
    static ArrayList<User> reg_user = LoginJFrame.allUsers;


    int register_x = 70;
    int register_y = 310;
    int reset_x = this.register_x + 135;
    int reset_y = this.register_y;

    JLabel userhint = new JLabel("用户名长度至少要5位哦～");
    JLabel passwordhint = new JLabel("密码长度至少要8位哦～");
    JLabel usernamereg = new JLabel(new ImageIcon(LoginJFrame.default_path + "register/注册用户名.png"));
    JLabel passwordlabel = new JLabel(new ImageIcon(LoginJFrame.default_path + "register/注册密码.png"));
    JLabel reenterpasslabel = new JLabel(new ImageIcon(LoginJFrame.default_path + "register/再次输入密码.png"));

    JTextField username = new JTextField();
    JTextField password = new JTextField();
    JTextField repassword = new JTextField();

    JButton back = new JButton();
    JButton reg_button = new JButton();
    JButton reset_button = new JButton();

    public RegisterJFrame() {
        initFrame();
        //add contents into frame
        initview();

        this.setVisible(true);
    }

    public void initFrame() {
        this.setSize(470, 435);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setAlwaysOnTop(true);
        this.setTitle("注册");
        this.setResizable(false);
        this.setDefaultCloseOperation(3);
    }

    private void initview() {
        //user, password hint label
        passwordhint.setBounds(180, 225, 210, 20);
        passwordhint.setForeground(Color.RED);
        passwordhint.setVisible(false);
        userhint.setBounds(180, 168, 210, 20);
        userhint.setForeground(Color.RED);
        userhint.setVisible(false);
        this.getContentPane().add(userhint);
        this.getContentPane().add(passwordhint);

        // reigster label
        usernamereg.setBounds(85, 140, 79, 17);
        this.getContentPane().add(usernamereg);
        // register user text frame
        username.setBounds(180, 135, 210, 30);
        username.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.getContentPane().add(username);
        //password label
        passwordlabel.setBounds(98, 200, 64, 16);
        this.getContentPane().add(passwordlabel);

        // password text
        password.setBounds(180, 195, 210, 30);
        password.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.getContentPane().add(password);

        // repassword label
        reenterpasslabel.setBounds(68, 260, 96, 17);
        this.getContentPane().add(reenterpasslabel);

        //repassword text
        repassword.setBounds(180, 255, 210, 30);
        repassword.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.getContentPane().add(repassword);

        //register button
        reg_button.setIcon(new ImageIcon(LoginJFrame.default_path + "register/注册按钮.png"));
        reg_button.setBounds(register_x, register_y, 128, 47);
        reg_button.setBorderPainted(false);
        reg_button.setContentAreaFilled(false);
        this.reg_button.addActionListener(this);
        this.getContentPane().add(reg_button);

        //reset button
        reset_button.setIcon(new ImageIcon(LoginJFrame.default_path + "register/重置按钮.png"));
        reset_button.setBounds(this.reset_x, this.reset_y, 128, 47);
        reset_button.setBorderPainted(false);
        reset_button.setContentAreaFilled(false);
        this.reset_button.addActionListener(this);
        this.getContentPane().add(reset_button);

        //backtologin button
        back.setIcon(new ImageIcon(LoginJFrame.default_path + "sport/frog.png"));
        back.setBounds(this.reset_x + 148, this.reset_y - 10, 50, 50);
        back.setContentAreaFilled(false);
        back.setBorderPainted(false);
        back.addActionListener(this);
        back.setFocusPainted(false);
        this.getContentPane().add(back);

        // load background and add
        JLabel bg = new JLabel(new ImageIcon(LoginJFrame.default_path + "register/background.png"));
        bg.setBounds(0, 0, 470, 390);
        this.getContentPane().add(bg);


    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == reg_button) {
            System.out.println("点了注册界面的注册按钮");
            try_register();
        } else if (e.getSource() == reset_button) {
            System.out.println("点了重置按钮");
            //clean everything and reset;
            username.setText("");
            password.setText("");
            repassword.setText("");
        } else if (e.getSource() == back) {
            System.out.println("返回登陆界面");
            this.setVisible(false);
            this.dispose();
            new LoginJFrame(RegisterJFrame.reg_user);
            this.dispose();
        }
    }

    public void try_register() {

        String user = this.username.getText();
        String password = this.password.getText();
        String repassword = this.repassword.getText();
        int userlen = user.length();
        int passwordlen = password.length();
        if (userlen == 0 && passwordlen == 0 && repassword.length() == 0) {
            System.out.println("啥都没输入");
            showDialog("你似乎什么都没有输入😨😨😨😨");
        } else if (userlen == 0) {
            System.out.println("用户名是空的");
            showDialog("用户名不能为空😨😨😨😨");
        } else if (passwordlen == 0) {
            System.out.println("密码是空的");
            showDialog("密码不能为空😈😈😈😈");
        } else if (repassword.length() == 0) {
            System.out.println("请重新输入密码🤔🤔🤔🤔");
            showDialog("请重新输入密码😕😕😕😕");
        } else if (!password.equals(repassword)) {
            System.out.println("两次密码不一样");
            showDialog("两次密码输入不一致❓❓❓❓");
        } else if (userlen < 5) {
            System.out.println("用户名太短");
            this.userhint.setVisible(true);
        } else if (passwordlen < 8) {
            System.out.println("密码太短");
            this.passwordhint.setVisible(true);
        } else if (userNameContains(user)) {
            System.out.println("用户已经存在");
            showDialog("用户已存在！！😖😖😖😖");
        } else {
            RegisterJFrame.reg_user.add(new User(user, password));
            LoginJFrame.dataAccess.registerUser(user, password);
            this.setVisible(false);
            new LoginJFrame(RegisterJFrame.reg_user);
            this.dispose();
        }


    }

    private void showDialog(String s) {
        JDialog popwindow = new JDialog();
        popwindow.setSize(220, 170);
        popwindow.setAlwaysOnTop(true);
        popwindow.setLocationRelativeTo(null);
        popwindow.setModal(true);

        JLabel warning = new JLabel();
        warning.setText(s);
        popwindow.add(warning);
        popwindow.setVisible(true);

    }

    private boolean userNameContains(String s) {
        ArrayList<User> data = RegisterJFrame.reg_user;
        for (User user : data) {
            if (s.equals(user.getUsername())) {
                return true;
            }
        }
        return false;
    }
}


