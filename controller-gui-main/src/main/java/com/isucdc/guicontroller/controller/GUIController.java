package com.isucdc.guicontroller.controller;

import com.isucdc.guicontroller.config.GUIControllerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Controller
public class GUIController {
    static Logger log = LogManager.getLogger(GUIController.class.getName());
    ArrayList<KeyValuePair> devices = new ArrayList<>();
    @RequestMapping("/")
    public String startUp(Model model){
        log.info(System.getProperty("os.name"));
        log.info(GUIControllerConfig.getPort());
        log.info(GUIControllerConfig.getIp());
        return "index.html";
    }

    @RequestMapping(value= "/logout", method = RequestMethod.GET)
    public String logout(HttpServletResponse resp) throws IOException {
        Cookie cookie = new Cookie("operatorID", null);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        resp.addCookie(cookie);
        resp.sendRedirect("/login");
        return null;
    }

    @RequestMapping(value= "/login", method = RequestMethod.GET)
    public String login() {
        return "login.html";
    }

    @RequestMapping(value= "/login", method = RequestMethod.POST)
    @ResponseBody
    public String loginPost(HttpServletResponse resp, @RequestParam String operatorID, @CookieValue(value = "operatorID", defaultValue = "") String id) throws IOException {
//        System.out.println(System.getProperty("os.name"));
//        System.out.println(GUIControllerConfig.getPort());
//        System.out.println(GUIControllerConfig.getIp());

        try {
//            if(!id.isEmpty()){
//                resp.sendRedirect("/status");
//                return null;
//            }
            if (GUIControllerConfig.getOperators().contains(Integer.parseInt(operatorID))) {
                log.info(GUIControllerConfig.getOperators());
                log.info("Valid operator ID: " + operatorID);
                resp.addCookie(new Cookie("operatorID", operatorID));
                resp.sendRedirect("/status");
                return null;
            }
            else
            {
                log.error("No such operator ID" + operatorID + " found within: " + GUIControllerConfig.getOperators().toString());
            }
        }
        catch (Exception e) {
            log.info("Either no operators were added or file not found");
            e.printStackTrace();
        }
        resp.sendRedirect("/login");
        return null;    }

    @RequestMapping("/actions")
    public String actionsPage(HttpServletResponse resp,Model model, @CookieValue(value = "operatorID", defaultValue = "") String id) throws IOException {
        if(id.isEmpty()){
            resp.sendRedirect("/login");
            return null;
        }
        listDevice();
        model.addAttribute("deviceNames", devices);
        return "device_actions.html";
    }

