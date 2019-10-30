package com.guiyajun.tank;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JRadioButton;

/**
 * @ProjectName:  [TankWar] 
 * @Package:      [com.guiyajun.tank.TankWarClient.java]  
 * @ClassName:    [TankWarClient]   
 * @Description:  [游戏客户端的画面和元素，监听事件]   
 * @Author:       [Guiyajun]   
 * @CreateDate:   [2019年10月22日 下午4:19:54]   
 * @UpdateUser:   [Guiyajun]   
 * @UpdateDate:   [2019年10月22日 下午4:19:54]   
 * @UpdateRemark: [说明本次修改内容]  
 * @Version:      [v1.0]
 */
public class TankWarClient extends Frame {
    
    /** 游戏窗口的宽度 */
    public static final int GAME_WIDTH = 800;
    /** 游戏窗口的高度 */ 
    public static final int GAME_HEIGHT = 600;
    /** 记分区的高度，宽度与游戏客户端宽度一致，这里不做申明 */ 
    public static final int SCORE_AREA = 60;
    /** 在界面左边实例化一个钢化墙壁 */
    public Wall wallLeft = new Wall(180, 150, 20, 350, this);
    /** 在界面右边实例化一个钢化墙壁 */
    public Wall wallRight = new Wall(600, 150, 20, 350, this);
    /** 敌方坦克集合 */
    public List<EnemyTank> enemyTanks = new ArrayList<>();
    /** 玩家坦克集合 */
    public List<MyTank> tanks = new ArrayList<>();
    /** 敌方坦克打出的炮弹集合 */
    public List<Missile> missilesOfEnemyTanks = new ArrayList<>();
    /** 我方坦克打出的炮弹集合 */
    public List<Missile> missilesOfMyTank = new ArrayList<>();
    /** 屏幕上产生的爆炸集合 */
    public List<Explode> explodes = new ArrayList<>();
    /** 屏幕上产生的加血包集合 */
    public List<Blood> bloods = new ArrayList<>();
    /** 定义一个虚拟屏幕，目的是双缓冲，先把图片画到虚拟屏幕上 */
    Image backScreen = null;
    /** 主坦克 */
    MyTank myTank = null;
    /** 网络客户端 */
    NetClient netClient = null;
    /** 网络服务端 */
    NetServer netServer = null;
    /** 网络服务端线程 */
    ServerThread serverThread = null;
    
    /**
     * @Fields field:field:(显示声明serialVersionUID可以避免对象不一致)
     */
    private static final long serialVersionUID = 1L;
    
    /**
    * @Title: main
    * @Description: 游戏的主函数，创建游戏客户端，并在客户端上加载游戏的所有元素
    * @param @param args    参数 
    * @return void    返回类型
    * @throws
     */
    public static void main(String[] args) {
        // 创建一个客户端
        TankWarClient twk = new TankWarClient();
        // 加载客户端上的元素
        twk.lauchFrame();
    }
    
    /**
    * @Title: lauchFrame
    * @Description: 设置客户端格式，定义游戏界面的元素
    * @return void    返回类型
    * @throws
     */
    public void lauchFrame() {
        // 定义客户端的属性
        this.setTitle("TankWar");
        this.setLocation(40, 120);
        this.setSize(GAME_WIDTH, GAME_HEIGHT);
        this.setBackground(Color.LIGHT_GRAY);
        this.setLayout(null);
        this.setResizable(false);
        this.setVisible(true);
        
        ConnectJDialog connectJDialog = new ConnectJDialog();
        connectJDialog.setVisible(true);
        
        // 启动画面刷新线程池
        ThreadPoolService.getInstance().execute(new PaintTank()); 
        // 加入window事件监听
        this.addWindowListener(new WindowAdapter() {    
            @Override
            public void windowClosing(WindowEvent e) {
                Message message = null;
                if (myTank != null) {
                    message = new TankExitMessage(myTank);
                    myTank.setAliveOfTank(false);
                }
                setVisible(false);
                if (netClient != null && message != null) {
                    netClient.send(message);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } finally {
                    System.exit(-1);
                }
            }
        });
        
        // 加入键盘事件监听
        this.addKeyListener(new KeyAdapter() {  
            @Override
            public void keyPressed(KeyEvent e) {    
                myTank.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                myTank.keyReleased(e);
            }
        });
    }
    
