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
public class TankMessage {
    Tank myTank = null;
    private DatagramSocket datagramSocket = null;
    private String ServerIP;
    private static int udpServerPort = Integer.parseInt(PropertiesManager.getPerproty("udpServerPort"));
    
    TankMessage(DatagramSocket datagramSocket, Tank myTank) {
        this.datagramSocket = datagramSocket;
        this.myTank = myTank;
    }

    /**
    * @Title: send
    * @Description: TODO(这里用一句话描述这个方法的作用)
    * @param     参数 
    * @return void    返回类型
    * @throws
    */
    public void send() {
        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;
        try {
            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);
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
        ServerIP = PropertiesManager.getPerproty("ServerIP");
System.out.println(ServerIP);
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, 
            new InetSocketAddress(ServerIP, udpServerPort));
        try {
            if (datagramSocket != null) {
                datagramSocket.send(datagramPacket);
System.out.println("发送了一包数据");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
