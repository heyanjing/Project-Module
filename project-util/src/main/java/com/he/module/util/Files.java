package com.he.module.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * // com.google.common.io.Files
 */
public class Files {

    public static final String  SEPERATOR       = "/";
    private static final String CHARSET_DEFAULT = "GBK";
    public static String[]      IMAGES          = { "bmp", "jpg", "jpeg", "gif", "png", "tiff" };

    /**
     * 根据文件路径或者URL获取文件
     */
    public static File getFile(String path) {
        return new File(path);
    }

    /**
     * 根据指定类获取文件
     */
    public static File getFile(String path, Class<?> clazz) {
        return new File(clazz.getResource(path).getFile());
    }

    /**
     * 根据指定类获取文件
     */
    public static File getFileByClass(String path, Class<?> clazz) {
        return getFile(path, clazz);
    }

    /**
     * 从ClassPath路径获取文件
     */
    public static File getFileFromClassPath(String path) {
        return getFile(Classes.getClassPath() + path);
    }

    /**
     * 获得资源URL
     */
    public static URL getURL(String resource) {
        return Classes.getURL(resource);
    }

    /**
     * 获得资源URL
     */
    public static URL getURL(String resource, Class<?> clazz) {
        return Classes.getURL(resource, clazz);
    }

    /**
     * 根据charset编码读取文件
     */
    public static String read(File file, Charset charset) {
        String result = null;
        try {
            result = FileUtils.readFileToString(file, charset.toString());
        } catch (IOException e) {
            throw Exceptions.newRuntimeException(e);
        }
        return result;
    }

    /**
     * 读取文件charset编码默认UTF-8
     */
    public static String read(File file) {
        return read(file, Charsets.UTF_8);
    }

    /**
     * 根据path或url和charset编码读取文件
     */
    public static String read(String path, Charset charset) {
        String result = null;
        try {
            if (Strings.isUrl(path)) {
                result = read(new URL(path));
            } else {
                result = read(getFile(path), charset);
            }
        } catch (IOException e) {
            throw Exceptions.newRuntimeException(e);
        }
        return result;
    }

    /**
     * 根据path读取文件,编码默认UTF-8
     */
    public static String read(String path) {
        return read(path, Charsets.UTF_8);
    }

    /**
     * 根据url和charset编码读取文件
     */
    public static String read(URL url, Charset charset) {
        String result = null;
        InputStream input = null;
        InputStreamReader reader = null;
        try {
            input = url.openStream();
            reader = new InputStreamReader(input, charset);
            result = IOUtils.toString(reader);
        } catch (IOException e) {
            throw Exceptions.newRuntimeException(e);
        } finally {
            Closeables.closeQuietly(input);
            Closeables.closeQuietly(reader);
        }
        return result;
    }

    /**
     * 根据url读取文件,编码默认UTF-8
     */
    public static String read(URL url) {
        return read(url, Charsets.UTF_8);
    }

    /**
     * 根据Class读取文件,默认编码UTF-8
     */
    public static String read(String path, Class<?> clazz) {
        return read(path, clazz, Charsets.UTF_8);
    }

    /**
     * 根据Class读取文件
     */
    public static String read(String path, Class<?> clazz, Charset charset) {
        return read(Files.getFile(path, clazz), charset);
    }

    /**
     * 根据Classpath读取文件
     */
    public static String readFromClassPath(String path, Charset charset) {
        return read(Files.getFileFromClassPath(path), charset);
    }

    /**
     * 根据Classpath读取文件
     */
    public static String readFromClassPath(String path) {
        return readFromClassPath(path, Charsets.UTF_8);
    }

    /**
     * 分行读取
     */
    public static List<String> readLines(File file, Charset charset) {
        List<String> result = null;
        try {
            result = FileUtils.readLines(file, charset);
        } catch (IOException e) {
            throw Exceptions.newRuntimeException(e);
        }
        return result;
    }

    /**
     * 分行读取,编码默认：UTF-8
     */
    public static List<String> readLines(File file) {
        return readLines(file, Charsets.UTF_8);
    }

