package com.example.labone;


import java.io.*;

public class BufferReader {
    public static File file = new File("C:\\Users\\brend\\IdeaProjects\\output.txt");
    public static BufferedReader br;

    static {
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public BufferReader() throws FileNotFoundException {
    }

    public static String getThisData() throws IOException{
        String s;
        String tempStr = "";
        while ((s = br.readLine()) != null){
            if(s.length() != 0){
                tempStr = s;
            }
        }
        return tempStr;
    }
}