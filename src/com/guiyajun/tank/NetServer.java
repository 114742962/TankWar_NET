/**
* @Title: NetServer.java
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
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName:  [TankWar_NET] 
 * @Package:      [com.guiyajun.tank.NetServer.java]  
 * @ClassName:    [NetServer]   
 * @Description:  [坦克大战服务端]   
 * @Author:       [桂亚君]   
 * @CreateDate:   [2019年10月26日 下午10:12:00]   
 * @UpdateUser:   [桂亚君]   
 * @UpdateDate:   [2019年10月26日 下午10:12:00]   
 * @UpdateRemark: [说明本次修改内容]  
 * @Version:      [v1.0]
 */
public class NetServer {
    /** 坦克的编号，每加入一个坦克增加1 */
    private static int id = 100;
    /** UDP端口号 */
    private static int UDPServerPort = Integer.parseInt(PropertiesManager.getPerproty("UDPServerPort"));
    /** 从配置文件中读取服务端TCP端口号 */
    private static int TCPServerPort = Integer.parseInt(PropertiesManager.getPerproty("TCPServerPort"));
    private List<Client> clients = new ArrayList<>();
    /** 创建一个UDP服务 */
    DatagramSocket datagramSocket = null;
    /** 主坦克*/
    
    
//    public static void main(String[] args) {
//        new NetServer().start();
//    }
    
    public void start () {
        ServerSocket ss = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            ss = new ServerSocket(TCPServerPort);
            while (true) {
                Socket socket = ss.accept();
System.out.println("A client is connect!" + socket.getInetAddress() + ":" + socket.getPort()
                    + ":" + id);
                dis = new DataInputStream(socket.getInputStream());
                int udpPort = dis.readInt();
System.out.println("The udpPort of the client:" + udpPort);
                String clientIPAdress = socket.getInetAddress().getHostAddress();
                clients.add(new Client(clientIPAdress, udpPort));
                dos  = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(id++);
                dos.flush();
                
                datagramSocket = new DatagramSocket(UDPServerPort);
                
                // 启动UDP服务线程池
                ThreadPoolService.getInstance().execute(new UDPServerThread()); 
                
            }
        } catch (BindException e) {
            System.out.println("The server is already started on port:" + TCPServerPort);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * @ProjectName:  [TankWar_NET] 
     * @Package:      [com.guiyajun.tank.NetServer.java]  
     * @ClassName:    [Client]   
     * @Description:  [客户端在服务端内部的包装类，记录每个连接到服务端的客户端信息，用于UDP回传]   
     * @Author:       [桂亚君]   
     * @CreateDate:   [2019年10月27日 上午10:40:31]   
     * @UpdateUser:   [桂亚君]   
     * @UpdateDate:   [2019年10月27日 上午10:40:31]   
     * @UpdateRemark: [说明本次修改内容]  
     * @Version:      [v1.0]
     */
    private class Client {
        public String ip;
        public int udpPort;
        
        Client(String ip, int udpPort) {
            this.ip = ip;
            this.udpPort = udpPort;
        }
    }
    
    /** 
     * @ProjectName:  [TankWar_NET] 
     * @Package:      [com.guiyajun.tank.NetServer.java]  
     * @ClassName:    [UDPServerThread]   
     * @Description:  [UDP服务线程]   
     * @Author:       [桂亚君]   
     * @CreateDate:   [2019年10月27日 下午9:19:55]   
     * @UpdateUser:   [桂亚君]   
     * @UpdateDate:   [2019年10月27日 下午9:19:55]   
     * @UpdateRemark: [说明本次修改内容]  
     * @Version:      [v1.0]
     */
    private class UDPServerThread implements Runnable {
        byte[] buffered = new byte[1024];
        DatagramPacket datagramPacket = null;
        @Override
        public void run() {
            try {
                datagramPacket = new DatagramPacket(buffered, buffered.length);
                while (datagramSocket != null) {
                    datagramSocket.receive(datagramPacket);
                    for (int i=0; i<clients.size(); i++) {
                        Client c = clients.get(i);
                        datagramPacket.setSocketAddress(new InetSocketAddress(c.ip, c.udpPort));
                        datagramSocket.send(datagramPacket);
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (datagramSocket != null) {
                    datagramSocket.close();
                    datagramSocket = null;
                }
            }
        }
    }
}
