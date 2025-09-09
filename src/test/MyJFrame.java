package test;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyJFrame extends JFrame implements KeyListener {

    JButton jb = new JButton("😈");

    public MyJFrame(){
        this.setSize(603, 680);
        this.setTitle("keylistener");
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(3);


        jb.setSize(100, 100);
        jb.addKeyListener(this);

        this.add(jb);

        this.setVisible(true);
    }
    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println("typed:" + e.getKeyChar());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("press " + e.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println("released:" + e.getKeyChar() );
    }
}
