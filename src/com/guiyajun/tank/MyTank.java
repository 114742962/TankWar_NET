package com.guiyajun.tank;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
 * @ProjectName:  [TankWar] 
 * @Package:      [com.guiyajun.tank.MyTank.java]  
 * @ClassName:    [MyTank]   
 * @Description:  [实现了主坦克属性和方法]   
 * @Author:       [Guiyajun]   
 * @CreateDate:   [2019年10月22日 下午4:14:04]   
 * @UpdateUser:   [Guiyajun]   
 * @UpdateDate:   [2019年10月22日 下午4:14:04]   
 * @UpdateRemark: [说明本次修改内容]  
 * @Version:      [v1.0]
 */
public class MyTank extends Tank {
    /** 主坦克的颜色 */
    public static Color COLOROFMISSILE = Color.BLUE;
    /** 记录左方向按键的状态，true表示按键被按压中，false表示按键被释放了 */
    private boolean beLeft = false;
    /** 记录右方向按键的状态，true表示按键被按压中，false表示按键被释放了 */
    private boolean beRight = false;
    /** 记录上方向按键的状态，true表示按键被按压中，false表示按键被释放了 */
    private boolean beUp = false; 
    /** 记录下方向按键的状态，true表示按键被按压中，false表示按键被释放了 */
    private boolean beDown = false;
    
    /**
    * @Description: 创建一个新的实例 MyTank.
    * @param x  x坐标
    * @param y  x坐标
    * @param friendly   是否友好，目前版本未使用该属性，为后续联网版本设计
    * @param twc
     */
    public MyTank(int x, int y, boolean friendly, TankWarClient twc) {    // 将客户端的引用传入我方坦克
        super(x, y, friendly, twc);
    }
    
    @Override
    public void draw(Graphics g) {
        if(!this.getAliveOfTank()) {
            return;
        }
        
        // 定义坦克的格式
        Color c = g.getColor();
        g.setColor(Color.GREEN);
        g.fillOval(x, y, TANK_WIDTH, TANK_HEIGHT);
        g.setColor(Color.DARK_GRAY);
        
        // 画出坦克的ID
        g.drawString("  " + this.id, x, y+50);
        // 移动坦克
        move();
        // 每移动一步做一次碰撞检测
        collisionDetection();
        // 画出炮筒
        drawBarrel(g);
        g.setColor(Color.RED);
        // 画出主坦克的血量值
        drawBlood(g, getBloodOfTank());
        g.setColor(c);
    }
    
    /**
     * 
    * @Title: keyPressed
    * @Description: 检测到相应键按下时，修改对应键的状态
    * @param @param e    传入按键被按下时的事件
    * @return void    返回类型
    * @throws
     */
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        switch (keyCode) {
            case KeyEvent.VK_UP:
                beUp = true;
                break;
            case KeyEvent.VK_LEFT:
                beLeft = true;
                break;
            case KeyEvent.VK_RIGHT:
                beRight = true;
                break;                    
            case KeyEvent.VK_DOWN:
                beDown = true;
                break;
            default:
                break;
        }
        
        // 刷新坦克方向
        getDirection();     
    }
    
    /**
    * @Title: keyReleased
    * @Description: 检测到相应键释放时，修改对应键的状态或者响应按键的功能
    * @param @param e    传入按键被释放时的事件 
    * @return void    返回类型
    * @throws
     */
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch(keyCode) {
            // F2键我方主坦克复位恢复最初始状态
            case KeyEvent.VK_F2:
                myTankReset();
                break;
             // F键发射炮弹    
            case KeyEvent.VK_F:
                twc.missilesOfMyTank.add(fire(COLOROFMISSILE, dirOfBarrel));
                break;
            case KeyEvent.VK_UP:
                beUp = false;
                break;
            case KeyEvent.VK_LEFT:
                beLeft = false;
                break;
            case KeyEvent.VK_RIGHT:
                beRight = false;
                break;                    
            case KeyEvent.VK_DOWN:
                beDown = false;
                break;
            // 超级发射一次往八个方向各发射一枚炮弹
            case KeyEvent.VK_A:
                superFire(COLOROFMISSILE);
            default:
                break; 
        }
        
        // 刷新坦克方向
        getDirection();     
    }
    
    @Override
    void getDirection() {
        if(beUp && !beDown && !beLeft && !beRight) {dir = Direction.UP;}
        else if (!beUp && beDown && !beLeft && !beRight) {dir = Direction.DOWN;}
        else if (!beUp && !beDown && beLeft && !beRight) {dir = Direction.LEFT;}
        else if (!beUp && !beDown && !beLeft && beRight) {dir = Direction.RIGHT;}
        else if (beUp && !beDown && beLeft && !beRight) {dir = Direction.UP_LEFT;}
        else if (beUp && !beDown && !beLeft && beRight) {dir = Direction.UP_RIGHT;}
        else if (!beUp && beDown && beLeft && !beRight) {dir = Direction.DOWN_LEFT;}
        else if (!beUp && beDown && !beLeft && beRight) {dir = Direction.DOWN_RIGHT;}
        else {dir = Direction.STOP;}
    }
    
    /**
    * @Title: superFire
    * @Description: 超级发射方法，一次往八个方向各发射一枚炮弹
    * @param @param c    炮弹的颜色 
    * @return void    返回类型
    * @throws
     */
    public void superFire(Color c) {
        if (!getAliveOfTank()) {
            return;
        }
        
        // 将枚举变量Direction中的值转化为Direction数组
        Direction[] dirs = Direction.values();
        
        // 每个方向发射一枚炮弹
        for(int i=0; i<dirs.length -1; i++) {
            twc.missilesOfMyTank.add(fire(COLOROFMISSILE, dirs[i]));
        }
    }
    
    /**
    * @Title: myTankReset
    * @Description: 初始化主坦克
    * @param      
    * @return void    返回类型
    * @throws
     */
    public void myTankReset() {
        twc.myTank = new MyTank(700, 400, true, twc);
    }
}
