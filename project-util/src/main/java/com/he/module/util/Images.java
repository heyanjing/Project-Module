package com.he.module.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

public class Images {
    public static String       JPG                = "jpg";
    public static final String ERROR_STEAM_CLOSE  = "流关闭错误!";
    public static final String ERROR_IMAGE_GET    = "获取图片错误!";
    public static final String ERROR_IMAGE_DECODE = "图片解码错误!";

    /**
     * 判断是否是图片文件
     */
    public static boolean isImage(String filename) {
        return Files.isImage(filename);
    }

    /**
     * 读取图片 支持多种参数
     */
    private static BufferedImage read(Object obj) {
        BufferedImage bufferedImage = null;
        InputStream input = null;
        try {
            if (obj instanceof String) {
                String path = (String) obj;
                if (Regexs.isUrl(path)) {
                    return ImageIO.read(new URL(path));
                } else {
                    input = new FileInputStream(path);
                    bufferedImage = ImageIO.read(input);
                }
            } else if (obj instanceof URL) {
                bufferedImage = ImageIO.read((URL) obj);
            } else if (obj instanceof InputStream) {
                input = (InputStream) obj;
                bufferedImage = ImageIO.read(input);
            } else if (obj instanceof File) {
                bufferedImage = ImageIO.read((File) obj);
            } else {
                throw Exceptions.newRuntimeException("该方法不支持类型：" + obj.getClass());
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(ERROR_IMAGE_GET + ":" + obj, e);
        } finally {
            Closeables.closeQuietly(input);
        }
        return bufferedImage;
    }

    /**
     * 获取图片 支持path、URL、以及urlString参数
     */
    public static BufferedImage getImage(String pathOrUrlString) {
        return read(pathOrUrlString);
    }

    /**
     * 获取图片
     */
    public static BufferedImage getImage(URL url) {
        return read(url);
    }

    /**
     * 获取图片
     */
    public static BufferedImage getImage(File file) {
        return read(file);
    }

    /**
     * 获取图片
     */
    public static BufferedImage getImage(InputStream is) {
        return read(is);
    }

    /**
     * 获取图片
     */
    public static BufferedImage getImage(byte[] imageByte) {
        BufferedImage img = null;
        InputStream ins = null;
        try {
            ins = new ByteArrayInputStream(imageByte);
            img = getImage(ins);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(ERROR_IMAGE_GET);
        } finally {
            Closeables.closeQuietly(ins);
        }
        return img;
    }

    /**
     * 获取图片
     */
    public static BufferedImage getImageByClassPath(String path) {
        return getImage(Files.getFileFromClassPath(path));
    }

    /**
     * 获取图片
     */
    public static BufferedImage getImage(String path, Class<?> clazz) {
        return getImage(Files.getFile(path, clazz));
    }

    /**
     * 获取图片
     */
    public static Map<String, Object> getImageResultMapByUrlString(String urlString) {
        Map<String, Object> result = Maps.newHashMap();
        InputStream ins = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            int resp = urlConnection.getResponseCode();
            if (resp == 200) {
                result.put("success", true);
                ins = url.openStream();
                result.put("data", IOUtils.toByteArray(ins));
            } else if (resp == 403) {
                throw new MalformedURLException("非法URL,返回值403");
            } else if (resp == 400) {
                throw new FileNotFoundException("未找到图片,返回值400");
            } else {
                throw Exceptions.newRuntimeException("图片获取错误,状态值:" + resp);
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(ERROR_IMAGE_GET, e);
        } finally {
            Closeables.closeQuietly(ins);
        }
        return result;
    }

    /**
     * 根据路径获取图片并转换为byte[]
     */
    public static byte[] getByte(String path) {
        return getByte(getImage(path), JPG);
    }

    /**
     * 根据路径获取图片并转换为byte[]
     */
    public static byte[] getByte(String path, String format) {
        return getByte(getImage(path), format);
    }

    /**
     * 根据BufferedImage对象获取图片并转换为byte[]
     */
    public static byte[] getByte(BufferedImage image) {
        return getByte(image, JPG);
    }

    /**
     * 根据BufferedImage对象获取图片并转换为byte[] 图片的格式默认：jpg
     */
    public static byte[] getByte(BufferedImage image, String format) {
        byte[] imageByte = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();// no need to
                                                                // close
        try {
            ImageIO.write(image, format, bos);
            imageByte = bos.toByteArray();
        } catch (IOException e) {
            throw Exceptions.newRuntimeException(ERROR_IMAGE_GET, e);
        }
        return imageByte;
    }

    /**
     * 根据路径获取图片并Base64编码
     */
    public static String encodeBase64(String imagePath) {
        return encodeBase64(getImage(imagePath));
    }

    /**
     * Base64编码BufferedImage对象
     */
    public static String encodeBase64(BufferedImage bufferedImage) {
        return Base64.encodeBase64String(getByte(bufferedImage));
        // return new BASE64Encoder().encode(getByte(bufferedImage));
    }

    /**
     * Base64解码imageBase64String并转换为BufferedImage对象
     */
    public static BufferedImage decodeBase64(String imageBase64String) {
        BufferedImage image = null;
        // BASE64Decoder decoder = new BASE64Decoder();
        try {
            // byte[] imageByte = decoder.decodeBuffer(imageBase64String);
            byte[] imageByte = Base64.decodeBase64(imageBase64String);
            image = getImage(imageByte);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(ERROR_IMAGE_DECODE, e);
        }
        return image;
    }

    /**
     * Base64解码imageBase64String并转换为byte[] 图片格式默认为jpg
     */
    public static byte[] getByteBase64(String imageBase64String) {
        return getByte(decodeBase64(imageBase64String), JPG);
    }

    /**
     * Base64解码imageBase64String并转换为byte[]
     */
    public static byte[] getByteBase64(String imageBase64String, String format) {
        return getByte(decodeBase64(imageBase64String), format);
    }

    /**
     * 保存BufferedImage图片 默认格式为jpg
     */
    public static void saveImage(String path, BufferedImage image) {
        saveImage(path, image, JPG);
    }

    /**
     * 保存BufferedImage图片
     */
    public static void saveImage(String path, BufferedImage image, String format) {
        try {
            ImageIO.write(image, format, new File(path));
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 保存byte[]图片 默认格式为jpg
     */
    public static void saveImage(String path, byte[] imageByte) {
        saveImage(path, imageByte, JPG);
    }

    /**
     * 保存byte[]图片
     */
    public static void saveImage(String path, byte[] imageByte, String format) {
        saveImage(path, getImage(imageByte), format);
    }

    /**
     * 保存imageBase64String为图片 默认格式为jpg
     */
    public static void saveImageBase64(String path, String imageBase64String) {
        saveImage(path, decodeBase64(imageBase64String), JPG);
    }

    /**
     * 保存imageBase64String为图片
     */
    public static void saveImageBase64(String path, String imageBase64String, String format) {
        saveImage(path, decodeBase64(imageBase64String), format);
    }

    /**
     * 截取
     */
    public static BufferedImage crop(BufferedImage img, int x, int y, int w, int h) {
        assert (img != null && w > 0 && h > 0);
        int width = img.getWidth();
        int height = img.getHeight();
        w = w > width ? width : w;
        h = h > height ? height : h;
        return img.getSubimage(x, y, w, h);
    }

    /**
     * 水平翻转
     */
    public static BufferedImage flipHorizontal(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(w, h, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);
        g.dispose();
        return dimg;
    }

    /**
     * 垂直翻转
     */
    public static BufferedImage flipVertical(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(w, h, img.getColorModel().getTransparency());
        Graphics2D g = dimg.createGraphics();
        g.drawImage(img, 0, 0, w, h, 0, h, w, 0, null);
        g.dispose();
        return dimg;
    }

    /**
     * 调整大小
     */
    public static BufferedImage resize(BufferedImage img, int newW, int newH, boolean keepRatio) {
        int w = img.getWidth();
        int h = img.getHeight();
        if (keepRatio) {
            if (w > h) {
                newH = (int) (h * ((double) newW / (double) w));
            } else {
                newW = (int) (w * ((double) newH / (double) h));
            }
        }
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }

    /**
     * 旋转
     */
    public static BufferedImage rotate(BufferedImage img, int angle) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(w, h, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.rotate(Math.toRadians(angle), w / 2, h / 2);
        g.drawImage(img, null, 0, 0);
        return dimg;
    }

    /**
     * 图片压缩
     * 
     * @param inputFile
     *            压缩图片全路径
     * @param outputFile
     *            压缩后的全路径
     * @param width
     *            压缩后的宽度
     * @param height
     *            压缩后的高度
     * @param proportion
     *            是否等比例压缩
     * @return
     */
    public static Map<String, Object> compress(String inputFile, String outputFile, int width, int height, boolean proportion) {
        Map<String, Object> result = Maps.newHashMap();
        // File file = new File(inputFile);
        // result.put("success", false);
        // try {
        // if (file.exists()) {
        // Image img = ImageIO.read(file);
        // int newWidth;
        // int newHeight;
        // // 判断是否是等比缩放
        // if (proportion == true) {
        // // 为等比缩放计算输出的图片宽度及高度
        // double rate1 = ((double) img.getWidth(null)) / (double) width + 0.1;
        // double rate2 = ((double) img.getHeight(null)) / (double) height + 0.1;
        // // 根据缩放比率大的进行缩放控制
        // double rate = rate1 > rate2 ? rate1 : rate2;
        // newWidth = (int) ((img.getWidth(null)) / rate);
        // newHeight = (int) ((img.getHeight(null)) / rate);
        // } else {
        // newWidth = width; // 输出的图片宽度
        // newHeight = height; // 输出的图片高度
        // }
        // BufferedImage tag = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        // /*
        // * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
        // */
        // tag.getGraphics().drawImage(img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
        // FileOutputStream out = new FileOutputStream(outputFile);
        //
        // JPEGImageWriter imageWriter = (JPEGImageWriter) ImageIO
        // .getImageWritersBySuffix("jpg").next();
        // ImageOutputStream ios = ImageIO.createImageOutputStream(fos);
        // imageWriter.setOutput(ios);
        // IIOMetadata imageMetaData = imageWriter.getDefaultImageMetadata(
        // new ImageTypeSpecifier(image_to_save), null);
        //
        //
        //
        //
        //
        // // JPEGImageEncoder可适用于其他图片类型的转换
        // JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        // encoder.encode(tag);
        // out.close();
        // result.put("msg", "压缩完成!");
        // result.put("success", true);
        // } else {
        // result.put("msg", "文件不存在!");
        // }
        // } catch (IOException e) {
        // e.printStackTrace();
        // result.put("msg", "图片转换出错!");
        // }
        return result;
    }

}
