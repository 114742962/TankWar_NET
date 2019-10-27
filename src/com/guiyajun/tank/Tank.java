package com.guiyajun.tank;
import java.awt.*;

/**
 * @ProjectName:  [TankWar] 
 * @Package:      [com.guiyajun.tank.Tank.java]  
 * @ClassName:    [Tank]   
 * @Description:  [坦克的共有属性和方法，为其他坦克的父类]   
 * @Author:       [Guiyajun]   
 * @CreateDate:   [2019年10月22日 下午4:18:46]   
 * @UpdateUser:   [Guiyajun]   
 * @UpdateDate:   [2019年10月22日 下午4:18:46]   
 * @UpdateRemark: [说明本次修改内容]  
 * @Version:      [v1.0]
 */
public class Tank {
    /** 坦克宽度 */
    public static final int TANK_WIDTH = 32;
    /** 坦克高度 */
    public static final int TANK_HEIGHT = 32;
    /** 坦克移动速度 */
    public static final int TANK_MOVESTEP = 4;
    /** 客户端实例变量用于管理元素  */
    public TankWarClient twc = null;
    /** 坦克的编号 */
    public int id;
    /** 坦克X坐标 */
    protected int x;
    /** 坦克Y坐标 */
    protected int y;
    /** 坦克上一次X坐标，用于坦克越界后复位 */
    protected int xOld;
    /** 坦克上一次Y坐标，用于坦克越界后复位 */
    protected int yOld;
    /** 初始化坦克的移动方向 */
    protected Direction dir = Direction.STOP; 
    /** 初始化炮筒的方向 */
    protected Direction dirOfBarrel = Direction.UP;
    /** 坦克的友好性，为联网版设计，单机版没有实际作用 */
    protected boolean friendly = true;
    /** 坦克的初始血量 */
    private int bloodOfTank = 4;
    /** 坦克的存活状态 */
    private boolean aliveOfTank = true;
    
    /**
    * @Description: 创建一个新的实例 Tank.
    * @param x  x坐标
    * @param y  y坐标
    * @param friendly 相对于主坦克的友好性
     */
    public Tank(int x, int y, boolean friendly) {
        this.x = x;
        this.y = y;
        this.xOld = x;
        this.yOld = y;
        this.friendly = friendly;
    }
    
    /**
    * @Description: 创建一个新的实例 Tank.
    * @param x  x坐标
    * @param y  y坐标
    * @param friendly   相对于主坦克的友好性
    * @param twc 客户端实例
     */
    public Tank(int x, int y, boolean friendly,TankWarClient twc) {
        this(x, y, friendly);
        this.twc = twc;
    }
    
    /**
    * @Title: move
    * @Description: 坦克的移动方法，根据坦克方向算出下一步的坦克的位置
    * @param     
    * @return void    返回类型
    * @throws
     */
    protected void move() {
        this.xOld = x;
        this.yOld = y;
        
        switch (dir) {
            case UP:
                y -= TANK_MOVESTEP;
                break;
            case DOWN:
                y += TANK_MOVESTEP;
                break;
            case LEFT:
                x -= TANK_MOVESTEP;
                break;
            case RIGHT:
                x += TANK_MOVESTEP;
                break;
            case UP_LEFT:
                x -= Math.round(TANK_MOVESTEP * (Math.sqrt(2)/2));
                y -= Math.round(TANK_MOVESTEP * (Math.sqrt(2)/2));
                break;
            case UP_RIGHT:
                x += Math.round(TANK_MOVESTEP * (Math.sqrt(2)/2));
                y -= Math.round(TANK_MOVESTEP * (Math.sqrt(2)/2));
                break;
            case DOWN_LEFT:
                x -= Math.round(TANK_MOVESTEP * (Math.sqrt(2)/2));
                y += Math.round(TANK_MOVESTEP * (Math.sqrt(2)/2));
                break;
            case DOWN_RIGHT:
                x += Math.round(TANK_MOVESTEP * (Math.sqrt(2)/2));
                y += Math.round(TANK_MOVESTEP * (Math.sqrt(2)/2));
                break;
            case STOP:
                break;
            default:
                break;
        }
    }
    
    /**
    * @Title: getDirection
    * @Description: 获取坦克的移动方向，这里未实现，每个子类需要复写自己的方向获取方法
    * @param      
    * @return void    返回类型
    * @throws
     */
    void getDirection() {}
    
    /**
    * @Title: getBarrelDirection
    * @Description: 根据坦克的移动方向改变炮筒的方向
    * @param     
    * @return void    返回类型
    * @throws
     */
    void getBarrelDirection() {
        if (dir != Direction.STOP) {
            dirOfBarrel = dir;
        }
    }
    
    /**
    * @Title: draw
    * @Description: 画出坦克的所有元素，子类坦克需要实现自己的draw方法
    * @param @param g    画笔 
    * @return void    返回类型
    * @throws
     */
    public void draw(Graphics g) {}
    
