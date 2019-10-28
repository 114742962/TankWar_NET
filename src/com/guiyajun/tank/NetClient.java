/**
* @Title: NetClient.java
* @Package com.guiyajun.tank
* @Description: TODO(用一句话描述该文件做什么)
* @author Administrator
* @date 2019年10月26日
* @version V1.0
*/
package com.guiyajun.tank;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @ProjectName:  [TankWar_NET] 
 * @Package:      [com.guiyajun.tank.NetClient.java]  
 * @ClassName:    [NetClient]   
 * @Description:  [网络连接客户端类]   
 * @Author:       [桂亚君]   
 * @CreateDate:   [2019年10月26日 下午10:03:24]   
 * @UpdateUser:   [桂亚君]   
 * @UpdateDate:   [2019年10月26日 下午10:03:24]   
 * @UpdateRemark: [说明本次修改内容]  
 * @Version:      [v1.0]
 */
public class NetClient {
    private static int TCPServerPort = Integer.parseInt(PropertiesManager.getPerproty("TCPServerPort"));
    private static String ServerIP = PropertiesManager.getPerproty("TCPServerIP");
    private static int UDP_PORT_START = 35555;
    private int udpPort;
    public TankWarClient twc = null;
    private DatagramSocket datagramSocket = null;
    
    NetClient(TankWarClient twc) {
        this.twc = twc;
    }
    
    public void connect() {
        Socket socket = null;
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            socket = new Socket(ServerIP, TCPServerPort);
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt(UDP_PORT_START);
            dis = new DataInputStream(socket.getInputStream());
            int id = dis.readInt();
            twc.myTank.id = id;
System.out.println("Connect to server and get a id:" + id);            
        } catch (ConnectException e) {
            System.out.println("The TCPServer is not started!");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                    socket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        try {
            datagramSocket = new DatagramSocket(UDP_PORT_START);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        
        TankNewMessage tankNewMessage = new TankNewMessage(twc.myTank);
        send(tankNewMessage);
        
        ThreadPoolService.getInstance().execute(new UDPThread());
        
    }
    
    public void send(Message tankMessage) {
        tankMessage.send(datagramSocket);
    }
    
    private class UDPThread implements Runnable {
        byte[] buffered = new byte[1024];
        @Override
        public void run() {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(buffered, buffered.length);
                while (datagramSocket != null) {
System.out.println("Start to recieve TankMessage from server!");                    
                    datagramSocket.receive(datagramPacket);
System.out.println("Get TankMessage from server!");                    
                    parse(datagramPacket);
                } 
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        public void parse(DatagramPacket datagramPacket) {
            ByteArrayInputStream bais = new ByteArrayInputStream(buffered);
            DataInputStream dis = new DataInputStream(bais);
            int messageType = 0;
            
            try {
                messageType = dis.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            Message message = null;
            switch (messageType) {
                case Message.TANK_NEW_MESSAGE:
                    message = new TankNewMessage(twc);
                    message.parse(dis);
                    break;
                case Message.TANK_MOVE_MESSAGE:
                    message = new TankMoveMessage(twc);
                    message.parse(dis);
                    break;
                default:
                    break;
            }
        }
    }
}
