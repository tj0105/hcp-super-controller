package org.onosproject.hcp.protocol.ver10;

import com.google.common.hash.PrimitiveSink;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.hcp.exceptions.HCPParseError;
import org.onosproject.hcp.protocol.*;
import org.onosproject.hcp.types.HCPInternalLink;
import org.onosproject.hcp.types.U32;
import org.onosproject.hcp.util.ChannelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.WildcardType;
import java.util.List;

/**
 * @Author ldy
 * @Date: 20-2-24 下午8:42
 * @Version 1.0
 */
public class HCPTopologyReplyVer10 implements HCPTopologyReply{
    public final static Logger logger = LoggerFactory.getLogger(HCPTopologyReplyVer10.class);

    //version = 1
    private final static byte WIRE_VERSON=1;
    private final static int HEAD_LENGTH=8;

    //hco topology reoply message field
    private final long xid;
    private final List<HCPInternalLink> internalLinks;

    public HCPTopologyReplyVer10(long xid,List<HCPInternalLink> internalLinks){
        if (internalLinks==null){
            throw new NullPointerException("HCPTopologyVer10: property internalinks must not be null");
        }
        this.xid=xid;
        this.internalLinks=internalLinks;
    }
    @Override
    public HCPVersion getVersion() {
        return HCPVersion.HCP_10;
    }

    @Override
    public HCPType getType() {
        return HCPType.HCP_TOPO_REPLY;
    }

    @Override
    public long getXid() {
        return xid;
    }

    @Override
    public List<HCPInternalLink> getInternalLink() {
        return internalLinks;
    }

    static final Reader READER=new Reader();
    static class Reader implements HCPMessageReader<HCPTopologyReply>{

        @Override
        public HCPTopologyReply readFrom(ChannelBuffer bb) throws HCPParseError {
            int startIndex=bb.readerIndex();
            //version
            byte version=bb.readByte();
            //check version
            if(version!=(byte)0x1)
                throw new HCPParseError("Wrong version:Expected=HCPVersion.HCP_10(1), got="+version);
            //type
            byte type=bb.readByte();
            if (type!=(byte)0xa)
                throw new HCPParseError("Wrong type:Expected=HCPType.HCP_TOPOLOGY_REPLY(10),got="+type);
            //length
            int length=bb.readShort();
            if (bb.readableBytes()+(bb.readerIndex()-startIndex)<length){
                bb.readerIndex(startIndex);
                return null;
            }
            //xid
            long xid= U32.f(bb.readInt());

            //internalLinks
            List<HCPInternalLink> internalLinks= ChannelUtils.readList(bb,length-(bb.readerIndex()-startIndex),HCPInternalLinkVer10.READER);

            return new HCPTopologyReplyVer10(xid,internalLinks) ;
        }
    }

    @Override
    public void writeTo(ChannelBuffer bb) {
        WRITER.write(bb,this);
    }

    static final Writer WRITER=new Writer();
    static class Writer implements HCPMessageWriter<HCPTopologyReplyVer10>{

        @Override
        public void write(ChannelBuffer bb, HCPTopologyReplyVer10 message) {
            int startIndex=bb.writerIndex();

            //version
            bb.writeByte(WIRE_VERSON);
            //type
            bb.writeByte(HCPType.HCP_TOPO_REPLY.value());
            //temp length
            int lengthIndex=bb.writerIndex();
            //temporary length
            bb.writeShort(0);
            //xid
            bb.writeInt(U32.t(message.xid));
            //internalLinks
            ChannelUtils.writeList(bb,message.internalLinks);

            //update length
            int length=bb.writerIndex()-startIndex;
            bb.setShort(lengthIndex,length);
        }
    }
    @Override
    public HCPMessage.Builder createBuilder() {
        return null;
    }

    static class Builder implements HCPTopologyReply.Builder{
        private long xid;
        private List<HCPInternalLink> internalLinks;
        @Override
        public HCPTopologyReply build() {
            if (internalLinks==null)
                throw new NullPointerException("HCPTopologyVer10: property internalinks must not be null");
            return new HCPTopologyReplyVer10(xid,internalLinks);
        }

        @Override
        public HCPVersion getVersion() {
            return HCPVersion.HCP_10;
        }

        @Override
        public HCPType getType() {
            return HCPType.HCP_TOPO_REPLY;
        }

        @Override
        public long getXid() {
            return xid;
        }

        @Override
        public HCPTopologyReply.Builder setXid(long xid) {
            this.xid=xid;
            return this;
        }

        @Override
        public List<HCPInternalLink> getInternalLink() {
            return internalLinks;
        }

        @Override
        public HCPTopologyReply.Builder setInternalLink(List<HCPInternalLink> InternalLinklist) {
            this.internalLinks=InternalLinklist;
            return this;
        }
    }

    @Override
    public Builder creteBuilder() {
        return null;
    }

    @Override
    public void putTo(PrimitiveSink sink) {

    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj)
            return false;
        if (obj==null|| getClass()!=obj.getClass())
            return false;
        HCPTopologyReplyVer10 other=(HCPTopologyReplyVer10) obj;
        if(this.xid!=other.xid)
            return false;
        if (!this.internalLinks.equals(other.internalLinks))
            return false;
        return true;
    }
}