    /**
     * 分行读取
     */
    public static List<String> readLines(String path, Charset charset) {
        List<String> result = null;
        try {
            if (Strings.isUrl(path)) {
                result = readLines(new URL(path), charset);
            } else {
                result = readLines(Files.getFile(path), charset);
            }
        } catch (IOException e) {
            throw Exceptions.newRuntimeException(e);
        }
        return result;
    }

    /**
     * 分行读取,编码默认：UTF-8
     */
    public static List<String> readLines(String path) {
        return readLines(path, Charsets.UTF_8);
    }

    /**
     * 根据URL分行读取
     */
    public static List<String> readLines(URL url, Charset charset) {
        List<String> result = null;
        InputStream input = null;
        try {
            input = url.openStream();
            result = IOUtils.readLines(input, charset);
        } catch (IOException e) {
            throw Exceptions.newRuntimeException(e);
        } finally {
            Closeables.closeQuietly(input);
        }
        return result;
    }

    /**
     * 根据URL分行读取:编码默认：UTF-8
     */
    public static List<String> readLines(URL url) {
        return readLines(url, Charsets.UTF_8);
    }

    /**
     * 根据Class分行读取
     */
    public static List<String> readLines(String path, Class<?> clazz, Charset charset) {
        return readLines(getFile(path, clazz), charset);
    }

    /**
     * 根据Class分行读取,编码默认：UTF-8
     */
    public static List<String> readLines(String path, Class<?> clazz) {
        return readLines(getFile(path, clazz), Charsets.UTF_8);
    }

    /**
     * 根据ClassPath分行读取
     */
    public static List<String> readLinesFromClassPath(String path, Charset charset) {
        return readLines(getFileFromClassPath(path), charset);
    }

    /**
     * 根据ClassPath分行读取,编码默认：UTF-8
     */
    public static List<String> readLinesFromClassPath(String path) {
        return readLinesFromClassPath(path, Charsets.UTF_8);
    }

