package test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class test {
    public static void main(String[] args) {
        JFrame jf = new JFrame("test");
        jf.setSize(603, 680);
        jf.setDefaultCloseOperation(3);
        jf.setLocationRelativeTo(null);
        jf.setAlwaysOnTop(true);
        jf.setLayout(null);

        JButton jb1 = new JButton("hit me!");
        JButton jb2 = new JButton("dont hit!");
        //create event
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object o = e.getSource();
                if (o == jb1) {
                    jb1.setSize(jb1.getWidth() + 10, jb1.getHeight() + 10);
                } else if (o == jb2) {
                    Random r = new Random();
                    jb2.setLocation(r.nextInt(400), r.nextInt(400));
                }

            }
        };

        jb1.setBounds(0, 0, 100, 50);
        jb1.addActionListener(al);
        jb2.setBounds(0, 50, 100, 50);
        jb2.addActionListener(al);
        jf.getContentPane().add(jb1);
        jf.getContentPane().add(jb2);


        jf.setVisible(true);
    }
}
