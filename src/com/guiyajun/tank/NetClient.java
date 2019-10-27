/**
* @Title: NetClient.java
* @Package com.guiyajun.tank
* @Description: TODO(用一句话描述该文件做什么)
* @author Administrator
* @date 2019年10月26日
* @version V1.0
*/
package com.guiyajun.tank;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
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
        udpPort = UDP_PORT_START ++;
    }
    
    public void connect() {
        Socket socket = null;
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            socket = new Socket(ServerIP, TCPServerPort);
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt(udpPort);
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
        TankMessage tankMessage = new TankMessage(datagramSocket, twc.myTank);
        send(tankMessage);
    }
    
    public void send(TankMessage tankMessage) {
        tankMessage.send();
    }

}
