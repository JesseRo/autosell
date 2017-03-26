/**
 * Created by asus on 2016/10/23.
 */

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import taobao.autosell.AutoSell;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import taobao.autosell.entity.Item;
import taobao.autosell.entity.OrderPush;
import taobao.autosell.entity.Type;
import taobao.autosell.repository.ItemRepository;
import taobao.autosell.repository.OrderPushRepository;
import taobao.autosell.repository.TypeRepository;
import taobao.autosell.service.ProcessOrderService;
import taobao.autosell.service.StorageService;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @Autowired
    private OrderPushRepository orderPushRepository;

//    @org.junit.Test
    @Transactional
    public void test() throws Exception {
        OrderPush orderPush = orderPushRepository.findOne("2428598375070333");
        orderPush.setJson("{\"ReceiverName\":\"不**人\",\"ReceiverMobile\":\"\",\"ReceiverAddress\":\"所在区/服:国服\\r\\n角色名:fatbull\\r\\n备注:id  230348553\",\"BuyerArea\":null,\"ExtendedFields\":{},\"Tid\":jessetest,\"Status\":\"WAIT_SELLER_SEND_GOODS\",\"SellerNick\":\"mo_pc\",\"BuyerNick\":\"小肥牛5185\",\"BuyerMessage\":null,\"Price\":\"29.80\",\"Num\":1,\"TotalFee\":\"29.80\",\"Payment\":\"29.80\",\"PayTime\":null,\"Created\":\"2016-12-22 21:49:24\",\"Orders\":[{\"Oid\":2428598375070333,\"NumIid\":538929740317,\"OuterIid\":null,\"Title\":\"DOTA2 幽鬼 SPE UG 盛华之影套 全解锁 hao娘套 自动发货\",\"Price\":\"29.80\",\"Num\":1,\"TotalFee\":\"29.80\",\"Payment\":\"29.80\",\"SkuPropertiesName\":null}]}");
        OrderPush orderPush1 = new OrderPush();
        BeanUtils.copyProperties(orderPush, orderPush1);
        orderService.saveOrderPush(orderPush1);
        throw new Exception("sd");
    }
}