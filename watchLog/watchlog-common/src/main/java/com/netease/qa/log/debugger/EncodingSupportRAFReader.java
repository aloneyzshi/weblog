package com.netease.qa.log.debugger;


import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;


/**
 * A wrapper of RandomAccessFile to process the readline() encoding issue.
 * @author zhouliangwei
 */
public class EncodingSupportRAFReader {


    /**
     * Default encoding if no encoding or wrong encoding privided.
     */
    private static final String DEFAULT_ENCODING         = "UTF-8";
    public static final int     SINGLE_LINE_LENGTH_LIMIT = 32 * 1024 * 1024;
    private Charset             charset                  = Charset.forName(DEFAULT_ENCODING);

    private CyclicBuffer        buffer;
    // byte push for holding single line
    private ByteBuffer          temp                     = ByteBuffer.allocate(4096);
    // reach end
    private boolean             eof                      = false;

    public long getMyCursor() throws IOException {
        return file.getFilePointer() - buffer.getRemaining();
    }

    /**
     * the raf file handler
     */
    RandomAccessFile file    = null;
    FileChannel      channel = null;

    /**
     * Default constructor
     * 
     * @param channel
     * @param encoding null to use default
     */
    public EncodingSupportRAFReader(RandomAccessFile file, String encoding) {
        this.file = file;
        channel = file.getChannel();
        this.buffer = new CyclicBuffer(channel);
        if (encoding != null && !encoding.trim().equals("") && Charset.isSupported(encoding)) {
            charset = Charset.forName(encoding);
        }
    }
    

    /**
     * jump to the cursor pointer character this method has problem, will not detect new input
     * 
     * @param cursor
     */
    // @Deprecated
    /*
     * public void seek(long cursor) throws IOException { long count = 0; if (eof) { push.resetBuff(); } while (count
     * + 1 < cursor&&(byte) -1 != push.get() ) { count++; } }
     */

    /**
     * read single line
     * 
     * @return
     * @throws IOException
     */
    public String readLine() throws IOException {
        // reach end of file
        if (eof) {
            buffer.resetBuff();
            eof = false;
        }
        String line = null;
        byte x = 0;
        temp.clear();
        int retry = 1;
        while ((byte) '\n' != (x = (buffer.get())) && retry > 0) {
            if (x == -1) {
                retry--;
                if (retry > 0) {
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                    }
                    continue;
                } else {
                    break;
                }
            }
            if (temp.position() == temp.capacity()) {
                temp = addCapacity(temp);
            }

            temp.put(x);
            // following code limited a single line size, should not do this
            if (temp.position() > SINGLE_LINE_LENGTH_LIMIT) {
                if (temp.position() == temp.capacity()) {
                    temp = addCapacity(temp);
                }
                temp.put((byte) '\n');
                break;
            }
        }
        if (x == -1) {
            eof = true;
        } else {
            if (temp.position() == temp.capacity()) {
                temp = addCapacity(temp);
            }
            temp.put((byte) ' ');
        }
        temp.flip();
        if (temp.hasRemaining()) {
            line = charset.decode(temp).toString();
            temp.clear();
            return line;
        } else {
            return null;
        }
    }

    /**
     * when 4096 is not enough
     * 
     * @param temp
     * @return
     */
    private ByteBuffer addCapacity(ByteBuffer temp) {
        ByteBuffer t = ByteBuffer.allocate(temp.capacity() + 1024);
        temp.flip();
        t.put(temp);
        return t;
    }

    public RandomAccessFile getFile() {
        return file;
    }

}
