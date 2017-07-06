package com.netease.qa.log.debugger;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Buff for read every read() operation
 * 
 * @author zhouliangwei
 */
public class CyclicBuffer {

    // in UTF case, most need is 3 bytes every char
    private static final int size   = 10240;

    // file channel read in
    private FileChannel      channel;
    // holder for read in char
    private ByteBuffer       buffer = ByteBuffer.allocate(size);

    public long getRemaining() {
        if (buffer.hasRemaining()) {
            return buffer.remaining();
        } else return 0;
    }

    /**
     * Default constructor
     * 
     * @param channel
     */
    public CyclicBuffer(FileChannel channel) {
        this.channel = channel;
        this.resetBuff();
    }

    public void resetBuff() {
        if (buffer != null) {
            buffer.position(0);
            buffer.limit(0);
        }
    }

    /**
     * single read operation
     * 
     * @return
     * @throws IOException
     */
    private int read() throws IOException {
        int size = channel.read(buffer);
        buffer.flip();
        return size;
    }

    /**
     * Returns the byte just read
     * 
     * @return byte read -1 - end of file reached
     * @throws IOException
     */
    public byte get() throws IOException {
        byte value;
        if (!buffer.hasRemaining()) {
            buffer.clear();
            int eof = read();
            if (eof == -1) {
                return (byte) eof;
            }

        }
        value = buffer.get();
        // This is not necessary if the data is pure utf-8. In my case,
        // sometimes the data was not utf-8
        // then presence of byte 255 will stop further processing of records
        if (value == -1) {
            return 0x20;
        }
        return value;
    }
}
