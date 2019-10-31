package com.guiyajun.tank;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

/**
 * @ProjectName:  [TankWar] 
 * @Package:      [com.guiyajun.tank.Missile.java]  
 * @ClassName:    [Missile]   
 * @Description:  [坦克炮弹的属性和方法]   
 * @Author:       [Guiyajun]   
 * @CreateDate:   [2019年10月22日 下午4:11:41]   
 * @UpdateUser:   [Guiyajun]   
 * @UpdateDate:   [2019年10月22日 下午4:11:41]   
 * @UpdateRemark: [说明本次修改内容]  
 * @Version:      [v1.0]
 */
public class Missile {
    /** 炮弹宽度 */
    public static final int MISSILE_WIDTH = 8;
    /** 炮弹高度 */
    public static final int MISSILE_HEIGHT = 8;
    /** 炮弹移动速度 */
    public static final int MISSILE_MOVESTEP = 8;
    /** 客户端实例变量用于管理元素  */
    public TankWarClient twc = null;
    /** 炮弹的X坐标 */
    private int x = 0;
    /** 炮弹的y坐标 */
    private int y = 0;
    /** 炮弹的默认颜色*/
    private Color colorOfMissile = Color.BLACK;
    /** 炮弹的存活状态*/
    private boolean aliveOfMissile = true;      
    /** 炮弹的方向，类型为Tank类中定义的枚举类型Direction*/
    Direction dirOfMissile = null;
    
    /**
    * @Description: 创建一个新的实例 Missile.
    * @param x  x坐标
    * @param y  y坐标
    * @param dirOfBarrel 炮筒方向
    * @param twc    客户端实例
     */
    Missile(int x, int y, Direction dirOfBarrel, TankWarClient twc) {
        this.x = x;
        this.y = y;
        this.dirOfMissile = dirOfBarrel;
        this.twc = twc;
    }
    /**
     * 
    * @Description: 创建一个新的实例 Missile.
    * @param x  x坐标
    * @param y  y坐标
    * @param dirOfBarrel 炮筒方向
    * @param twc 客户端实例
    * @param color 坦克颜色
     */
    Missile(int x, int y, Direction dirOfBarrel, TankWarClient twc, Color color) {
        this(x, y, dirOfBarrel, twc);
        this.colorOfMissile = color;
    }
    
    /**
    * @Title: draw
    * @Description: 炮弹的绘制方法
    * @param @param g    绘画用的参数，可以理解为画笔 
    * @return void    返回类型
    * @throws
     */
    public void draw(Graphics g) {
        if (!this.getAliveOfMissile()) {
            return;
        }
        
        //画出炮弹
        Color c = g.getColor();
        g.setColor(colorOfMissile);
        g.fillOval(x, y, MISSILE_WIDTH, MISSILE_HEIGHT);        
        g.setColor(c);
        
        //移动炮弹
        move();
    }
    
    /**
    * @Title: move
    * @Description: 子弹的移动方法
    * @param     
    * @return void    返回类型
    * @throws
     */
    public void move() {
        switch (dirOfMissile) {
            case UP:
                y -= MISSILE_MOVESTEP;
                break;
            case DOWN:
                y += MISSILE_MOVESTEP;
                break;
            case LEFT:
                x -= MISSILE_MOVESTEP;
                break;
            case RIGHT:
                x += MISSILE_MOVESTEP;
                break;
            case UP_LEFT:
                x -= Math.round(MISSILE_MOVESTEP * Math.sqrt(2)/2);
                y -= Math.round(MISSILE_MOVESTEP * Math.sqrt(2)/2);
                break;
            case UP_RIGHT:
                x += Math.round(MISSILE_MOVESTEP * Math.sqrt(2)/2);
                y -= Math.round(MISSILE_MOVESTEP * Math.sqrt(2)/2);
                break;
            case DOWN_LEFT:
                x -= Math.round(MISSILE_MOVESTEP * Math.sqrt(2)/2);
                y += Math.round(MISSILE_MOVESTEP * Math.sqrt(2)/2);
                break;
            case DOWN_RIGHT:
                x += Math.round(MISSILE_MOVESTEP * Math.sqrt(2)/2);
                y += Math.round(MISSILE_MOVESTEP * Math.sqrt(2)/2);
                break;
            default:
                break;
        }
        
        // 炮弹每移动一步需要做一次碰撞检测
        collisionDetection();
    }
    
    /**
    * @Title: hitTank
    * @Description: 判断炮弹是否击中坦克，根据判断修改炮弹和坦克的生存状态
    * @param @param tank 传入一个tank对象
    * @param @return
    * @return boolean    返回类型
    * @throws
     */
    boolean hitTank(Tank tank) {
        boolean hit = false;
        if (this.aliveOfMissile && this.getRectOfMissle().intersects(tank.getRectOfTank()) 
            && tank.getAliveOfTank()) {
            hit = true;
            this.setAliveOfMissile(false);
            
            // 主坦克被击中一次掉一点血
            if(tank instanceof MyTank) {
                tank.reduceBloodOfTank(1);
            } else {
                tank.reduceBloodOfTank(4);
            }
            
            // 如果坦克没血了将坦克设置为被摧毁状态
            if (tank.getBloodOfTank() <= 0) {
                tank.setAliveOfTank(false);
            }
            
            // 坦克被击中时产生爆炸效果并将爆炸效果加入到客户端实例的爆炸集合中
            twc.explodes.add(new Explode(tank.x, tank.y, twc));
        }
        
        return hit;
    }
    
