/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javachatapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nick Williams
 */
public class ChatServer {

    static ArrayList<String> userNames = new ArrayList<String>();
    static ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>();
    
    public static void main(String[] args) {
        try {
            System.out.println("Waiting for clients...");
            ServerSocket ss = new ServerSocket(9806);
            
            while(true){                
                Socket soc = ss.accept();
                System.out.println("Connection established");
                
                ConversationHandler handler = new ConversationHandler(soc);
                handler.start();
            }
            
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }
    
}

class ConversationHandler extends Thread{    
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    String name;
    PrintWriter logs;
    static FileWriter fw;
    static BufferedWriter bw;
    
    public ConversationHandler(Socket socket){
        try{
            this.socket = socket;
            fw = new FileWriter("C:\\xxxxx\\xxxxx\\JavaChatApp\\ChatServer-Logs.txt",true); //removed the file path for git, replace with your own file path if  you are testing this.
            bw = new BufferedWriter(fw);
            logs = new PrintWriter(bw,true);
            
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    public void run(){
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),true);
            
            int count = 0;
            while(true){
                if(count > 0){
                    out.println("NAMEALREADYEXISTS");
                }else{
                    out.println("NAMEREQUIRED"); //will default to this since count is initialized at 0
                }
                
                name = in.readLine();
                
                if(name == null){
                    return; //returns since we don't want a null name
                }
                
                if(!ChatServer.userNames.contains(name)){ //checks the userName arraylist to verify if name is unique
                    ChatServer.userNames.add(name);
                    break; //breaks if username is unique
                }
                count++; //increments count variable so that "name already exists" messaging appears if name isn't unique
            }
            
            out.println("NAMEACCEPTED" + name);
            ChatServer.printWriters.add(out);
            
            while(true){
                String message = in.readLine();
                
                if(message == null){
                    return;
                }
                
                logs.println(name + ": " + message);
                
                for(PrintWriter writer:ChatServer.printWriters){
                    writer.println(name + ": " + message);
                }
                
            }
            
        }catch(Exception e){
            System.out.println(e);
        }
    }
}