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
    /** 声明一个配置文件工具类的实例 */
    private static Properties properties = new Properties();
    /** 通过静态加载的方式将配置文件加载到内存 */
    static {
        try {
            properties.load(PropertiesManager.class.getClassLoader().getResourceAsStream("config/tank.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // 将构造方法设置为私有禁止通过new来创建类的实例
    private PropertiesManager() {}
    
    /**
    * @Title: getPerproty
    * @Description: 通过变量名获取到配置文件中对应的值
    * @param @param key参数的变量名 
    * @return String    返回类型
    * @throws
     */
    public static String getPerproty(String key) {
        return properties.getProperty(key);
    }
}