    public static void downloadFromUrl(String fileUrl, String dir) {
        try {
            URL url = new URL(fileUrl);
            String fileName = getNameFromUrl(fileUrl);
            File f = new File(dir + fileName);
            FileUtils.copyURLToFile(url, f);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 下载网络图片到指定的目录
     */
    public static boolean downloadFromUrl(String fileUrl, String fileName, String dir) {
        InputStream input = null;
        OutputStream output = null;
        try {
            // 如果路径中含有中文,则进行转码操作
            Pattern pa = Pattern.compile("[\\u4e00-\\u9fa5]", Pattern.DOTALL);
            Matcher ma = pa.matcher(fileUrl);
            String t = null;
            while (ma.find()) {
                t = ma.group();
                fileUrl = fileUrl.replace(t, URLEncoder.encode(t, "UTF-8"));
            }
            // 构造URL
            URL url = new URL(fileUrl);
            // 打开连接
            URLConnection con = url.openConnection();
            // 设置请求超时为5s
            con.setConnectTimeout(5 * 1000);
            // 输入流
            input = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流
            File sf = new File(dir);
            if (!sf.exists()) {
                sf.mkdirs();
            }
            output = new FileOutputStream(dir + fileName);
            // 开始读取
            while ((len = input.read(bs)) != -1) {
                output.write(bs, 0, len);
            }
            return true;
        } catch (Exception e) {
            Logs.error(Files.class, "图片保存错误", e);
            return false;
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                Closeables.closeQuietly(input);
            } catch (Exception e) {
                Logs.error(Files.class, "流关闭错误", e);
                return false;
            }
        }
    }

    public static String getNameFromUrl(String url) {
        String name = null;
        int index = url.lastIndexOf("/");
        if (index > 0) {
            name = url.substring(index + 1);
            if (name.trim().length() > 0) {
                return name;
            }
        }
        return name;
    }

    /**
     * 复制文件
     */
    public static void copy(File srcFile, File destFile) {
        try {
            FileUtils.copyFile(srcFile, destFile);
        } catch (IOException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 复制文件
     */
    public static void copy(String source, String destination) {
        if (Strings.isUrl(source)) {
            try {
                copy(new URL(source), destination);
            } catch (MalformedURLException e) {
                throw Exceptions.newRuntimeException(e);
            }
        } else {
            copy(getFile(source), getFile(destination));
        }
    }

    /**
     * 复制文件
     */
    public static void copy(URL source, File destination, int connectionTimeout, int readTimeout) {
        try {
            FileUtils.copyURLToFile(source, destination, connectionTimeout, readTimeout);
        } catch (IOException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 复制文件
     */
    public static void copy(URL source, File destination) {
        copy(source, destination, 10000, 10000);
    }

    /**
     * 复制文件
     */
    public static void copy(String source, File destination) {
        try {
            copy(new URL(source), destination);
        } catch (MalformedURLException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 复制文件
     */
    public static void copy(URL source, String destination) {
        copy(source, getFile(destination));
    }

    /**
     * 复制文件
     */
    public static void copyFromClassPath(String srcFile, String destFile) {
        copy(getFileFromClassPath(srcFile), getFileFromClassPath(destFile));
    }

    /**
     * 复制文件
     */
    public static boolean delete(File file) {
        return file.delete();
    }

    /**
     * 删除文件
     */
    public static boolean delete(String path) {
        return delete(getFile(path));
    }

    /**
     * 强制删除
     */
    public static void deleteForce(String path) {
        deleteForce(getFile(path));
    }

    /**
     * 强制删除
     */
    public static void deleteForce(File file) {
        try {
            if (file.exists()) {
                FileUtils.forceDelete(file);
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 当文件存在的时候，删除该文件
     */
    public static void deleteOnExit(String path) {
        deleteOnExit(path);
    }

    /**
     * 删除文件
     */
    public static void deleteOnExit(File file) {
        file.deleteOnExit();
    }

    /**
     * 强制删除
     */
    public static void deleteForceOnExit(File file) {
        try {
            FileUtils.forceDeleteOnExit(file);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 强制删除
     */
    public static void deleteForceOnExit(String path) {
        deleteForceOnExit(getFile(path));
    }

    /**
     * 创建路径
     */
    public static boolean mkdir(String path) {
        return mkdir(getFile(path));
    }

    /**
     * 创建路径
     */
    public static boolean mkdir(File file) {
        return file.mkdir();
    }

    /**
     * 强制创建路径
     */
    public static void mkdirsForce(String path) {
        mkdirsForce(getFile(path));
    }

    /**
     * 强制创建路径
     */
    public static void mkdirsForce(File file) {
        try {
            FileUtils.forceMkdir(file);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 强制创建文件的路径
     */
    public static void createDir(String path) {
        mkdirsForce(getDirFile(path));
    }

    /**
     * 强制创建文件的路径
     */
    public static void createDir(File file) {
        mkdirsForce(getDirFile(file));
    }

    /**
     * 移动
     */
    public static void move(File dest, File source) {
        try {
            com.google.common.io.Files.move(source, dest);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 移动
     */
    public static void move(String dest, String source) {
        move(getFile(dest), getFile(source));
    }

    /**
     * 写文件
     */
    public static void write(File file, String str, Charset charset) {
        try {
            com.google.common.io.Files.write(str, file, charset);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 写文件
     */
    public static void write(String file, String str, Charset charset) {
        write(getFile(file), str, charset);
    }

    /**
     * 写文件
     */
    public static void write(String file, String str) {
        write(getFile(file), str, Charsets.UTF_8);
    }

    /**
     * 写文件
     */
    public static void write(File file, String str) {
        write(file, str, Charsets.UTF_8);
    }

    /**
     * 创建并写文件
     */
    public static void createFile(String file) {
        createFile(file, Strings.EMPTY, Charsets.UTF_8);
    }

    /**
     * 创建并写文件
     */
    public static void createFile(String file, String str) {
        createFile(file, str, Charsets.UTF_8);
    }

    /**
     * 创建并写文件
     */
    public static void createFile(String file, Charset charsets) {
        createFile(file, Strings.EMPTY, charsets);
    }

    /**
     * 创建并写文件
     */
    public static void createFile(String file, String str, Charset charset) {
        createDir(file);
        write(file, str, charset);
    }

    /**
     * 创建并写文件
     */
    public static void createFile(File file) {
        createFile(file, Strings.EMPTY, Charsets.UTF_8);
    }

    /**
     * 创建并写文件
     */
    public static void createFile(File file, String str) {
        createFile(file, str, Charsets.UTF_8);
    }

    /**
     * 创建并写文件
     */
    public static void createFile(File file, Charset charsets) {
        createFile(file, Strings.EMPTY, charsets);
    }

    /**
     * 创建并写文件
     */
    public static void createFile(File file, String str, Charset charsets) {
        createDir(file);
        write(file, str, charsets);
    }

    /**
     * 创建并追加写文件
     */
    public static void append(String file, String str) {
        append(file, str, Charsets.UTF_8);
    }

    /**
     * 创建并追加写文件
     */
    public static void append(String file, String str, Charset charset) {
        append(getFile(file), str, charset);
    }

    /**
     * 创建并追加写文件
     */
    public static void append(File file, String str) {
        append(file, str, Charsets.UTF_8);
    }

    /**
     * 创建并追加写文件
     */
    public static void append(File file, String str, Charset charset) {
        try {
            if (!file.exists()) {
                createFile(file, str, charset);
            } else {
                com.google.common.io.Files.append(str, file, Charsets.UTF_8);
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 删除文件夹
     */
    public static void deleteDirs(String dir) {
        deleteDirs(getFile(dir));
    }

    /**
     * 删除文件夹
     */
    public static void deleteDirs(File dir) {
        try {
            FileUtils.deleteDirectory(dir);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 判断是否相等
     */
    public static boolean isEquals(File f1, File f2) throws IOException {
        return com.google.common.io.Files.equal(f1, f2);
    }

    /**
     * 判断是否相等
     */
    public static boolean isEquals(String f1, String f2) throws IOException {
        return com.google.common.io.Files.equal(getFile(f1), getFile(f2));
    }

    /**
     * 判断是否存在
     */
    public static boolean isExists(String path) {
        return getFile(path).exists();
    }

    /**
     * 判断是否存在
     */
    public static boolean isExists(File file) {
        return file.exists();
    }

    /**
     * 判断是否存在
     */
    public static boolean isExists(String path, Class<?> clazz) {
        return getFile(path, clazz).exists();
    }

    /**
     * 判断是否存在
     */
    public static boolean isExistsFromClassPath(String path) {
        return getFileFromClassPath(path).exists();
    }

    /**
     * 获取文件后缀名
     */
    public static String getExtension(File file) {
        return getExtension(file.getName());
    }

    /**
     * 获取文件后缀名
     */
    public static String getExtension(String file) {
        return FilenameUtils.getExtension(file);
    }

    /**
     * 获取全路径
     * 
     * <pre>
     * C:\a\b\c.txt --> C:\a\b\
     * ~/a/b/c.txt  --> ~/a/b/
     * a.txt        --> ""
     * a/b/c        --> a/b/
     * a/b/c/       --> a/b/c/
     * C:           --> C:
     * C:\          --> C:\
     * ~            --> ~/
     * ~/           --> ~/
     * ~user        --> ~user/
     * ~user/       --> ~user/
     * </pre>
     */
    public static String getFullPath(String file) {
        return FilenameUtils.getFullPath(file);
    }

    /**
     * 获取全路径
     * 
     * <pre>
     * C:\a\b\c.txt --> C:\a\b\
     * ~/a/b/c.txt  --> ~/a/b/
     * a.txt        --> ""
     * a/b/c        --> a/b/
     * a/b/c/       --> a/b/c/
     * C:           --> C:
     * C:\          --> C:\
     * ~            --> ~/
     * ~/           --> ~/
     * ~user        --> ~user/
     * ~user/       --> ~user/
     * </pre>
     */
    public static String getFullPath(File file) {
        return getFullPath(file.getAbsolutePath());
    }

    /**
     * 获取全路径
     * 
     * <pre>
     * C:\a\b\c.txt --> C:\a\b\
     * ~/a/b/c.txt  --> ~/a/b/
     * a.txt        --> ""
     * a/b/c        --> a/b/
     * a/b/c/       --> a/b/c/
     * C:           --> C:
     * C:\          --> C:\
     * ~            --> ~/
     * ~/           --> ~/
     * ~user        --> ~user/
     * ~user/       --> ~user/
     * </pre>
     */
    public static String getDir(String file) {
        return getFullPath(file);
    }

    /**
     * 获取全路径
     * 
     * <pre>
     * C:\a\b\c.txt --> C:\a\b\
     * ~/a/b/c.txt  --> ~/a/b/
     * a.txt        --> ""
     * a/b/c        --> a/b/
     * a/b/c/       --> a/b/c/
     * C:           --> C:
     * C:\          --> C:\
     * ~            --> ~/
     * ~/           --> ~/
     * ~user        --> ~user/
     * ~user/       --> ~user/
     * </pre>
     */
    public static String getDir(File file) {
        return getFullPath(file);
    }

    /**
     * 获取文件的文件路径File
     */
    public static File getDirFile(String path) {
        return getFile(getDir(path));
    }

    /**
     * 获取文件的文件路径File
     */
    public static File getDirFile(File file) {
        return getFile(getDir(file));
    }

    /**
     * 获取全路径,无最后的分隔符
     * 
     * <pre>
     * C:\a\b\c.txt --> C:\a\b
     * ~/a/b/c.txt  --> ~/a/b
     * a.txt        --> ""
     * a/b/c        --> a/b
     * a/b/c/       --> a/b/c
     * C:           --> C:
     * C:\          --> C:\
     * ~            --> ~
     * ~/           --> ~
     * ~user        --> ~user
     * ~user/       --> ~user
     * </pre>
     */
    public static String getFullPathNoEndSeparator(String file) {
        return FilenameUtils.getFullPathNoEndSeparator(file);
    }

    /**
     * 获取全路径,无最后的分隔符
     * 
     * <pre>
     * C:\a\b\c.txt --> C:\a\b
     * ~/a/b/c.txt  --> ~/a/b
     * a.txt        --> ""
     * a/b/c        --> a/b
     * a/b/c/       --> a/b/c
     * C:           --> C:
     * C:\          --> C:\
     * ~            --> ~
     * ~/           --> ~
     * ~user        --> ~user
     * ~user/       --> ~user
     * </pre>
     */
    public static String getFullPathNoEndSeparator(File file) {
        return getFullPathNoEndSeparator(file.getAbsolutePath());
    }

    /**
     * File转换为ByteArray
     */
    public static byte[] toByteArray(String path) {
        return toByteArray(getFile(path));
    }

    /**
     * File转换为ByteArray
     */
    public static byte[] toByteArray(File file) {
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * File转换为InputStream
     */
    public static InputStream toInputStream(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    /**
     * InputStream转换为File
     */
    public static File toFile(File file, InputStream input) {
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = input.read(buffer, 0, 8192)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                Closeables.closeQuietly(input);
            } catch (IOException ioe) {
                // ignore
            }
        }
        return file;
    }

    /**
     * 递归打印目录
     */
    public static void printDirs(File file) {
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                Logs.println(childFile.getName());
                printDirs(childFile);
            }
        }
    }

    /**
     * 递归获取目录
     */
    public static Map<String, Object> getDirs(File file) {
        Map<String, Object> result = Maps.newHashMap();
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            result.put("name", childFile.getName());
            if (childFile.isDirectory()) {
                result.put("data", getDirs(childFile));
            }
        }
        return result;
    }

    /**
     * 获取文件编码 es-common
     */
    public static String getCharset(String fileName) {
        return getCharset(new File(fileName));
    }

    /**
     * 只能判断常见的GBK，UTF-16LE，UTF-16BE,UTF-8，其分别对应window下的记事本可另存为的编码类型ANSI,Unicode,
     * Unicode big endian,UTF-8
     */
    public static String getCharset(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return getCharset(new BufferedInputStream(is));
        } catch (FileNotFoundException e) {
            return CHARSET_DEFAULT;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public static String getCharset(final BufferedInputStream is) {
        String charset = CHARSET_DEFAULT;
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            is.mark(0);
            int read = is.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            is.reset();
            if (!checked) {
                int loc = 0;

                while ((read = is.read()) != -1 && loc < 100) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = is.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                            // (0x80
                            // - 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
                        read = is.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = is.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            is.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return charset;
    }

    /**
     * 判断是否是图片文件
     */
    public static boolean isImage(String filename) {
        if (filename == null || filename.trim().length() == 0) {
            return false;
        }
        return ArrayUtils.contains(IMAGES, Files.getExtension(filename).toLowerCase());
    }
}
