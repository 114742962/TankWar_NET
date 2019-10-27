/**
* @Title: PropertiesManager.java
* @Package com.guiyajun.tank
* @Description: TODO(用一句话描述该文件做什么)
* @author Administrator
* @date 2019年10月26日
* @version V1.0
*/
package com.guiyajun.tank;

import java.io.IOException;
import java.util.Properties;

 /**
 * @ProjectName:  [TankWar_NET] 
 * @Package:      [com.guiyajun.tank.PropertiesManager.java]  
 * @ClassName:    [PropertiesManager]   
 * @Description:  [加载配置文件到静态内存]   
 * @Author:       [桂亚君]   
 * @CreateDate:   [2019年10月26日 下午9:52:55]   
 * @UpdateUser:   [桂亚君]   
 * @UpdateDate:   [2019年10月26日 下午9:52:55]   
 * @UpdateRemark: [说明本次修改内容]  
 * @Version:      [v1.0]
 */
public class PropertiesManager {
    private static Properties properties = new Properties();
    static {
        try {
            properties.load(PropertiesManager.class.getClassLoader().getResourceAsStream("config/tank.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private PropertiesManager() {}
    
    public static String getPerproty(String key) {
        return properties.getProperty(key);
    }
}
