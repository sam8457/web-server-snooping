package com.isucdc.guicontroller.clienthelper;

import com.isucdc.guicontroller.config.GUIControllerConfig;

import java.net.Socket;

public class ClientHelper {
    public static void helloWorld() {
        try {
            Socket sock = new Socket(GUIControllerConfig.getIp(), GUIControllerConfig.getPort());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
