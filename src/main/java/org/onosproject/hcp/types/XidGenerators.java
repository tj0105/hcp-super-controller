package org.onosproject.hcp.types;

import org.onosproject.hcp.protocol.XidGenerator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author ldy
 * @Date: 20-2-21 下午5:54
 * @Version 1.0
 */
public final class XidGenerators  {
    private final static XidGenerator GLOBEL_XID_GENERATOR=new StandardXidGenerator();

    private XidGenerators(){

    }

    public static XidGenerator create(){
        return new StandardXidGenerator();
    }

    public static XidGenerator global(){
        return GLOBEL_XID_GENERATOR;
    }

}
class StandardXidGenerator implements XidGenerator{

    private final AtomicLong xidGen=new AtomicLong();
    final static long MAX_XID=0xFFFFFFFFL;
    @Override
    public long nextXid() {
        long xid;
        do{
            xid=xidGen.incrementAndGet();
            if(xid>MAX_XID)
            synchronized (this){
                if (xidGen.get()>MAX_XID){
                    xidGen.set(0);
                }
            }
        }while (xid>MAX_XID);
        return xid;
    }
}
