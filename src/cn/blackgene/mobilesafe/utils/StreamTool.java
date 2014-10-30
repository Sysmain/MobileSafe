package cn.blackgene.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Robin on 2014-10-20.
 */
public class StreamTool {
    /**
     * 从字节流中读取字符串
     *
     * @param inputStream 输入流
     * @return 读取字符串
     * @throws IOException
     */
    public static String readFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String result = "";
        if (inputStream != null) {
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) != -1) {
                baos.write(buff, 0, len);
            }
            inputStream.close();
            result = baos.toString();
            baos.close();
        }
        return result;
    }
}
