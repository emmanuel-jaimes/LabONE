package com.example.labone;


import java.io.*;

public class BufferReader {
    public File file = new File("filename.txt");

    public BufferedReader buff = new BufferedReader(new FileReader(file));

    public BufferReader() throws FileNotFoundException {
    }

    public String getMostRecentData() throws IOException {
        String st;
        String temp = "-127.0";
        int x = 0;
        while ((st = buff.readLine()) != null) {
            temp = st;
        }
        return temp;
    }
}
