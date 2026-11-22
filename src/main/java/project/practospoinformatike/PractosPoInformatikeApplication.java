package project.practospoinformatike;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import project.practospoinformatike.service.BuyerService;
import project.practospoinformatike.service.SellerService;

import java.util.Scanner;

@SpringBootApplication
public class PractosPoInformatikeApplication {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(PractosPoInformatikeApplication.class, args);

        BuyerService bs = ctx.getBean("buyerService", BuyerService.class);
        SellerService ss = ctx.getBean("sellerService", SellerService.class);
        starter(bs, ss);
    }

    private static void starter(BuyerService bs, SellerService ss){
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Выберите роль(1 или 2): \n\t\t1. Покупатель \n\t\t2. Продавец");
            String option = scanner.nextLine();
            switch (option){
                case "1": bs.starter(); scanner.close(); break;
                case "2": ss.starter(); scanner.close(); break;
                default: System.out.println("некорректный формат"); break;
            }
            if (option.equals("1") | option.equals("2")) break;
        }
        System.out.println("программа завершена в исходном меню");
    }
}