    /**
    * @Title: collisionDetection
    * @Description: 坦克的碰撞检测
    * @param      
    * @return void    返回类型
    * @throws
     */
    public void collisionDetection() {
        if (twc != null) {
            // 坦克与围墙碰撞检测
            if (x < 0) {
                stay();
            }
            else if (y < TankWarClient.SCORE_AREA) {
                stay();
            }
            else if (x > TankWarClient.GAME_WIDTH - Tank.TANK_WIDTH) {
                stay();
            }
            else if (y > TankWarClient.GAME_HEIGHT - Tank.TANK_HEIGHT) {
                stay();
            }
            
            // 坦克与内墙碰撞检测
            if (this.getRectOfTank().intersects(twc.wallLeft.getRectOfWall()) 
                || this.getRectOfTank().intersects(twc.wallRight.getRectOfWall())) {
                this.stay();
            }
            
            // 坦克与坦克的碰撞检测
            for (int i=0; i<twc.enemyTanks.size(); i++) {
                Tank tank = twc.enemyTanks.get(i);
                    
                if (this.getAliveOfTank() && tank.getAliveOfTank() && !this.equals(tank) && !this.equals(twc.myTank)) {
                    if (twc.myTank.getRectOfTank().intersects(this.getRectOfTank())
                        || tank.getRectOfTank().intersects(this.getRectOfTank())) {
                        this.stay();
                    }
                } else if (this.getAliveOfTank() && tank.getAliveOfTank() && this.equals(twc.myTank)) {
                    if (tank.getRectOfTank().intersects(twc.myTank.getRectOfTank())) {
                        twc.myTank.stay();
                    }
                }
            }
            
            // 坦克与加血包碰撞检测
            for(int i=0; i<twc.bloods.size(); i++) {
                for(int j=0; j<twc.enemyTanks.size(); j++) {
                    Blood blood = twc.bloods.get(i);
                    Tank tank = twc.enemyTanks.get(j);
                    if (blood.getAlive() && tank.getAliveOfTank() && blood.getRectOfBlood()
                        .intersects(tank.getRectOfTank())) {
                        blood.setAlive(false);
                        tank.setBloodOfTank(4);
                    } else if (blood.getAlive() && blood.getRectOfBlood().intersects(twc.myTank
                        .getRectOfTank())) {
                        blood.setAlive(false);
                        twc.myTank.setBloodOfTank(4);
                    }
                }
            }
        }
    }
    
    /**
    * @Title: fire
    * @Description: 坦克发射炮弹方法
    * @param @param color 炮弹的颜色
    * @param @param dirOfBarrel 炮筒的方向
    * @return Missile    返回类型
    * @throws
     */
    public Missile fire(Color color, Direction dirOfBarrel) {
        if (!getAliveOfTank()) {
            return null;
        }
        
        Missile missileOfMyTank = null;
        
        // 根据炮筒的方向和炮筒尾部的位置打出炮弹
        switch(dirOfBarrel) {   
            case UP:
                missileOfMyTank = new Missile(x + TANK_WIDTH / 2 - Missile.MISSILE_WIDTH / 2, 
                    y - TANK_HEIGHT * 1 / 4 - Missile.MISSILE_HEIGHT / 2, dirOfBarrel, this.twc, color);
                break;
            case DOWN:
                missileOfMyTank = new Missile(x + TANK_WIDTH/2 - Missile.MISSILE_WIDTH/2, 
                    y + TANK_HEIGHT * 5 / 4 - Missile.MISSILE_HEIGHT/2, dirOfBarrel, this.twc, color);
                break;
            case LEFT:
                missileOfMyTank = new Missile(x - TANK_WIDTH * 1 / 4 - Missile.MISSILE_WIDTH/2, 
                    y + TANK_HEIGHT/2 - Missile.MISSILE_HEIGHT/2, dirOfBarrel, this.twc, color);
                break;
            case RIGHT:
                missileOfMyTank = new Missile(x + TANK_WIDTH * 5 / 4 - Missile.MISSILE_WIDTH/2, 
                    y + TANK_HEIGHT/2 - Missile.MISSILE_HEIGHT/2, dirOfBarrel, this.twc, color);
                break;
            case UP_LEFT:
                missileOfMyTank = new Missile(x, y, dirOfBarrel, this.twc, color);
                break;
            case UP_RIGHT:
                missileOfMyTank = new Missile(x + TANK_WIDTH, y - Missile.MISSILE_HEIGHT, dirOfBarrel, this.twc, color);
                break;
            case DOWN_LEFT:
                missileOfMyTank = new Missile(x - Missile.MISSILE_WIDTH / 2, y + TANK_HEIGHT - Missile
                    .MISSILE_HEIGHT/2,dirOfBarrel, this.twc, color);
                break;
            case DOWN_RIGHT:
                missileOfMyTank = new Missile(x + TANK_WIDTH, y + TANK_HEIGHT, dirOfBarrel, this.twc, color);
                break;
            default:
                break;
        }
        
        return missileOfMyTank;
    }
    
