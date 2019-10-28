/**
* @Title: Message.java
* @Package com.guiyajun.tank
* @Description: TODO(用一句话描述该文件做什么)
* @author Administrator
* @date 2019年10月28日
* @version V1.0
*/
package com.guiyajun.tank;

import java.io.DataInputStream;
import java.net.DatagramSocket;

/**
 * @ProjectName:  [TankWar_NET] 
 * @Package:      [com.guiyajun.tank.Message.java]  
 * @ClassName:    [Message]   
 * @Description:  [一句话描述该类的功能]   
 * @Author:       [桂亚君]   
 * @CreateDate:   [2019年10月28日 下午9:52:15]   
 * @UpdateUser:   [桂亚君]   
 * @UpdateDate:   [2019年10月28日 下午9:52:15]   
 * @UpdateRemark: [说明本次修改内容]  
 * @Version:      [v1.0]
 */
public interface Message {
    public static final int TANK_NEW_MESSAGE = 1;
    public static final int TANK_MOVE_MESSAGE = 2;
    
    public void send(DatagramSocket datagramSocket);
    
    public void parse(DataInputStream dis);
}
