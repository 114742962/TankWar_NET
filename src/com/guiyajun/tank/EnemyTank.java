package com.guiyajun.tank;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 * Simple to Introduction  
 * @ProjectName:  [TankWar] 
 * @Package:      [com.guiyajun.tank.EnemyTank.java]  
 * @ClassName:    [EnemyTank]   
 * @Description:  [定义敌方坦克的属性和功能]   
 * @Author:       [Guiyajun]   
 * @CreateDate:   [2019年10月21日 下午9:43:50]   
 * @UpdateUser:   [Guiyajun]   
 * @UpdateDate:   [2019年10月21日 下午9:43:50]   
 * @UpdateRemark: [说明本次修改内容]  
 * @Version:      [v1.0]
 */
public class EnemyTank extends Tank {
    /** 设置机器人坦克的炮弹的默认颜色 */
    public static Color COLOROFMISSILE = Color.BLACK;
    /** 申明一个随机数生成器，用于生成随机步数和随机发射炮弹 */
    public static Random random = new Random();
    
    /** turnDirectionFrequency控制机器人坦克下次变向前的要走的随机步数范围，值越大变向的频率越低 */
    public int turnDirectionFrequency = 20;
    /** step变量随机生成机器人坦克要走的步数，最低为3步 */
    public int step = random.nextInt(turnDirectionFrequency) + 3;
    /** fireFrequency控制机器人坦克子弹发射的倒计数最大范围，值越大，炮弹发射的越少 */
    public int fireFrequency = 50;
    /** 随机生成坦克发射炮弹的倒计数，假如fireFrequency=50，范围是0-50 */
    public int fireCountdown = random.nextInt(fireFrequency);
    
    /**
          * 创建一个新的实例 EnemyTank.
    * @param x  x坐标
    * @param y  y坐标 
    * @param friendly   是否友好，目前版本未使用该属性，为后续联网版本设计
    * @param twc    客户端实例
     */
    public EnemyTank(int x, int y, boolean friendly, TankWarClient twc) {
        super(x, y, friendly, twc);
    }
    

    @Override
    public void draw(Graphics g) {
        if (!this.getAliveOfTank()) {
            return;
        }
        
        //获取初始颜色并画出坦克
        Color c = g.getColor();
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(x, y, TANK_WIDTH, TANK_HEIGHT);
        g.setColor(Color.DARK_GRAY);
        
        /*
                    如果步数为0就获取新的移动方向，并获取炮管方向，生成新的步数，否则步数减去1
        */
        if (step == 0) {
            getDirection();
            getBarrelDirection();
            step = random.nextInt(turnDirectionFrequency) + turnDirectionFrequency;
        } else {
            step --;
        }
        
        /*
                   如果倒计数为0就开火，并生成新的倒计数，否则倒计数减1
        */
        if(fireCountdown == 0) {
            twc.missilesOfEnemyTanks.add(fire(COLOROFMISSILE, dirOfBarrel));
            fireCountdown= random.nextInt(fireFrequency) + fireFrequency;
        } else {
            fireCountdown --;
        }
        
        /*
                    移动，碰撞检测，画出坦克的炮筒
        */
        move();
        collisionDetection();
        drawBarrel(g);
        
        // 还原颜色
        g.setColor(c);
    }
    
    @Override
    void getDirection() { 
        Direction[] dirOfEnemyTank = Direction.values();
        int indexOfDirection = random.nextInt(dirOfEnemyTank.length);
        dir = dirOfEnemyTank[indexOfDirection];
    }
}
