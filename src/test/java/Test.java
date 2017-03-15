/**
 * Created by asus on 2016/10/23.
 */

import org.springframework.beans.factory.annotation.Autowired;
import taobao.autosell.AutoSell;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import taobao.autosell.entity.Item;
import taobao.autosell.entity.Type;
import taobao.autosell.repository.ItemRepository;
import taobao.autosell.repository.TypeRepository;
import taobao.autosell.service.ProcessOrderService;
import taobao.autosell.service.StorageService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//
//@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！
//@SpringApplicationConfiguration(classes = AutoSell.class) // 指定我们SpringBoot工程的Application启动类
//@WebAppConfiguration // 由于
//@Component
public class Test {
    @Resource(name = "storageService")
    private StorageService storageService;
    @Autowired
    private ProcessOrderService orderService;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private ItemRepository itemRepository;

//    @org.junit.Test
    public void test(){
        orderService.sendAgisoUpdateStorage("533308885608","30");
    }
}