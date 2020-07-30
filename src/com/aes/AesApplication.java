package com.aes;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AesApplication extends JFrame implements ActionListener {
    JTextField tfKey, tfHexKey;
    JLabel lForImage, lForImage1, lForImage2, lEnterHexKey;
    JButton btnLoadImage, btnHex, btnEncode, btnViewImage1, btnViewImage2, btnDecode;
    JTextArea jTextArea1, jTextArea2;
    JScrollPane jScrollPane1, jScrollPane2;

    byte[] k = new byte[16];
    File selectedFile, encodedFile, decodedFile;
    byte[] b1 = new byte[0];

    public AesApplication() {

        btnLoadImage = new JButton("Pick Image");
        btnLoadImage.setBounds(100, 50, 100, 30);
        add(btnLoadImage);

        lForImage = new JLabel("Original image will be here ...");
        lForImage.setBounds(50, 100, 200, 150);
        add(lForImage);

        lEnterHexKey = new JLabel("Enter Plain Text Keys ...");
        lEnterHexKey.setBounds(280, 10, 250, 50);
        add(lEnterHexKey);

        tfKey = new JTextField();
        tfKey.setBounds(260, 50, 200, 30);
        add(tfKey);

        btnHex = new JButton("Hex >>");
        btnHex.setBounds(480, 50, 100, 30);
        add(btnHex);
        btnHex.setEnabled(false);

        tfHexKey = new JTextField("");
        tfHexKey.setBounds(600, 50, 300, 30);
        add(tfHexKey);
        tfHexKey.setEditable(false);

        btnEncode = new JButton("Encode >>");
        btnEncode.setBounds(270, 140, 100, 30);
        add(btnEncode);

        btnViewImage1 = new JButton("View Image >>");
        btnViewImage1.setBounds(270, 190, 100, 30);
        add(btnViewImage1);

        jTextArea1 = new JTextArea("");
        jTextArea1.setSize(200, 150);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setEditable(false);

        jScrollPane1 = new JScrollPane(jTextArea1);
        jScrollPane1.setBounds(400, 100, 200, 150);
        add(jScrollPane1);

        lForImage1 = new JLabel("Encoded Image will be here ...");
        lForImage1.setBounds(700, 100, 200, 150);
        add(lForImage1);

        btnDecode = new JButton("Decode >>");
        btnDecode.setBounds(270, 340, 100, 30);
        add(btnDecode);

        btnViewImage2 = new JButton("View Image >>");
        btnViewImage2.setBounds(270, 390, 100, 30);
        add(btnViewImage2);

        jTextArea2 = new JTextArea("");
        jTextArea2.setSize(200, 150);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setEditable(false);

        jScrollPane2 = new JScrollPane(jTextArea2);
        jScrollPane2.setBounds(400, 300, 200, 150);
        add(jScrollPane2);

        lForImage2 = new JLabel("Decoded Image will be here ...");
        lForImage2.setBounds(700, 300, 200, 150);
        add(lForImage2);

        btnEncode.setEnabled(false);
        btnViewImage1.setEnabled(false);
        btnDecode.setEnabled(false);
        btnViewImage2.setEnabled(false);

        btnLoadImage.addActionListener(this);
        btnHex.addActionListener(this);
        btnEncode.addActionListener(this);
        btnViewImage1.addActionListener(this);
        btnDecode.addActionListener(this);
        btnViewImage2.addActionListener(this);

        setTitle("AES Algorithm");
        setSize(1000, 600);
        setLayout(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLoadImage) {
            loadImage();
            btnHex.setEnabled(true);
        } else if (e.getSource() == btnHex) {
            if (tfKey.getText().trim().length() > 0) {
                tfHexKey.setText(toHexString(tfKey.getText().trim().getBytes()));
                btnEncode.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(null, "Plain text key must not be blank.");
            }

        } else if (e.getSource() == btnEncode) {
            k = tfKey.getText().trim().getBytes();
            if (isHex(tfHexKey.getText().trim()) && tfHexKey.getText().trim().length() == 48) {
                encodeImage();
                btnViewImage1.setEnabled(true);
                btnDecode.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(null, "Key Hex value must be 54 hexadecimal digits.");
            }
        } else if (e.getSource() == btnViewImage1) {
            lForImage1.setIcon(ResizeImage(encodedFile.getAbsolutePath()));

        } else if (e.getSource() == btnDecode) {
            decodeImage();
            btnViewImage2.setEnabled(true);
        } else if (e.getSource() == btnViewImage2) {
            lForImage2.setIcon(ResizeImage(decodedFile.getAbsolutePath()));
        }
    }


    private void loadImage() {
        JFileChooser file = new JFileChooser();
        file.setCurrentDirectory(new File(System.getProperty("user.home")));
        //filter the files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg", "gif", "png");
        file.addChoosableFileFilter(filter);
        int result = file.showSaveDialog(null);
        //if the user click on save in Jfilechooser
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = file.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            lForImage.setIcon(ResizeImage(path));
        }
        //if the user click on save in Jfilechooser

        else if (result == JFileChooser.CANCEL_OPTION) {
            System.out.println("No File Select");
        }
    }


    public ImageIcon ResizeImage(String ImagePath) {
        ImageIcon MyImage = new ImageIcon(ImagePath);
        Image img = MyImage.getImage();
        Image newImg = img.getScaledInstance(lForImage.getWidth(), lForImage.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);
        return image;
    }

    public void encodeImage() {

        try {
            BufferedImage image = ImageIO.read(selectedFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] b = baos.toByteArray();
            byte[] b2 = new byte[b.length - 620];

            for (int i = 0; i < (b2.length); i++)
                b2[i] = b[i + 620];
            b2 = AES.encrypt(b2, k, 10);
            b1 = new byte[b2.length + 620];
            for (int i = 0; i < b1.length; i++) {
                if (i < 620) b1[i] = b[i];
                else b1[i] = b2[i - 620];
            }
            encodedFile = new File("image/encryptedimage.jpg");
            FileOutputStream fos = new FileOutputStream(encodedFile);
            fos.write(b1);
            fos.flush();
            fos.close();

            jTextArea1.setText(toHexString(b1));
        }
        //work with the image here ...

        catch (IOException e) {
            System.out.println(e.getMessage());

        }
    }

    private void decodeImage() {

        try {
            BufferedImage image = ImageIO.read(selectedFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] b = baos.toByteArray();
            byte[] b2 = new byte[b.length - 620];

            for (int i = 0; i < b2.length; i++)
                b2[i] = b1[i + 620];
            b2 = AES.decrypt(b2, k, 10);

            decodedFile = new File("image/decryptedimage.jpg");
            FileOutputStream fos = new FileOutputStream(decodedFile);
            fos.write(b);
            fos.flush();
            fos.close();
            jTextArea2.setText(toHexString(b2));

        }
        //work with the image here ...

        catch (IOException e) {
            System.out.println(e.getMessage());

        }
    }

    public static String toHexString(byte[] ba) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < ba.length; i++)
            str.append(String.format("%x", ba[i]));
        return str.toString();
    }

    public static String fromHexString(String hex) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
        }
        return str.toString();
    }

    private boolean isHex(String inputData) {
        return inputData.trim().matches("[0-9A-Fa-f]+");
    }

    public static void main(String args[]) {
        new AesApplication();
    }
}
