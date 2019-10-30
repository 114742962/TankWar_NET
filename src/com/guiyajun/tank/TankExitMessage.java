/**
* @Title: TankExitMessage.java
* @Package com.guiyajun.tank
* @Description: TODO(用一句话描述该文件做什么)
* @author Administrator
* @date 2019年10月29日
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
 * @Package:      [com.guiyajun.tank.TankExitMessage.java]  
 * @ClassName:    [TankExitMessage]   
 * @Description:  [一句话描述该类的功能]   
 * @Author:       [桂亚君]   
 * @CreateDate:   [2019年10月29日 下午9:15:07]   
 * @UpdateUser:   [桂亚君]   
 * @UpdateDate:   [2019年10月29日 下午9:15:07]   
 * @UpdateRemark: [说明本次修改内容]  
 * @Version:      [v1.0]
 */
public class TankExitMessage implements Message {
    private int messageType = Message.TANK_EXIT_MESSAGE;  
    Tank myTank = null;
    TankWarClient twc = null;
    private int UDPServerPort;
    private String serverIP;
    TankExitMessage(Tank myTank) {
        this.myTank = myTank;
        this.UDPServerPort = NetServer.UDPServerPort;
        this.serverIP = NetClient.serverIP;
    }
    
    TankExitMessage(TankWarClient twc) {
        this.twc = twc;
        this.UDPServerPort = NetServer.UDPServerPort;
        this.serverIP = NetClient.serverIP;
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
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, 
            new InetSocketAddress(serverIP, UDPServerPort));
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
            
            for (int i=0; i<twc.tanks.size(); i++) {
                MyTank tank = twc.tanks.get(i);
                if (tank.id == id) {
                    tank.setAliveOfTank(false);
                    twc.tanks.remove(tank);
                    Message message = new TankExitRecievedMessage(id, twc);
                    twc.netClient.send(message);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
