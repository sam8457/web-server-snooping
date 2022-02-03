package com.isucdc.guicontroller.config;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class GUIControllerConfig {

    public static int getPort() {
        String path;
        if(System.getProperty("os.name").contains("Windows")) {
            path = System.getProperty("user.dir") + "\\config.conf";
        }
        else{
            path = "/etc/config.conf";
        }
        File f = new File(path);
        Scanner s;
        try {
            s = new Scanner(f);
            String data = "-1";
            while (s.hasNextLine()) {
                data = s.nextLine();
//                System.out.println(data);
            }
            return Integer.parseInt(data);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return -1;
    }

    public static String getIp() {
        String path;
        if(System.getProperty("os.name").contains("Windows")) {
            path = System.getProperty("user.dir") + "\\config.conf";
        }
        else{
            path = "/etc/config.conf";
        }
        File f = new File(path);
        Scanner s;
        try {
            s = new Scanner(f);
            return s.nextLine();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return "-1";
    }
    public static ArrayList<Integer> getOperators(){
        ArrayList<Integer> operators = new ArrayList<>();
        String path;
        if(System.getProperty("os.name").contains("Windows")) {
            path = System.getProperty("user.dir") + "\\operators.txt";
        }
        else{
            path = "/etc/operators.txt";
        }
        File f = new File(path);
        Scanner s;
        try {
            s = new Scanner(f);
            while (s.hasNextLine()) {
                operators.add(Integer.parseInt(s.nextLine()));
//                System.out.println(data);
            }
            return operators;
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
}
