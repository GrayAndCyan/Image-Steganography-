package com.mizore.iopic;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.mizore.iopic.Constant.*;

public class Main {
    /**
     * 程序入口
     * @param args jpg图片路径 操作（加密e还是解析p） [txt文件路径]
     */
    public static void main(String[] args) throws IOException {
        if (!checkArgs(args)) {
            return;
        }
        if (Objects.equals(args[1], "e")) {
            // 加密
            new Encryptor().encrypt(args[0], args[2]);
        } else {
            // 解析
            new Parser().parse(args[0]);
        }
    }

    private static boolean checkArgs(String[] args) {
        int length = args.length;
        System.out.println(length);
        if (length != 2 && length != 3) {
            System.out.println(USAGE);
            return false;
        }
        if ((length == 2 && !Objects.equals(args[1], "p")) || (length == 3 && !Objects.equals(args[1], "e"))) {
            System.out.println(USAGE);
            return false;
        }
        File f = new File(args[0]);
        if (!f.exists()) {
            System.out.println("Path error!!");
            return false;
        }
        if (length == 3) {
            if (! new File(args[2]).exists()) {
                System.out.println("Path error!!");
                return false;
            }
        }
        return true;
    }
}
