package com.speedyg.btw.license;


import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.LogLevel;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class License {

    private boolean license = false;

    public License(final String ip, final String pluginName) throws IOException {
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        try {
            socket = new Socket("178.20.229.149", 1453);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BasicTeamWars.getInstance().log(LogLevel.INFO, "License server connection success!");
        } catch (Exception e) {
            BasicTeamWars.getInstance().log(LogLevel.CONNECTION, "License server connection error!");
            BasicTeamWars.getInstance().log(LogLevel.CONNECTION, "Please contact with Discord; Yusuf#7761");
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §cLicense server connection error!");
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §cPlease contact with Discord; Yusuf#7761");
            return;
        }


        String send_packet = ip + "_" + pluginName + "_";
        String receive_packet;
        if (ip != null && pluginName != null) {
            out.println(send_packet);
            receive_packet = in.readLine();

            if (receive_packet != null) {
                try {
                    this.license = Boolean.parseBoolean(receive_packet);
                } catch (Exception e) {
                    this.license = false;
                }
            } else {
                this.license = false;
            }
        }
        out.close();
        in.close();
        socket.close();

    }


    public boolean getControl() {
        return this.license;
    }


}
