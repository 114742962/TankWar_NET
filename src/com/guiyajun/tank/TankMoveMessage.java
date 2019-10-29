/**
* @Title: TankMoveMessage.java
* @Package com.guiyajun.tank
* @Description: TODO(用一句话描述该文件做什么)
* @author Administrator
* @date 2019年10月28日
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
 * @Package:      [com.guiyajun.tank.TankMoveMessage.java]  
 * @ClassName:    [TankMoveMessage]   
 * @Description:  [坦克移动的消息]   
 * @Author:       [桂亚君]   
 * @CreateDate:   [2019年10月28日 下午10:10:04]   
 * @UpdateUser:   [桂亚君]   
 * @UpdateDate:   [2019年10月28日 下午10:10:04]   
 * @UpdateRemark: [说明本次修改内容]  
 * @Version:      [v1.0]
 */
public class TankMoveMessage implements Message {
    private int messageType = Message.TANK_MOVE_MESSAGE;
    private int UDPServerPort = Integer.parseInt(PropertiesManager.getPerproty("UDPServerPort"));
    public Tank myTank = null;
    public TankWarClient twc = null;
    
    TankMoveMessage(Tank myTank) {
        this.myTank = myTank;
    }
    
    TankMoveMessage(TankWarClient twc) {
        this.twc = twc;
    }
    
    @Override
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
            new InetSocketAddress(ServerIP, UDPServerPort));
        try {
            if (datagramSocket != null) {
                datagramSocket.send(datagramPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parse(DataInputStream dis) {
        try {
            int id = dis.readInt();
            if (myTank != null && id == myTank.id) {
                return;
            }
            int x = dis.readInt();
            int y = dis.readInt();
            Direction dir = Direction.values()[dis.readInt()];
            
            for (int i=0; i<twc.tanks.size(); i++) {
                MyTank tank = twc.tanks.get(i);
                if (tank.id == id) {
                    tank.dir = dir;
                    tank.x = x;
                    tank.y = y;
                    break;
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