    /**
    * @Title: drawBarrel
    * @Description: 根据坦克的方向画出炮筒
    * @param @param g    画笔 
    * @return void    返回类型
    * @throws
     */
    public void drawBarrel(Graphics g) {  
        getBarrelDirection();
        
        switch(dirOfBarrel) {   
            case UP:
                g.drawLine(x + TANK_WIDTH/2, y + TANK_HEIGHT/2, x + TANK_WIDTH/2, y - TANK_HEIGHT * 1 / 4);
                break;
            case DOWN:
                g.drawLine(x + TANK_WIDTH/2, y + TANK_HEIGHT/2, x + TANK_WIDTH/2, y + TANK_HEIGHT * 5 / 4);
                break;
            case LEFT:
                g.drawLine(x + TANK_WIDTH/2, y + TANK_HEIGHT/2, x - TANK_WIDTH * 1 / 4, y + TANK_HEIGHT/2);
                break;
            case RIGHT:
                g.drawLine(x + TANK_WIDTH/2, y + TANK_HEIGHT/2, x + TANK_WIDTH * 5 / 4, y + TANK_HEIGHT/2);
                break;
            case UP_LEFT:
                g.drawLine(x + TANK_WIDTH/2, y + TANK_HEIGHT/2, x, y);
                break;
            case UP_RIGHT:
                g.drawLine(x + TANK_WIDTH/2, y + TANK_HEIGHT/2,x + TANK_WIDTH, y);
                break;
            case DOWN_LEFT:
                g.drawLine(x + TANK_WIDTH/2, y + TANK_HEIGHT/2, x, y + TANK_HEIGHT);
                break;
            case DOWN_RIGHT:
                g.drawLine(x + TANK_WIDTH/2, y + TANK_HEIGHT/2, x + TANK_WIDTH, y + TANK_HEIGHT);
                break;
            default:
                break;
        }
    }
    
    /**
    * @Title: stay
    * @Description: 记录坦克上一次移动后的坐标信息，用于坦克越界复位
    * @return void    返回类型
    * @throws
     */
    public void stay() {
        x = xOld;
        y = yOld;
    }
    
    /**
    * @Title: reduceBloodOfTank
    * @Description: 减少坦克的血量值，可以直接定义减少的血量数
    * @param @param numOfBlood    血量数
    * @return void    返回类型
    * @throws
     */
    public void reduceBloodOfTank(int numOfBlood) {
        this.bloodOfTank -= numOfBlood;
    }
    
    /**
     * @Title: drawBlood
     * @Description: 根据坦克的血量及方向画出坦克的血量值，血量值永远在炮筒后方显示
     * @param @param g   画笔
     * @param @param bloodOfTank    血量值 
     * @return void    返回类型
     * @throws
     */
    public void drawBlood(Graphics g, int bloodOfTank) {
        // 根据炮筒方向画出血量值
        getBarrelDirection();
        switch(dirOfBarrel) {   
        case UP:
            g.drawString(" " + bloodOfTank, x + 10, y + 27);
            break;
        case DOWN:
            g.drawString(" " + bloodOfTank, x + 10, y + 12);
            break;
        case LEFT:
            g.drawString(" " + bloodOfTank, x + 18, y + 20);
            break;
        case RIGHT:
            g.drawString(" " + bloodOfTank, x + 5, y + 20);
            break;
        case UP_LEFT:
            g.drawString(" " + bloodOfTank, x + 15, y + 25);
            break;
        case UP_RIGHT:
            g.drawString(" " + bloodOfTank, x + 5, y + 25);
            break;
        case DOWN_LEFT:
            g.drawString(" " + bloodOfTank, x + 15, y + 15);
            break;
        case DOWN_RIGHT:
            g.drawString(" " + bloodOfTank, x + 5, y + 18);
            break;
        default:
            break;
        }
    }
    
    /**
    * @Title: setAliveOfTank
    * @Description: 设置坦克的生存状态
    * @param @param aliveOfTank    false为摧毁
    * @return void    返回类型
    * @throws
     */
    public void setAliveOfTank(boolean aliveOfTank) {
        this.aliveOfTank = aliveOfTank;
    }
    
    /**
    * @Title: getRectOfTank
    * @Description: (获取坦克的矩形区域对象，用于碰撞检测)
    * @return Rectangle    返回类型
    * @throws
     */
    public Rectangle getRectOfTank() {
        return new Rectangle(x, y, TANK_WIDTH, TANK_HEIGHT);
    }
    
    /**
    * @Title: getBloodOfTank
    * @Description: 获取坦克的血量值
    * @return int    返回类型
    * @throws
     */
    public int getBloodOfTank() {
        return bloodOfTank;
    }
    
    /**
    * @Title: setBloodOfTank
    * @Description: 设置坦克的血量值
    * @param @param bloodOfTank    血量值 
    * @return void    返回类型
    * @throws
     */
    public void setBloodOfTank(int bloodOfTank) {
        this.bloodOfTank = bloodOfTank;
    }
    
    /**
    * @Title: getAliveOfTank
    * @Description: 获取坦克的生存状态
    * @param @return    参数 
    * @return boolean    返回类型
    * @throws
     */
    public boolean getAliveOfTank() {
        return aliveOfTank;
    }
}
    




