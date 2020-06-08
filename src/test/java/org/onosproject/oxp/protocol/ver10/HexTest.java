package org.onosproject.oxp.protocol.ver10;

import org.junit.Test;
import org.onlab.packet.IpAddress;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author ldy
 * @Date: 20-3-3 下午10:37
 * @Version 1.0
 */
public class HexTest {
    @Test
    public  void main() {
        System.out.println(String.format("%016d",10));
        System.out.println(String.format("%016x",10));
//        Map<String,Long> hashmap=new HashMap<>();
//        Thread thread=new Thread(){
//            @Override
//            public void run() {
//                while (true){
//                    System.out.println("wo ai ni");
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        thread.start();
//        System.out.println("hao a ");
//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        IpAddress ipAddress=IpAddress.valueOf("192.168.109.12");
//        IpAddress dst=IpAddress.valueOf("192.168.109.13");
//        String ss=ipAddress.toString()+" "+dst.toString();
//        hashmap.put(ss,System.currentTimeMillis());
//        long s=hashmap.get(ss);
//        System.out.println(hashmap.containsKey("1"));
//        System.out.println(s);
//        System.out.println(hashmap.toString());
//        IpAddress ipAddress1=IpAddress.valueOf("192.168.109.12");
//        IpAddress dst2=IpAddress.valueOf("192.168.109.13");
//        String ss1=ipAddress.toString()+" "+dst.toString();
//        hashmap.put(ss1,System.currentTimeMillis());
//        System.out.println(hashmap.toString());
//        System.out.println(ss);
//        System.out.println (String.format("%06x",2222));
    }
    @Test
    public void longTest(){
       String s="hcp:0000000000000000001a";
        System.out.println(Integer.parseInt(s.split(":")[1],16));
    }
    @Test
    public void andTest(){
        int flag=0;
        flag |=1<<0;
        System.out.println(Integer.toBinaryString(flag).equals("1"));
        flag |=1<<1;
        System.out.println(Integer.toBinaryString(flag));
        flag |=1<<2;
        System.out.println(Integer.toBinaryString(flag));
    }
    @Test
    public void ListSort(){
        List<Integer> list=new ArrayList<>();
        list.add(1);
        list.add(3);
        list.add(6);
        list.add(2);
        list.add(8);
        list.add(10);
        list.add(4);
        list.sort((p1,p2)->p1<p2?1:p1>p2?-1:0);
        System.out.println(list.toString());

    }

    @Test
    public void HashMap(){
        Map<String,Integer> integerIntegerMap=new HashMap<>();
        integerIntegerMap.put("1",2);
//        integerIntegerMap.put("2",3);
        integerIntegerMap.put("1",2);
        System.out.println(!integerIntegerMap.containsKey("1"));
        System.out.println(integerIntegerMap.toString());
        integerIntegerMap.put("1",4);
        System.out.println(integerIntegerMap.toString());
    }
}
