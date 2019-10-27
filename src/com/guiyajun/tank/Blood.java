package com.guiyajun.tank;

import java.awt.*;

/**
 * Simple to Introduction  
 * @ProjectName:  [TankWar] 
 * @Package:      [com.guiyajun.tank.Blood.java]  
 * @ClassName:    [Blood]   
 * @Description:  [加血包的属性和功能]   
 * @Author:       [Guiyajun]   
 * @CreateDate:   [2019年10月21日 下午4:13:44]   
 * @UpdateUser:   [Guiyajun]   
 * @UpdateDate:   [2019年10月21日 下午4:13:44]   
 * @UpdateRemark: [去掉了两种加血包的设计，只保留一种，并去掉相应的变量]  
 * @Version:      [v1.0]
 */
public class Blood {
    /** 加血包宽度 */
    public static int EAT_BLOOD_WIDTH = 15;
    /** 加血包高度 */
    public static int EAT_BLOOD_HEIGHT = 15;
    /** 客户端实例变量用于管理元素 */
    public TankWarClient twc = null;
    /** 定义血块的x坐标变量  */
    private int x;
    /** 定义血块的x坐标变量  */
    private int y;
    /** 血块的存活状态 */
    private boolean alive = true;
    
    /**
          * 创建一个新的实例 Blood.
    * @param x  x坐标
    * @param y  y坐标
    * @param twc    客户端实例
     */
    public Blood(int x, int y, TankWarClient twc) {
        this.x = x;
        this.y = y;
        this.twc = twc;
    }
    
    /**
     * 
    * @Title: draw
    * @Description: (加血包的绘制方法)
    * @param g    参数，可以理解为绘画用的笔 
    * @return void    返回类型
    * @throws
     */
    public void draw(Graphics g) {
        if (!this.getAlive()) {
            return;
        }
        
        Color c = g.getColor();
        g.setColor(Color.RED);
        g.fillRect(x, y, EAT_BLOOD_WIDTH, EAT_BLOOD_HEIGHT);
        g.setColor(c);
    }
    
    /**
    * @Title: getAlive
    * @Description: (获取当前血块实例的存活状态)
    * @param 
    * @return boolean    返回类型
    * @throws
     */
    public boolean getAlive() {
        return alive;
    }
    
    /**
    * @Title: setAlive
    * @Description: (设置当前血块实例的存活状态)
    * @param @param alive    存活状态 
    * @return void    返回类型
    * @throws
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    
    /**
    * @Title: getRectOfBlood
    * @Description: (获取加血包的矩形区域对象，用于碰撞检测)
    * @param  
    * @return Rectangle    返回类型
    * @throws
     */
    public Rectangle getRectOfBlood() {
        return new Rectangle(x, y, EAT_BLOOD_WIDTH, EAT_BLOOD_HEIGHT);
    }
}
