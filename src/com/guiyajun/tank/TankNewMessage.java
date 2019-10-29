/**
* @Title: TankMessage.java
* @Package com.guiyajun.tank
* @Description: TODO(用一句话描述该文件做什么)
* @author Administrator
* @date 2019年10月27日
* @version V1.0
*/
package com.guiyajun.tank;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * @ProjectName:  [TankWar_NET] 
 * @Package:      [com.guiyajun.tank.TankMessage.java]  
 * @ClassName:    [TankMessage]   
 * @Description:  [包装坦克的消息]   
 * @Author:       [桂亚君]   
 * @CreateDate:   [2019年10月27日 下午7:18:16]   
 * @UpdateUser:   [桂亚君]   
 * @UpdateDate:   [2019年10月27日 下午7:18:16]   
 * @UpdateRemark: [说明本次修改内容]  
 * @Version:      [v1.0]
 */
public class TankNewMessage implements Message{
    private int messageType = Message.TANK_NEW_MESSAGE;  
    Tank myTank = null;
    TankWarClient twc = null;
    private int udpServerPort = Integer.parseInt(PropertiesManager.getPerproty("udpServerPort"));
    
    TankNewMessage(Tank myTank) {
        this.myTank = myTank;
    }
    
    TankNewMessage(TankWarClient twc) {
        this.twc = twc;
    }
    /**
    * @Title: send
    * @Description: TODO(这里用一句话描述这个方法的作用)
    * @param     参数 
    * @return void    返回类型
    * @throws
    */
    public void send(DatagramSocket datagramSocket) {
        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;
        try {
            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);
            dos.writeInt(messageType);
            dos.writeInt(myTank.id);
            dos.writeInt(myTank.x);
            dos.writeInt(myTank.y);
            dos.writeInt(myTank.dir.ordinal());
            dos.writeBoolean(myTank.friendly);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        byte[] buf = baos.toByteArray();
        String ServerIP = PropertiesManager.getPerproty("ServerIP");
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, 
            new InetSocketAddress(ServerIP, udpServerPort));
        try {
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * @Title: parse
    * @Description: 解析UDP服务端转发的消息包
    * @param     参数 
    * @return void    返回类型
    * @throws
    */
    public void parse(DataInputStream dis) {
        
        try {
            int id = dis.readInt();
            if (twc.myTank != null && id == twc.myTank.id) {
                return;
            }
            
            int x = dis.readInt();
            int y = dis.readInt();
            Direction dir = Direction.values()[dis.readInt()];
            boolean friendly = dis.readBoolean();
System.out.println("messageType:" + messageType + "-id:" + id + "-x:" + x + "-y:" + y + "-dir:" + dir + "-friendly:" + friendly);
            boolean exists = false;
            for (int i=0; i<twc.tanks.size(); i++) {
                Tank tank = twc.tanks.get(i);
                if (tank.id == id) {
                    exists = true;
                    break;
                }
            }
            
            if (exists == false) {
                Message message = new TankNewMessage(twc.myTank);
                twc.netClient.send(message);
                
                MyTank tank = new MyTank(x, y, friendly, twc);
                tank.id = id;
                twc.tanks.add(tank);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
