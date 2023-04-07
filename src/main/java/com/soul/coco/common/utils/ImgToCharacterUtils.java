package com.soul.coco.common.utils;

import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片转字符工具类
 * @author lh
 * @date 2019-9-7 14:19:27
 */
public class ImgToCharacterUtils {

    /**
     * 导出txt
     * @param request
     * @param response
     * @param fileName
     * @param imgTxt
     */
    public static void exportTxt(HttpServletRequest request, HttpServletResponse response, String fileName, String imgTxt) {
        OutputStream fos = null;
        try {
            fos = response.getOutputStream();

            //获得请求头中的User-Agent
            String agent = request.getHeader("User-Agent");
            //根据不同浏览器进行不同的编码
            String filenameEncoder = "";
            if (agent.contains("MSIE")) {
                // IE浏览器
                filenameEncoder = URLEncoder.encode(fileName, "utf-8");
                filenameEncoder = filenameEncoder.replace("+", " ");
            } else if (agent.contains("Firefox")) {
				// 火狐浏览器
				BASE64Encoder base64Encoder = new BASE64Encoder();
				filenameEncoder = "=?utf-8?B?"
						+ base64Encoder.encode(fileName.getBytes("utf-8")) + "?=";
			} else {
                // 其它浏览器
                filenameEncoder = URLEncoder.encode(fileName, "utf-8");
            }
            //要下载的这个文件的类型-----客户端通过文件的MIME类型去区分类型
            response.setContentType("application/vnd.ms-txt;charset=utf-8");
            //告诉客户端该文件不是直接解析 而是以附件形式打开(下载)----filename="+filename 客户端默认对名字进行解码
            response.setHeader("Content-Disposition", "attachment;filename="+filenameEncoder);

            fos.write(imgTxt.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将图片转为字符
     * @param fileInputStream
     * @param ratio 图片比例（是否需要缩小）1-100
     */
    public static String imgToString(FileInputStream fileInputStream, BigDecimal ratio) {
        final String base = "#8XOHLTI)i=+;:,. ";

        try {
            // 将图片像素大小缩小到100
            Image srcImg = ImageIO.read(fileInputStream); // 取图片
            // 缩放比例只能在1-100
            if (BigDecimal.ZERO.compareTo(ratio) != -1 || new BigDecimal(100).compareTo(ratio) == -1) {
                return null;
            }
            // 获取图片的宽，进行缩放处理
            BigDecimal width = new BigDecimal(srcImg.getWidth(null));
            BigDecimal zoom = ratio.multiply(new BigDecimal(0.01)).setScale(2,BigDecimal.ROUND_DOWN);
            width = width.multiply(zoom).setScale(2,BigDecimal.ROUND_DOWN);
            int height = srcImg.getHeight(null) * width.intValue() / srcImg.getWidth(null); // 按比例，将高度缩减
            Image smallImg = srcImg.getScaledInstance(width.intValue(), height, Image.SCALE_SMOOTH); // 缩小

            // 将图片转为字符
            final BufferedImage image = toBufferedImage(smallImg);
            // 图片字符集合
            List<String> imgStrList = new ArrayList<String>();
            for (int y = 0; y < image.getHeight(); y += 2) {
                StringBuffer stringBuffer = new StringBuffer();
                for (int x = 0; x < image.getWidth(); x++) {
                    final int pixel = image.getRGB(x, y);
                    final int r = (pixel & 0xff0000) >> 16, g = (pixel & 0xff00) >> 8, b = pixel & 0xff;
                    final float gray = 0.299f * r + 0.578f * g + 0.114f * b;
                    final int index = Math.round(gray * (base.length() + 1) / 255);
                    // 将图片转转为的字符保存进StringBuffer对象中
                    stringBuffer.append(index >= base.length() ? " ":String.valueOf(base.charAt(index)));
                }
                // 将一行字符保存进list
                imgStrList.add(stringBuffer.toString());
            }

            StringBuffer imgStr = new StringBuffer();
            imgStrList.forEach(str -> imgStr.append(str + "\n"));

            return imgStr.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将Image对象转成BufferedImage
     * @param image
     * @return
     */
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
        }

        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        Graphics g = bimage.createGraphics();

        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

}