    @Override
    public void paint(Graphics g) {
        // 画出敌方坦克集合中所有活着的坦克并将被摧毁的坦克移除
        for (int j=0; j<enemyTanks.size(); j++) {
            EnemyTank et = enemyTanks.get(j);
            
            if (et.getAliveOfTank()) {
                et.draw(g);
            }
            else {
                enemyTanks.remove(et);
            }
        }
        
        // 画出坦克集合中所有活着的玩家主坦克并将被摧毁的坦克移除
        for (int j=0; j<tanks.size(); j++) {
            MyTank tank = tanks.get(j);
            
            if (tank.getAliveOfTank()) {
                tank.draw(g);
            }
            else {
                tanks.remove(tank);
            }
        }
        
        // 画出我方炮弹集合中所有活着的炮弹并将被摧毁的炮弹移除
        for (int i=0; i<missilesOfMyTank.size(); i++) {
            Missile m = missilesOfMyTank.get(i);
            if (m != null) {
                m.hitEnemyTanks(enemyTanks);
                m.hitPlayerTanks(tanks);
                
                if (m.getAliveOfMissile()) {
                    m.draw(g);
                } else {
                    missilesOfMyTank.remove(m);
                }
            }
        }
        
        // 画出敌方炮弹集合中所有活着的炮弹并将被摧毁的炮弹移除
        for (int i=0; i<missilesOfEnemyTanks.size(); i++) {
            Missile m = missilesOfEnemyTanks.get(i);
            m.hitTank(myTank);
            
            if (m.getAliveOfMissile()) {
                m.draw(g);
            }
            else {
                missilesOfEnemyTanks.remove(m);
            }
        }
        
        // 画出爆炸集合中所有活着的爆炸效果，并移除过期的
        for (int i=0; i<explodes.size(); i++) {
            Explode ep = explodes.get(i);
            if (!ep.isAliveOfExplode()) {
                explodes.remove(ep);
            }
            ep.draw(g);
        }
        
        // 画出所有活着的加血包并移除过期的
        for (int i=0; i<bloods.size(); i++) {
            Blood blood = bloods.get(i);
            if (!blood.getAlive()) {
                bloods.remove(blood);
            }
            blood.draw(g);
        }
        
        // 画出主坦克
        if (myTank != null) {
            myTank.draw(g);
        }
        // 画出左墙
        wallLeft.draw(g);
        // 画出右墙
        wallRight.draw(g);
    }
    
    @Override
    public void update(Graphics g) {
        // 在一个虚拟屏幕上画图，画完后再显示到真实屏幕上，利用双缓冲解决界面闪烁问题
        if(backScreen == null) {
            backScreen = this.createImage(GAME_WIDTH, GAME_HEIGHT);
        }
        
        // 获取虚拟屏幕的画笔
        Graphics gOfBackScreen = backScreen.getGraphics();
        // 获取画笔的初始颜色
        Color c = gOfBackScreen.getColor();
        // 设置画笔的颜色为粉色
        gOfBackScreen.setColor(Color.PINK);
        // 画出记分区
        gOfBackScreen.fillRect(0, 0, GAME_WIDTH, SCORE_AREA);
        // 设置画笔的颜色为橘色
        gOfBackScreen.setColor(Color.ORANGE);
        // 画出游戏区
        gOfBackScreen.fillRect(0, SCORE_AREA, GAME_WIDTH, GAME_HEIGHT - SCORE_AREA);
        // 设置画笔的颜色为黑色
        gOfBackScreen.setColor(Color.BLACK);
        // 记分区画出我方坦克血量值
        if (myTank != null) {
            gOfBackScreen.drawString("BloodOfMyTank: " + myTank.getBloodOfTank(), 20, 50);
        }
        // 记分区画出敌方坦克数量
        gOfBackScreen.drawString("EnemyTanks: " + enemyTanks.size(), 150, 50);
        // 记分区画出屏幕中出现的我方炮弹数量
        gOfBackScreen.drawString("Missiles: " + missilesOfMyTank.size(), 260, 50);
        // 记分区画出爆炸事件数量
        gOfBackScreen.drawString("Explode: " + explodes.size(), 340, 50);
        // 记分区画出屏幕中出现的敌方炮弹数量
        gOfBackScreen.drawString("MissilesOfEnemy: " + missilesOfEnemyTanks.size(), 420, 50);
        // 使用虚拟屏画笔画出屏幕上的元素
        paint(gOfBackScreen);
        // 还原虚拟屏画笔的初始颜色
        gOfBackScreen.setColor(c);
        // 使用真实屏幕画笔将虚拟屏画出
        g.drawImage(backScreen, 0, 0, null);   
    }
    
