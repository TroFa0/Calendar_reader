package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

public class App 
{
    public static void main( String[] args )
    {
        final Helper helper = Helper.getInstance();
        final JFrame frame = new JFrame("My First GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000,800);
        frame.setResizable(false);
        helper.setWindow(frame);
        //Creating the panel at bottom and adding components
        JPanel panel = new JPanel(); // the panel is not visible in output
        JLabel label = new JLabel("Введіть ім'я");
        final JTextField tf = new JTextField(10); // accepts upto 10 characters
        JButton send = new JButton("Login");
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    HashMap<String, String> map;
                    if(tf.getText().isEmpty())
                        throw new IOException();
                    else
                        map = CalendarQuickstart.getList(tf.getText());
                    helper.setUser(tf.getText());
                    helper.setMap(map);
                    helper.createPanel();
                } catch (IOException q) {
                    throw new RuntimeException(q);
                } catch (GeneralSecurityException q) {
                    throw new RuntimeException(q);
                }
            }
        });
        panel.add(label);
        panel.add(tf);
        panel.add(send);
        JPanel text = new JPanel();
        JLabel lText = new JLabel("Будь ласка, введіть ім'я користувача");
        text.add(lText);
        text.setBounds(0,0,1000,30);
        panel.setBounds(0,30,1000,30);
        frame.setLayout(null);
        frame.getContentPane().add(text);
        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }
}
