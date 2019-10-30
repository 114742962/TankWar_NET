package com.guiyajun.tank;

import java.awt.*;

/**
 * @ProjectName:  [TankWar] 
 * @Package:      [com.guiyajun.tank.Wall.java]  
 * @ClassName:    [Wall]   
 * @Description:  [实现钢化墙的类，子弹和坦克不能穿透]   
 * @Author:       [Guiyajun]   
 * @CreateDate:   [2019年10月22日 下午3:59:59]   
 * @UpdateUser:   [Guiyajun]   
 * @UpdateDate:   [2019年10月22日 下午3:59:59]   
 * @UpdateRemark: [说明本次修改内容]  
 * @Version:      [v1.0]
 */
public class Wall {
    
    /** 墙壁的宽度 */
    public int width;
    /** 墙壁的高度 */
    public int height;
    /** 客户端实例变量用于管理元素 */
    public TankWarClient twc = null;
    /** 墙壁的x坐标 */
    private int x;
    /** 墙壁的y坐标 */
    private int y;
    /** 墙的渐变初始颜色 */
    public Color startColor;
    /** 墙的渐变结束颜色 */
    public Color endColor;
    
    /**
    * @Description: 创建一个新的实例 Wall.
    * @param x  x坐标
    * @param y  y坐标
    * @param width 墙壁宽度 
    * @param height 墙壁高度
    * @param twc 客户端实例
     */
    Wall(int x, int y, int width, int height, TankWarClient twc) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.twc = twc;
        initColor();
    }
    
    private void initColor() {
        int red = (int)(Math.random() * 255);
        int green = (int)(Math.random() * 255);
        int blue = (int)(Math.random() * 255);
        startColor = new Color(red, green, blue);
        red = (int)(Math.random() * 255);
        green = (int)(Math.random() * 255);
        blue = (int)(Math.random() * 255);
        endColor = new Color(red, green, blue);
    }
    
    /**
    * @Title: draw
    * @Description: 墙壁的绘制方法
    * @param @param g    画笔 
    * @return void    返回类型
    * @throws
     */
    public void draw(Graphics g) {
        Color c = g.getColor();
        Graphics2D graphics2D = (Graphics2D)g;
        GradientPaint gradientPaint = new GradientPaint(10, 10, startColor, 32, 
            32, endColor, true);
        graphics2D.setPaint(gradientPaint);
        graphics2D.fillRect(x, y, width, height);
        g.setColor(c);
    }
    
    /**
    * @Title: getRectOfWall
    * @Description: 获取墙壁的方框区域对象，用于碰撞比对
    * @return Rectangle    返回类型
    * @throws
     */
    public Rectangle getRectOfWall() {
        return new Rectangle(x, y, width, height);
    }
}