    /**
    * @Title: hitEnemyTanks
    * @Description: 判断是否击中任意机器人坦克
    * @param @param enemyTanks    敌方坦克的集合
    * @return void    返回类型
    * @throws
     */
    public void hitEnemyTanks(List<EnemyTank> enemyTanks) {
        for (int i=0; i<enemyTanks.size(); i++) {
            Tank et = enemyTanks.get(i);
            hitTank(et);
        }
    }
    
    /**
    * @Title: hitPlayTanks
    * @Description: 判断是否击中其他玩家任意坦克
    * @param @param tanks    参数 
    * @return void    返回类型
    * @throws
     */
    public void hitPlayerTanks(List<MyTank> tanks) {
        for (int i=0; i<tanks.size(); i++) {
            Tank et = tanks.get(i);
            hitTank(et);
        }
    }
        
    /**
    * @Title: getAliveOfMissile
    * @Description: 获取炮弹的生存状态
    * @param @return   
    * @return boolean    返回类型
    * @throws
     */
    public boolean getAliveOfMissile() {
        return aliveOfMissile;
    }
    
    /**
    * @Title: setAliveOfMissile
    * @Description: 设置炮弹的生存状态
    * @param @param live    布尔型，true代表正常，false代表摧毁 
    * @return void    返回类型
    * @throws
     */
    public void setAliveOfMissile(boolean live) {
        this.aliveOfMissile = live;
    }
    
    /**
    * @Title: collisionDetection
    * @Description: 子弹碰撞检测
    * @param     参数 
    * @return void    返回类型
    * @throws
     */
    public void collisionDetection() {
        if (twc != null) {
            // 子弹与围墙碰撞检测
            if (x < 0 || y < TankWarClient.SCORE_AREA || x > TankWarClient.GAME_WIDTH 
                || y > TankWarClient.GAME_HEIGHT) {
            
                aliveOfMissile = false;
                twc.missilesOfMyTank.remove(this);
            }

            // 子弹与内墙碰撞检测
            if (this.getRectOfMissle().intersects(twc.wallLeft.getRectOfWall()) 
                || this.getRectOfMissle().intersects(twc.wallRight.getRectOfWall())) {
                aliveOfMissile = false;
                twc.missilesOfMyTank.remove(this);
            }
            
            // 其他玩家主坦克子弹与我方坦克碰撞
//            for (int j=0; j<twc.missilesOfEnemyTanks.size(); j++) {
//                Missile mofen = twc.missilesOfEnemyTanks.get(j);
//                Tank myTank = twc.myTank;
//                if (myTank != null && mofen != null && myTank.getAliveOfTank() 
//                    && mofen.getAliveOfMissile()) {
//                    if (myTank.getRectOfTank().intersects(mofen.getRectOfMissle())) {
//                        hitTank(myTank);
//                        mofen.setAliveOfMissile(false);
//                    }
//                }
//            }
            
            // 玩家坦克子弹与子弹碰撞
            for (int i=0; i<twc.missilesOfMyTank.size(); i++) {
                Missile mofmy = twc.missilesOfMyTank.get(i);
                if (this.equals(mofmy)) {
                    break;
                } else if (mofmy !=null && mofmy.getAliveOfMissile()) {
                    this.getRectOfMissle().intersects(mofmy.getRectOfMissle());
                }
            }
            
            // 主坦克子弹与机器人坦克子弹碰撞
            for (int i=0; i<twc.missilesOfMyTank.size(); i++) {
                for (int j=0; j<twc.missilesOfEnemyTanks.size(); j++) {
                    Missile mofmy = twc.missilesOfMyTank.get(i);
                    Missile mofen = twc.missilesOfEnemyTanks.get(j);
                    
                    if (mofmy != null && mofen != null && mofmy.getAliveOfMissile() 
                        && mofen.getAliveOfMissile()) {
                        
                        if (mofmy.getRectOfMissle().intersects(mofen.getRectOfMissle())) {
                            mofmy.setAliveOfMissile(false);
                            mofen.setAliveOfMissile(false);
                        }
                    }
                }
            }
            
            // 子弹与玩家坦克碰撞检测
            hitPlayerTanks(twc.tanks);
            
            // 子弹与机器人坦克碰撞检测
            hitEnemyTanks(twc.enemyTanks);
        }
    }
    
    /**
    * @Title: getRectOfMissle
    * @Description: (获取炮弹的矩形区域对象，用于碰撞检测)
    * @param @return    参数 
    * @return Rectangle    返回类型
    * @throws
     */
    public Rectangle getRectOfMissle() {
        return new Rectangle(x, y, MISSILE_WIDTH, MISSILE_HEIGHT);
    }
}
