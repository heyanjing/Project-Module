package com.he.module.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

public class Thumbs {

    /**
     * 指定大小进行缩放 ,keep为true时输出指定的宽高
     */
    public static void fixedWidthAndHeight(String sourcePath, String destPath, int width, int height, boolean keep) {
        try {
            Thumbnails.of(sourcePath).size(width, height).keepAspectRatio(keep).toFile(destPath);
        } catch (IOException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 按照比例进行缩放
     */
    public static void fixedScale(String sourcePath, String destPath, double scale) {
        try {
            Thumbnails.of(sourcePath).scale(scale).toFile(destPath);
        } catch (IOException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 按角度进行旋转
     */
    public static void fixedAngle(String sourcePath, String destPath, double angle) {
        try {
            Thumbnails.of(sourcePath).rotate(angle).toFile(destPath);
        } catch (IOException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 添加水印
     */
    public static void addWatermark(String sourcePath, String destPath, Positions position, BufferedImage image, float opacity) {
        try {
            Thumbnails.of(sourcePath).watermark(position, image, opacity).toFile(destPath);
        } catch (IOException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }
}