    /**
     * @ProjectName:  [TankWar] 
     * @Package:      [com.guiyajun.tank.TankWarClient.java]  
     * @ClassName:    [PaintTank]   
     * @Description:  定义一个内部的线程类，实现刷新屏幕
     * @Author:       [Guiyajun]   
     * @CreateDate:   [2019年10月22日 下午3:58:05]   
     * @UpdateUser:   [Guiyajun]   
     * @UpdateDate:   [2019年10月22日 下午3:58:05]   
     * @UpdateRemark: [说明本次修改内容]  
     * @Version:      [v1.0]
     */
    private class PaintTank implements Runnable {
        @Override
        public void run() {
            while(true) {
                // 调用重画方法，重画方法会先调用update方法，再调用paint方法
                repaint();      
                try {
                    // 每刷新一次等待30ms
                    Thread.sleep(30);   
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private class ServerThread implements Runnable {

        public void run() {
            if (netServer != null) {
                netServer.start();
            }
        }
    }
    
    private class ConnectJDialog extends JDialog {
        /**
        * @Fields field:field:{todo}(用一句话描述这个变量表示什么)
        */
        private static final long serialVersionUID = 1L;
        ButtonGroup bg = new ButtonGroup();
        JButton button = new JButton("确定");
        JRadioButton radioButtonServer = new JRadioButton("server");
        JRadioButton radioButtonClient = new JRadioButton("client", true);
        TextField serverIP = new TextField("127.0.0.1",12);
        TextField TCPServerPort = new TextField(PropertiesManager.getPerproty("TCPServerPort"), 5);
        TextField UDPServerPort = new TextField(PropertiesManager.getPerproty("UDPServerPort"), 5);
        TextField UDPClientPort = new TextField(PropertiesManager.getPerproty("UDPClientPort"), 5);
        ConnectJDialog() {
            super(TankWarClient.this, "Setting");
            this.setSize(190, 200);
            this.setLocation(350, 300);
            this.setResizable(false);
            this.setModal(true);
            this.setLayout(new FlowLayout());
            bg.add(radioButtonServer);
            bg.add(radioButtonClient);
            this.add(radioButtonServer);
            this.add(radioButtonClient);
            this.add(new Label("ServerIP:"));
            this.add(serverIP);
            this.add(new Label("TCPServerPort:"));
            this.add(TCPServerPort);
            this.add(new Label("UDPServerPort:"));
            this.add(UDPServerPort);
            this.add(new Label("UDPClientPort:"));
            this.add(UDPClientPort);
            this.add(button);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // 创建一个服务端实例
                    netServer = new NetServer();
                    String ip = serverIP.getText();
                    int TCPServer = Integer.parseInt(TCPServerPort.getText().trim());
                    int UDPServer = Integer.parseInt(UDPServerPort.getText().trim());
                    int UDPClient = Integer.parseInt(UDPClientPort.getText().trim());
                    NetServer.TCPServerPort = TCPServer;
                    NetServer.UDPServerPort = UDPServer;
                    netClient = new NetClient(TankWarClient.this);                    
                    NetClient.serverIP = ip;
                    NetClient.UDPClientPort = UDPClient;
                    setVisible(false);
                    if (radioButtonServer.isSelected() && serverThread == null) {
                        serverThread = new ServerThread();
                        ThreadPoolService.getInstance().execute(serverThread);
                    }
                    // 客户端连接服务端
                    netClient.connect();
                    Message message = new TankNewMessage(myTank);
                    netClient.send(message);
                }
            });
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    setVisible(false);
                }
            });
        }
    }
}
