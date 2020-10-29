package com.learn.simple.netty.dubborpc.netty;

import jdk.nashorn.internal.objects.annotations.Property;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *  动态代理：
 *   代理对象不需要实现接口，但是目标对象要实现接口，否则不能使用动态代理
 *   代理对象的生成，是利用jdk的API + 动态的在内存中构建代理对象
 *   动态代理也叫做：JDK代理 ，接口代理
 * @ClassName: NettyClientProxyHandler
 * @Description: 动态代理类
 * @Author: lin
 * @Date: 2020/10/26 16:27
 * History:
 * @<version> 1.0
 */
public class NettyClientProxyHandler  {

    /**
     * 维护一个目标对象
     */
    private Object target;



    /**
     * 构造器，对target 和 clientHandler进行初始化
     * @param target
     */
    public  NettyClientProxyHandler(Object target){
         this.target = target;
    }


    /**
     * 说明：
     * public static Object newProxyInstance(ClassLoader loader,
     *                                           Class<?>[] interfaces,
     *                                           InvocationHandler h)
     *1、ClassLoader loader:指定当前目标对象使用的类加载器，获取加载器的方法固定
     *2、Class<?>[] interfaces：目标对象实现的接口类型，使用泛型方式 确认类型
     *3、InvocationHandler h: 事件处理，执行目标对象的方法时，会触发事情处理器方法，
     *  会把当前执行的目标对象方法作为一个参数
     *
     * @return
     */
     public Object getProxyInstance(){
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("代理开始......");
                        //通过反射机制调用目标对象的方法
                        Object returnVal = method.invoke(target, args);
                        return returnVal;
                    }
                });
     }
}
