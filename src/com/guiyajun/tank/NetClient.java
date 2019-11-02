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
import java.net.BindException;
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
    /** TCP服务的端口 */
    public static int TCPServerPort;
    /** TCP客户端的UDP端口 */
    public static int UDPClientPort;
    /** TCP的IP地址 */
    public static String serverIP;
    /** 客户端实例变量用于管理元素 */
    public TankWarClient twc = null;
    /** UDP服务引用变量 */
    private DatagramSocket datagramSocket = null;
    
    /**
    *@Description: 创建一个新的实例 NetClient.
    * @param twc 客户端实例变量用于管理元素
     */
    NetClient(TankWarClient twc) {
        this.twc = twc;
        // 获取到服务端的端口
        NetClient.TCPServerPort = NetServer.TCPServerPort;
    }
    
    public void connect() {
        Socket socket = null;
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            socket = new Socket(serverIP, TCPServerPort);
            dos = new DataOutputStream(socket.getOutputStream());
            // 将客户端自己的UDP端口号通过TCP发送给服务端
            dos.writeInt(UDPClientPort);
System.out.println("Send my UDPPort to server:" + UDPClientPort);            
            dis = new DataInputStream(socket.getInputStream());
            // 接收服务端给坦克分配的ID号
            int id = dis.readInt();
            
            // 通过ID号排列坦克加入后的出生地点
            if (id <= 110) {
                twc.myTank = new MyTank(700, 540 - 50 * (id - 100), true, twc);
            } else {
                twc.myTank = new MyTank(70, 540 - 50 * (id - 100), true, twc);
            }
            
            // 将服务端分配的ID号赋值给自己的坦克
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
        
        // 客户端创建UDP服务，用于接收消息
        try {
            datagramSocket = new DatagramSocket(UDPClientPort);
        } catch (BindException e) {
            System.out.println("The udpPort:" + UDPClientPort +"is already be used!");
        } catch (SocketException e) {
            e.printStackTrace();
        }
        
        // 启动UDP接收消息线程，UDPThread为内部类
        ThreadPoolService.getInstance().execute(new UDPThread());
    }
    
    /**
    * @Title: send
    * @Description: 发送消息体
    * @param @param message    参数 
    * @return void    返回类型
    * @throws
     */
    public void send(Message message) {
        message.send(datagramSocket);
    }
    
    /**
     * @ProjectName:  [TankWar_NET] 
     * @Package:      [com.guiyajun.tank.NetClient.java]  
     * @ClassName:    [UDPThread]   
     * @Description:  [UDP接收消息并解析消息]   
     * @Author:       [桂亚君]   
     * @CreateDate:   [2019年11月2日 下午6:17:15]   
     * @UpdateUser:   [桂亚君]   
     * @UpdateDate:   [2019年11月2日 下午6:17:15]   
     * @UpdateRemark: [说明本次修改内容]  
     * @Version:      [v1.0]
     */
    private class UDPThread implements Runnable {
        // 用来装UDP接收到的数据
        byte[] buffered = new byte[1024];
        
        @Override
        public void run() {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(buffered, buffered.length);
                while (datagramSocket != null) {
                    // 接收数据
                    datagramSocket.receive(datagramPacket);
                    // 解析数据
                    parse(datagramPacket);
                } 
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        /**
        * @Title: parse
        * @Description: 根据接收到的消息体类型调用对应的消息体解析方法
        * @param @param datagramPacket    参数 
        * @return void    返回类型
        * @throws
         */
        public void parse(DatagramPacket datagramPacket) {
            ByteArrayInputStream bais = new ByteArrayInputStream(buffered);
            DataInputStream dis = new DataInputStream(bais);
            // 消息体的类型，在Message接口中定义
            int messageType = 0;
            
            // 每个数据包的前32位是消息体的类别，获取后赋值给变量messageType
            try {
                messageType = dis.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            Message message = null;
            // 根据消息体类别调用各自的解析方法
            switch (messageType) {
                case Message.TANK_NEW_MESSAGE:
                    message = new TankNewMessage(twc);
                    message.parse(dis);
                    break;
                case Message.TANK_MOVE_MESSAGE:
                    message = new TankMoveMessage(twc);
                    message.parse(dis);
                    break;
                case Message.TANK_FIRE_MESSAGE:
                    message = new TankFireMessage(twc);
                    message.parse(dis);
                    break;                    
                case Message.TANK_BARREL_MESSAGE:
                    message = new TankBarrelMessage(twc);
                    message.parse(dis);
                    break;                    
                case Message.TANK_SUPER_FIRE_MESSAGE:
                    message = new TankSuperFireMessage(twc);
                    message.parse(dis);
                    break;                    
                case Message.TANK_EXIT_MESSAGE:
                    message = new TankExitMessage(twc);
                    message.parse(dis);
                    break;                    
                case Message.TANK_EXIT_RECEIVED_MESSAGE:
                    message = new TankExitRecievedMessage(twc.myTank);
                    message.parse(dis);
                    break;                    
                default:
                    break;
            }
        }
    }
}
