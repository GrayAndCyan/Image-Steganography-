package com.mizore.iopic;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.mizore.iopic.Constant.*;

public class Encryptor {

    private List<Integer> content;

    private BufferedImage img;

    public void encrypt(String imgPath, String txtPath) throws IOException {
        // unicode编码 两个字节编码一个字符 这两个字节表示的int值是content数组的每个元素
        content = getContent(txtPath);
        // 文本结尾加内容结束标识 "/exit"
        addSuffix();

        // 获取图片信息
        img = ImageIO.read(new File(imgPath));

        // 将文本编码进图片，原地修改
        encodeTextToImg();

        System.out.println(imgPath);

        // 命名结果图片
        String[] split = imgPath.split("\\.");
        String resImgPath = split[0] + RES_SUFFIX + "." +IMG_FORMAT;

        // 创建并写图片文件
        saveResultImage(resImgPath);

    }

    private void saveResultImage(String resImgPath) throws IOException {
        File resFile = new File(resImgPath);
        System.out.println(resImgPath);
        ImageIO.write(img, "png", resFile);
        System.out.println("ok");
    }

    private void encodeTextToImg() {
        int height = img.getHeight();
        int width = img.getWidth();

        int textLength = content.size();
        // System.out.println(content.get(textLength-1));
        int textIndex = 0;
        int offBit = 0;

        // 遍历每个像素，需要取每个像素的三个颜色通道的最低位存储文本信息
        label:
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = img.getRGB(x, y);

                // 从像素中获取各个通道的值 每个通道8位
                int blue = pixel & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int red = (pixel >> 16) & 0xFF;
                int alpha = (pixel >> 24) & 0xFF;
                // 每个content元素需要16位来存储

                red = encodeToChannel(red, content.get(textIndex), offBit);
                offBit++;
                if (offBit == 16) {
                    offBit = 0;
                    textIndex++;
                    if (textIndex >= textLength) {
                        // 更新最后一个隐藏文本信息的像素值（实际上是结束标识串字母t的最高位） 结束循环
                        int encodedPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                        img.setRGB(x, y, encodedPixel);
                        break label;
                    }
                }

                green = encodeToChannel(green, content.get(textIndex), offBit);
                offBit++;
                if (offBit == 16) {
                    offBit = 0;
                    textIndex++;
                    if (textIndex >= textLength) {
                        // 更新最后一个隐藏文本信息的像素值（实际上是结束标识串字母t的最高位） 结束循环
                        int encodedPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                        img.setRGB(x, y, encodedPixel);
                        break label;
                    }
                }

                blue = encodeToChannel(blue, content.get(textIndex), offBit);
                offBit++;
                if (offBit == 16) {
                    offBit = 0;
                    textIndex++;
                    if (textIndex >= textLength) {
                        // 更新最后一个隐藏文本信息的像素值（实际上是结束标识串字母t的最高位） 结束循环
                        int encodedPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                        img.setRGB(x, y, encodedPixel);
                        break label;
                    }
                }

                // 更新像素值
                int encodedPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                img.setRGB(x, y, encodedPixel);
            }
        }
    }

    /**
     * 将textValue的第offBit位存在到channelValue的最低位，返回新channelValue
     * @param channelValue
     * @param textValue
     * @param offBit
     * @return
     */
    private int encodeToChannel(int channelValue, int textValue, int offBit) {
        // 清零最低位  1111 1110
        channelValue &= 0xFE;

        int dataBit = (textValue >> offBit) & 1;
        return channelValue | dataBit;
    }

    private ArrayList<Integer> getContent(String txtPath) throws IOException {
        Reader reader = new FileReader(txtPath);
        ArrayList<Integer> content = new ArrayList<>();
        int data;
        while ((data = reader.read()) != -1) {
            content.add(data);
        }
        return content;
    }

    private void addSuffix() {
        for (byte b : END_FLAG.getBytes()) {
            content.add((int) b);
        }
    }
}
