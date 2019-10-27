/**
* @Title: Direction.java
* @Package com.guiyajun.tank
* @Description: TODO(用一句话描述该文件做什么)
* @author Administrator
* @date 2019年10月26日
* @version V1.0
*/
package com.guiyajun.tank;

 /**
 * @ProjectName:  [TankWar1.0] 
 * @Package:      [com.guiyajun.tank.Direction.java]  
 * @ClassName:    [Direction]   
 * @Description:  [定义了游戏中的方向]   
 * @Author:       [桂亚君]   
 * @CreateDate:   [2019年10月26日 上午10:19:00]   
 * @UpdateUser:   [桂亚君]   
 * @UpdateDate:   [2019年10月26日 上午10:19:00]   
 * @UpdateRemark: [说明本次修改内容]  
 * @Version:      [v1.0]
 */
public enum Direction {
    /** 上 */
    UP,
    /** 下 */
    DOWN,
    /** 左 */
    LEFT, 
    /** 右 */
    RIGHT,
    /** 左上 */
    UP_LEFT,
    /** 右上 */
    UP_RIGHT,
    /** 左下 */
    DOWN_LEFT,
    /** 右下 */
    DOWN_RIGHT,
    /** 停止 */
    STOP
}
