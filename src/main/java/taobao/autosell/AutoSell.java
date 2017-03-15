package taobao.autosell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import taobao.autosell.service.StorageService;

/**
 * Created by asus on 2016/10/16.
 */
@SpringBootApplication
@EnableScheduling
public class AutoSell {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(AutoSell.class,args);
        StorageService syn = applicationContext.getBean(StorageService.class);
        syn.reload();

    }
}