    @RequestMapping(value = "/newFuel", method = RequestMethod.POST)
    @ResponseBody
    public String newFuel(HttpServletResponse resp,@RequestParam String deviceName, @CookieValue(value = "operatorID", defaultValue = "") String id) throws IOException {
        if(id.isEmpty()){
            resp.sendRedirect("/login");
            return null;
        }
        refuel(deviceName);
        log.info("Refuel Success");
        return "SUCCESS";
    }

    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public String del(HttpServletResponse resp, @RequestParam String deviceName, @CookieValue(value = "operatorID", defaultValue = "") String id) throws IOException {
        if(id.isEmpty()){
            resp.sendRedirect("/login");
            return null;
        }
        log.info("The Deletion ID is: " +id);

        deleteDevice(deviceName);
        log.info("Device Deletion Success");
        return "SUCCESS";
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public String statusPage(HttpServletResponse resp, Model model, @CookieValue(value = "operatorID", defaultValue = "") String id) throws IOException {
        log.info("The Status ID is: " +id);
        if(id.isEmpty()){
            resp.sendRedirect("/login");
            return null;
        }
        int max = maxDraw();
        int cur = currentDraw();
        String colour;
        int percent = (int)((cur*1.0)/max*100);
        model.addAttribute("percent", percent);
        model.addAttribute("max", max);
        model.addAttribute("current",cur);
        log.info("Max Draw:" + max);
        log.info("Current Draw:" + cur);
        if((cur*1.0)/max < .75){
            colour = "green";
        }
        else if((cur*1.0)/max < .95){
            colour = "yellow";
        }
        else colour = "red";
        model.addAttribute("colour", colour);
        log.info("Status Page load success with code colour:" +colour);
        return "device_status.html";
    }

    @RequestMapping(value ="/addDevice", method = RequestMethod.POST)
    @ResponseBody
    public String addDevice(HttpServletResponse resp, @RequestParam String name, @RequestParam String ip, @CookieValue(value = "operatorID", defaultValue = "") String id) throws IOException {
        if(id.isEmpty()){
            resp.sendRedirect("/login");
            return null;
        }
        log.info("The ID is: " +id);
        System.out.println(name + " " + ip);
        addNewDevice(name, ip);
        log.info("New device:" + name + " at: " + ip);
        return "SUCCESS";
    }

    public void hello(){

        try {
            Socket sc = new Socket(GUIControllerConfig.getIp(), GUIControllerConfig.getPort());
            InputStream in = sc.getInputStream();
            OutputStream out = sc.getOutputStream();
            byte[] dataIn = new byte[1024];

//            System.out.println(in.read(dataIn));
            log.info("Number of Bytes read in: " + in.read(dataIn));
            log.info(bytesToString(dataIn));
            System.out.println(bytesToString(dataIn));
            out.write(toByteArr("HELLO"));
//            System.out.println(in.read(dataIn));
            log.info("Number of Bytes read in: " + in.read(dataIn));
            log.info(bytesToString(dataIn));
            System.out.println(bytesToString(dataIn));
            out.write(toByteArr("END"));
            sc.close();
            System.out.println("Connection Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refuel(String devName){
        try{
            Socket sc = new Socket(GUIControllerConfig.getIp(), GUIControllerConfig.getPort());
            InputStream in = sc.getInputStream();
            OutputStream out = sc.getOutputStream();
            byte[] dataIn = new byte[1024];
            log.info("Number of Bytes read in: " + in.read(dataIn));
            log.info(bytesToString(dataIn));
            out.write(toByteArr("REFUEL "+ devName));
//            System.out.println(in.read(dataIn));
//            System.out.println(bytesToString(dataIn));
            log.info("Number of Bytes read in: " + in.read(dataIn));
            log.info(bytesToString(dataIn));
            out.write(toByteArr("END"));
            sc.close();
            System.out.println("Connection Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listDevice(){
        try{
            devices.clear();
            Socket sc = new Socket(GUIControllerConfig.getIp(), GUIControllerConfig.getPort());
            InputStream in = sc.getInputStream();
            OutputStream out = sc.getOutputStream();
            byte[] dataIn = new byte[1024];
            log.info("Number of Bytes read in: " + in.read(dataIn));
            log.info(bytesToString(dataIn));
            out.write(toByteArr("DEVICE LIST"));
            log.info("Number of Bytes read in: " + in.read(dataIn));
            log.info(bytesToString(dataIn));
            String allDevices = bytesToString((dataIn));
            System.out.println(allDevices);
//            in.read(dataIn);
            String[] dev = allDevices.split(",");
            for(String s : dev){
//                System.out.println(s);
                devices.add(new KeyValuePair(s.split(" ")[0], s.split(" ")[1]));

            }
            log.info(devices);
            out.write(toByteArr("END"));
            sc.close();
            System.out.println("Connection Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNewDevice(String name, String ip){
        try{
            Socket sc = new Socket(GUIControllerConfig.getIp(), GUIControllerConfig.getPort());
            InputStream in = sc.getInputStream();
            OutputStream out = sc.getOutputStream();
            byte[] dataIn = new byte[1024];
            log.info("Number of Bytes read in: " + in.read(dataIn));
            log.info(bytesToString(dataIn));
            out.write(toByteArr("DEVICE ADD " + name + " " + ip));
            log.info("Number of Bytes read in: " + in.read(dataIn));
            log.info(bytesToString(dataIn));
            out.write(toByteArr("END"));
            sc.close();
            System.out.println("Connection Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteDevice(String name){
        try{
            Socket sc = new Socket(GUIControllerConfig.getIp(), GUIControllerConfig.getPort());
            InputStream in = sc.getInputStream();
            OutputStream out = sc.getOutputStream();
            byte[] dataIn = new byte[1024];
            log.info("Number of Bytes read in: " + in.read(dataIn));
            log.info(bytesToString(dataIn));
            out.write(toByteArr("DEVICE DELETE " + name));
//            System.out.println(in.read(dataIn));
//            System.out.println(bytesToString(dataIn));
            log.info("Number of Bytes read in: " + in.read(dataIn));
            log.info(bytesToString(dataIn));
            out.write(toByteArr("END"));
            sc.close();
            System.out.println("Connection Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int maxDraw(){
        try{
            Socket sc = new Socket(GUIControllerConfig.getIp(), GUIControllerConfig.getPort());
            InputStream in = sc.getInputStream();
            OutputStream out = sc.getOutputStream();
            byte[] dataIn = new byte[1024];
            log.info("Number of Bytes read in: " + in.read(dataIn));
            log.info(bytesToString(dataIn));
            out.write(toByteArr("DRAW GET MAXIMUM"));
            log.info("Number of Bytes read in: " + in.read(dataIn));
            log.info(bytesToString(dataIn));
            out.write(toByteArr("END"));
            sc.close();
            System.out.println("Connection Closed");
            return Integer.parseInt(bytesToString(dataIn));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int currentDraw(){
        try{
            Socket sc = new Socket(GUIControllerConfig.getIp(), GUIControllerConfig.getPort());
            InputStream in = sc.getInputStream();
            OutputStream out = sc.getOutputStream();
            byte[] dataIn = new byte[1024];
            log.info("Number of Bytes read in: " + in.read(dataIn));
            log.info(bytesToString(dataIn));
            out.write(toByteArr("DRAW GET CURRENT"));
//            System.out.println(in.read(dataIn));
//            System.out.println(bytesToString(dataIn));
            log.info("Number of Bytes read in: " + in.read(dataIn));
            log.info(bytesToString(dataIn));
            out.write(toByteArr("END"));
            sc.close();
            System.out.println("Connection Closed");
            return Integer.parseInt(bytesToString(dataIn));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private byte[] toByteArr(String strToBytes){
        Charset charset = StandardCharsets.US_ASCII;

        return charset.encode(strToBytes + "\n").array();
    }

    private String bytesToString(byte[] in){
        return (new String(in, StandardCharsets.UTF_8)).split("\n")[0];
    }


}
