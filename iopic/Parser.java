package com.mizore.iopic;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static com.mizore.iopic.Constant.END_FLAG;
import static com.mizore.iopic.Constant.TEXT_FORMAT;

public class Parser {

    private BufferedImage img;
    public void parse(String imgPath) throws IOException {

        // 获取图片信息
        this.img = ImageIO.read(new File(imgPath));

        // 获取加密其中的文本
        List<Integer> content = getContentInImg();

        // 转string
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < content.size(); i++) {
            char c = (char) (content.get(i).intValue());
            if (c == '/') {
                if ((char)(content.get(i+1).intValue()) == 'e'
                && (char)(content.get(i+2).intValue()) == 'x'
                && (char)(content.get(i+3).intValue()) == 'i'
                && (char)(content.get(i+4).intValue()) == 't') {
                    break;
                }
            }
            res.append((char) (content.get(i).intValue()));

        }

        // 得到字符串的文本
        String contentString = res.toString();
        System.out.println(contentString);

        // 结果文件命名
        String[] split = imgPath.split("\\.");
        String resTextPath = split[0] + "." + TEXT_FORMAT;

        // 创建并写文件
        saveResultText(contentString, resTextPath);
    }

    private static void saveResultText(String contentString, String resTextPath) throws IOException {
        File resFile = new File(resTextPath);
        Writer writer = new FileWriter(resFile);
        writer.write(contentString);
        System.out.println("ok");
        writer.close();
    }

    private List<Integer> getContentInImg() {

        List<Integer> content = new ArrayList<>();

        int height = img.getHeight();
        int width = img.getWidth();
        int[] pixels = img.getRGB(0, 0, width, height, null, 0, width);

        int pixelSize = pixels.length;

        int pixelIndex = 0;
        int data = 0;
        int offBit = 0;
        while (pixelIndex < pixelSize) {
            int pixel = pixels[pixelIndex];

            // 从像素中获取各个通道的值 每个通道8位
            int blue = pixel & 0xFF;
            int green = (pixel >> 8) & 0xFF;
            int red = (pixel >> 16) & 0xFF;

            data |= ((red & 1) << offBit);
            offBit++;
            if (offBit == 16) {
                offBit = 0;
                content.add(data);
                data = 0;
            }

            data |= ((green & 1) << offBit);
            offBit++;
            if (offBit == 16) {
                offBit = 0;
                content.add(data);
                data = 0;
            }

            data |= ((blue & 1) << offBit);
            offBit++;
            if (offBit == 16) {
                offBit = 0;
                content.add(data);
                data = 0;
            }
            pixelIndex++;
        }
    return content;
    }
}
