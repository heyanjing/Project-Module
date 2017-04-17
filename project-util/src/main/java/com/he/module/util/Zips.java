package com.he.module.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Zips {
    public static void en() throws Exception {
        // 输入流
        InputStream is = new FileInputStream("C:\\详细设计.doc");
        // 字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 缓冲区 缓冲区越大，速度越快
        byte[] bu = new byte[1024 * 100];
        int count = 0;
        // 将流写到字节输出流中
        while ((count = is.read(bu)) > 0) {
            baos.write(bu, 0, count);
        }
        // 将字节输出流转为字节数组
        byte[] text = baos.toByteArray();
        // 如果要使用同一个流对象，一定要重设清空原有数据
        baos.reset();
        // 压缩器
        Deflater compresser = new Deflater();
        // 重置 deflater 以处理新的输入数据集
        compresser.reset();
        // 为压缩设置输入数据
        compresser.setInput(text);
        // 调用时，指示压缩应当以输入缓冲区的当前内容结尾
        compresser.finish();
        try {
            byte[] buf = new byte[1024 * 100];
            // 如果已到达压缩数据输出流的结尾，则返回 true
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                baos.write(buf, 0, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 关闭解压缩器并放弃所有未处理的输入
        compresser.end();
        // 输出流 这里大家应该知道，输出任意名称，带不带后缀都是可以的，因为在Java中只有文件和文件夹之分
        FileOutputStream fos = new FileOutputStream("C:\\详细设计");
        // 将字节输出流写入到输出流中
        baos.writeTo(fos);
        fos.close();
        baos.close();
        is.close();
        System.out.println("压缩完毕");
    }

    public static void de() throws Exception {
        // 输入流
        InputStream is = new FileInputStream("C:\\详细设计");
        // 字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 缓冲区 缓冲区越大，速度越快
        byte[] bu = new byte[1024 * 100];
        int count = 0;
        // 将流写到字节输出流中
        while ((count = is.read(bu)) > 0) {
            baos.write(bu, 0, count);
        }
        // 将字节输出流转为字节数组
        byte[] text = baos.toByteArray();
        // 如果要使用同一个流对象，一定要重设清空原有数据
        baos.reset();
        // 解压器
        Inflater decompresser = new Inflater();
        // 重置 inflater 以处理新的输入数据集
        decompresser.reset();
        // 为解压缩设置输入数据
        decompresser.setInput(text);
        // 用于接受压缩后的数据
        try {
            // 缓冲
            byte[] buf = new byte[1024 * 100];
            // 如果已到达压缩数据流的结尾，则返回 true
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                baos.write(buf, 0, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 关闭解压缩器并放弃所有未处理的输入
        decompresser.end();
        // 输出流
        FileOutputStream fos = new FileOutputStream("C:\\详细设计.doc");
        // 将字节输出流写入到输出流中
        baos.writeTo(fos);
        fos.close();
        baos.close();
        is.close();
        System.out.println("解压完毕");
    }
}
