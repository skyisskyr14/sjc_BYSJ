package com.sq;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BootSuccessPrinter {

    @EventListener(ApplicationReadyEvent.class)
    public void printSuccess() {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("       ██████╗ ██╗  ██╗██╗   ██╗");
        System.out.println("       ██╔══██╗██║  ██║██║   ██║");
        System.out.println("       ██████╔╝███████║██║   ██║");
        System.out.println("       ██╔══██╗██╔══██║██║   ██║");
        System.out.println("       ██████╔╝██║  ██║╚██████╔╝");
        System.out.println("       ╚═════╝ ╚═╝  ╚═╝ ╚═════╝");
        System.out.println();
        System.out.println("        ★ 宠物寄养预约与管理系统 ★");
        System.out.println();
        System.out.println("         ★ 作者： 宋  嘉  诚  ★");
        System.out.println("          ★ 学号： 22012388  ★");
        System.out.println();
        System.out.println("      ★ 好~运~连~连~ 永~不~宕~机~ ★");
        System.out.println("==========================================");
        System.out.println();
    }
}
