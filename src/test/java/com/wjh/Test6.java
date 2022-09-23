package com.wjh;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

public class Test6 {

    public static void main(String[] args) throws IOException {
        //删除多级目录
        Files.walkFileTree(Paths.get("D:\\docs"),new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("----->进入文件夹" + dir);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println("<-----退出文件夹" + dir);
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    private static void test1() throws IOException {
        AtomicInteger files = new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\maven"),new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".jar")) {
                    System.out.println(file);
                    files.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("files: " + files);
    }

    private static void test2() throws IOException {
        AtomicInteger dirs = new AtomicInteger();
        AtomicInteger files = new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\maven"),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("----->" + dir);
                dirs.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("+++++>" + file);
                files.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("dirs:" + dirs);
        System.out.println("files:" + files);
    }

}
