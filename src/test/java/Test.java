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
import taobao.autosell.repository.OrderDataRepository;
import taobao.autosell.repository.OrderPushRepository;
import taobao.autosell.repository.TypeRepository;
import taobao.autosell.service.ProcessOrderService;
import taobao.autosell.service.StorageService;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private OrderDataRepository orderDataRepository;
    @Autowired
    private OrderPushRepository orderPushRepository;

//    @org.junit.Test
    @Transactional
    public void test() throws Exception {
        List<String > dd= new ArrayList<>();
        dd.add("DOTA2 卡尔祈求者 融合暗黑魔导士的试炼 解锁");
        dd.add("DOTA2 刀塔饰品 影魔SF不朽 黯影臂 麒麟手臂 自动发货");
        dd.add("DOTA2 刀塔 半人马战行者 人马不朽头 TI6不朽 地狱酋魁 自动发货");
        LocalDate localDate = LocalDate.now();
        LocalDate tomorrow = LocalDate.now();
        tomorrow.plusDays(1);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        Instant to = tomorrow.atStartOfDay().atZone(zone).toInstant();

        List<Object> aa = orderDataRepository.sumOrderGroupByPair(dd);
        aa.hashCode();
    }
}