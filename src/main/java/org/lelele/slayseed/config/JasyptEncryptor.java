package org.lelele.slayseed.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.util.Scanner;

/**
 * 独立的Jasypt加密工具类（不依赖Spring Boot，可单独运行）
 * 
 * 使用方式：
 * 1. 设置环境变量 JASYPT_ENCRYPTOR_KEY
 * 2. 直接运行此类的main方法
 * 3. 输入要加密的明文
 */
public class JasyptEncryptor {

    /**
     * 加密算法（固定）
     */
    private static final String ALGORITHM = "PBEWithMD5AndDES";

    public static void main(String[] args) {
        // 从系统环境变量读取Jasypt加密密钥
        String jasyptSecretKey = System.getenv("JASYPT_ENCRYPTOR_KEY");

        // 检查是否设置了加密密钥
        if (jasyptSecretKey == null || jasyptSecretKey.isEmpty()) {
            System.out.println("============================================");
            System.out.println("[错误] 未设置加密密钥!");
            System.out.println("请设置系统环境变量: JASYPT_ENCRYPTOR_KEY");
            System.out.println("============================================");
            return;
        }

        // 初始化加密器
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(jasyptSecretKey);
        encryptor.setAlgorithm(ALGORITHM);

        System.out.println("============================================");
        System.out.println("Jasypt加密工具已就绪");
        System.out.println("加密算法: " + ALGORITHM);
        System.out.println("加密密钥: " + jasyptSecretKey);
        System.out.println("============================================");

        // 优先使用命令行参数（如果传入了明文）
        if (args.length > 0 && !args[0].isEmpty()) {
            System.out.println("\n正在加密命令行参数中的明文...\n");
            for (String plainText : args) {
                String encrypted = encryptor.encrypt(plainText);
                System.out.println("原文: " + plainText);
                System.out.println("配置: spring.datasource.password=ENC(" + encrypted + ")");
                System.out.println("--------------------------------------------");
            }
        } else {
            // 交互式输入明文
            System.out.println("\n请输入要加密的明文密码（输入exit退出）:\n");
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("明文密码: ");
                String plainText = scanner.nextLine();

                if (plainText == null || plainText.isEmpty()) {
                    System.out.println("输入不能为空，请重新输入!\n");
                    continue;
                }

                if ("exit".equalsIgnoreCase(plainText)) {
                    System.out.println("\n已退出加密工具。");
                    break;
                }

                // 加密
                String encrypted = encryptor.encrypt(plainText);
                System.out.println("\n--------------------------------------------");
                System.out.println("原文: " + plainText);
                System.out.println("密文: ENC(" + encrypted + ")");
                System.out.println("--------------------------------------------\n");
                System.out.println("将上述密文粘贴到 application.properties 中:\n");
                System.out.println("  spring.datasource.password=ENC(" + encrypted + ")");
                System.out.println("  spring.data.redis.password=ENC(" + encrypted + ")");
                System.out.println("  jwt.secret=ENC(" + encrypted + ")");
                System.out.println("--------------------------------------------\n");
            }

            scanner.close();
        }
    }
}