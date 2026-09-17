package ui;

import domain.User;
import tool.CodeUtil;
import tool.DataAccess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


public class LoginJFrame extends JFrame implements MouseListener, ActionListener {
    public static String default_path = "/Users/peeko/IdeaProjects/puzzlegame/pictures/";
    public static DataAccess dataAccess = new DataAccess();
    public static ArrayList<User> allUsers = (ArrayList<User>) dataAccess.getAllUsers();



    JButton refresh = new JButton();
    JButton login = new JButton();
    JButton register = new JButton();

    JTextField username = new JTextField();
    JPasswordField password = new JPasswordField();
    JTextField code = new JTextField();

    //right code
    JLabel rightCode = new JLabel();

    public LoginJFrame(ArrayList<User> users) {
        LoginJFrame.allUsers = users;
        initJFrame();
        initView();
        this.setVisible(true);
    }

    public LoginJFrame(String path) {
        LoginJFrame.default_path = path;
        initJFrame();
        initView();
        this.setVisible(true);
    }


    public void initView() {

        //add user text
        JLabel usernameText = new JLabel(new ImageIcon(LoginJFrame.default_path + "login/用户名.png"));
        usernameText.setBounds(116, 138, 47, 17);
        this.getContentPane().add(usernameText);

        //add username textfield

        username.setBounds(195, 134, 200, 30);
        this.getContentPane().add(username);

        //add password text
        JLabel passwordText = new JLabel(new ImageIcon(LoginJFrame.default_path + "login/密码.png"));
        passwordText.setBounds(130, 201, 32, 16);
        this.getContentPane().add(passwordText);

        //add password text field
        password.setBounds(195, 195, 200, 30);
        this.getContentPane().add(password);

        //code refresh button
        refresh.setBounds(346, 259, 25, 25);
        refresh.setIcon(new ImageIcon(LoginJFrame.default_path + "sport/icon1.png"));
        refresh.setBorderPainted(false);
        refresh.setContentAreaFilled(false);
        refresh.addMouseListener(this);
        refresh.setFocusPainted(false);
        this.getContentPane().add(refresh);

        //code text
        JLabel codeText = new JLabel(new ImageIcon(LoginJFrame.default_path + "login/验证码.png"));
        codeText.setBounds(112, 256, 50, 30);
        this.getContentPane().add(codeText);

        //code text field
        code.setBounds(195, 256, 100, 30);
        this.getContentPane().add(code);

        // set code
        String codeStr = CodeUtil.getCode();
        rightCode.setText(codeStr);
        rightCode.addMouseListener(this);
        rightCode.setBounds(300, 256, 50, 30);
        this.getContentPane().add(rightCode);

        //add login button
        login.setBounds(123, 310, 128, 47);
        login.setIcon(new ImageIcon(LoginJFrame.default_path + "login/登录按钮.png"));
        login.setBorderPainted(false);
        login.setContentAreaFilled(false);
        login.addActionListener(this);
        this.getContentPane().add(login);

        // add register button
        register.setBounds(256, 310, 128, 47);
        register.setIcon(new ImageIcon(LoginJFrame.default_path + "login/注册按钮.png"));
        register.setBorderPainted(false);
        register.setContentAreaFilled(false);
        register.addMouseListener(this);
        this.getContentPane().add(register);


        // add background picture
        JLabel background = new JLabel(new ImageIcon(LoginJFrame.default_path + "login/background.png"));
        background.setBounds(0, 0, 470, 390);
        this.getContentPane().add(background);

    }


    public void initJFrame() {
        this.setSize(478, 420);
        this.setTitle("拼图游戏 登陆");
        this.setDefaultCloseOperation(3);
        this.setLocationRelativeTo(null);
        this.setAlwaysOnTop(true);
        this.setLayout(null);
        this.getRootPane().setDefaultButton(login);
        this.setResizable(false);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.getSource() == register) {
            System.out.println("点击了注册按钮");
            this.setVisible(false);
            new RegisterJFrame();
            this.dispose();
        } else if (e.getSource() == rightCode || e.getSource() == refresh) {
            System.out.println("更换验证码");
            //获取一个新的验证码
            String code = CodeUtil.getCode();
            rightCode.setText(code);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == login) {
            login.setIcon(new ImageIcon(LoginJFrame.default_path + "login/登录按下.png"));
        } else if (e.getSource() == register) {
            register.setIcon(new ImageIcon(LoginJFrame.default_path + "login/注册按下.png"));
        }
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() == login) {
            login.setIcon(new ImageIcon(LoginJFrame.default_path + "login/登录按钮.png"));
        } else if (e.getSource() == register) {
            register.setIcon(new ImageIcon(LoginJFrame.default_path + "login/注册按钮.png"));
        }
    }


    @Override
    public void mouseEntered(MouseEvent e) {

    }


    @Override
    public void mouseExited(MouseEvent e) {

    }


    public boolean matches(User userInput) {
        for (int i = 0; i < allUsers.size(); i++) {
            User rightUser = allUsers.get(i);
            if (userInput.getUsername().equals(rightUser.getUsername()) && userInput.getPassword().equals(rightUser.getPassword())) {
                return true;
            }
        }

        return false;
    }

    public void tryLogin() {
        //get the content in the text field
        String usernameInput = username.getText();
        String passwordInput = password.getText();
        //get the user input code
        String codeInput = code.getText();

        // create a new user
        User userInfo = new User(usernameInput, passwordInput);
        System.out.println("用户输入的用户名为" + usernameInput);
        System.out.println("用户输入的密码为" + passwordInput);

        //check the correctness of username and password
        if (codeInput.length() == 0) {
            showJDialog("验证码不能为空!😠");
        } else if (usernameInput.length() == 0 || passwordInput.length() == 0) {
            System.out.println("用户名或者密码为空🤔");
            showJDialog("用户名或者密码为空🤔");
        } else if (!codeInput.equalsIgnoreCase(rightCode.getText())) {
            showJDialog("验证码输入错误❌");
        } else if (matches(userInfo)) {
            System.out.println("用户名和密码正确");
            this.setVisible(false);
            User current_user = findUser(userInfo.getUsername());
            new GameJFrame(current_user);
            this.dispose();
        } else {
            System.out.println("用户名或密码错误😡😡");
            showJDialog("用户名或密码错误😡😡");
            String code = CodeUtil.getCode();
            rightCode.setText(code);
        }
    }

    public void showJDialog(String content) {
        JDialog jDialog = new JDialog();
        jDialog.setSize(200, 150);
        jDialog.setAlwaysOnTop(true);
        jDialog.setLocationRelativeTo(null);
        jDialog.setModal(true);

        JLabel warning = new JLabel(content);
        warning.setBounds(0, 0, 200, 150);
        jDialog.getContentPane().add(warning);

        jDialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == login) {
            tryLogin();
        }
    }

    private User findUser(String username) {
        for (User user : allUsers) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

}




