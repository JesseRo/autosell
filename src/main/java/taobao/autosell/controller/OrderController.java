package taobao.autosell.controller;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import taobao.autosell.entity.Item;
import taobao.autosell.entity.OrderPush;
import taobao.autosell.entity.TradeOrder;
import taobao.autosell.entity.UserInfo;
import taobao.autosell.entity.rest.*;
import taobao.autosell.repository.ItemRepository;
import taobao.autosell.repository.OrderDataRepository;
import taobao.autosell.repository.OrderPushRepository;
import taobao.autosell.repository.UserInfoRepository;
import taobao.autosell.service.ProcessOrderService;
import taobao.autosell.service.StorageService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2016/10/30.
 */
@Controller
@RequestMapping(value = "order")
public class OrderController {
    @Autowired
    private OrderPushRepository repository;
    @Autowired
    private OrderDataRepository orderDataRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ProcessOrderService processOrderService;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private ItemRepository itemRepository;
    Gson gson = new Gson();

    @RequestMapping(value = "/getOrderFromPush")
    public void getOrderFromPush(OrderPush orderPush,HttpServletResponse response,HttpServletRequest request) throws IOException, ServiceException {

        System.out.println("new order arrives");
        if (!storageService.finishLoad()){
            response.getWriter().write("error");
            return;
        }
//        if (orderPush.getTid() != null) {
//            repository.save(orderPush);
//        }
//        processOrderService.process(orderPush);

        int ret = processOrderService.saveOrderPush(orderPush);

        for (Map.Entry<String, String[]> e : request.getParameterMap().entrySet()) {
            if (e.getValue() != null && e.getValue().length > 0)
                System.out.println(e.getKey() + ":" + e.getValue()[0]);
            else
                System.out.println(e.getKey() + ":" + "null");
        }
        if (ret == 0) {
            OrderPush push = repository.findOne(orderPush.getTid());
            processOrderService.waitFriend(push.getSteamId().trim(), push.getBuyerNick(), null);
            response.getWriter().write("OK");
        }

    }


    @RequestMapping(value = "placeOrder",method = RequestMethod.POST)
    public @ResponseBody Result placeOrder(String buyer,String tids,String steamId,HttpServletResponse response) throws IOException, ServiceException {
        String steamid = String.valueOf(Long.valueOf(steamId) + 76561197960265728L);
        int r = processOrderService.waitFriend(steamid.trim(),buyer.trim(),tids.trim());
        switch (r){
            case 0:
                return new Result(true,"成功","");
            case 1:
                return new Result(false,"订单号或steamId错误","");
            case 2:
                return new Result(false,"交易正在处理中","");
            default:
                return new Result(false,"未知错误","");
        }
    }


    @RequestMapping(value = "tradeOffer",method = RequestMethod.GET)
    public @ResponseBody BotResult tradeOffer(String steamId) throws IOException, ServiceException {
//        String steamid = String.valueOf(Long.valueOf(steamId) + 76561197960265728L);
        int r = processOrderService.answer(steamId);
        switch (r){
            case 0:
                return new BotResult(true,"发送报价成功",null);
            case 1:
                return new BotResult(false,"未找到您的订单",null);
            default:
                return new BotResult(false,"服务器未知错误，请联系客服",null);
        }
    }


    @RequestMapping(value = "trade",method = RequestMethod.GET)
    public @ResponseBody BotResult trade(String steamId,HttpServletResponse response) throws IOException, ServiceException {
        //这些注释掉的代码是用来测试的
//        List<String > orderPushs = new ArrayList<>();
//        orderPushs.add("asdasd");
//        List<String  > items = new ArrayList<>();
//        items.add("10565935854");
//        BotOrders botOrders = new BotOrders(orderPushs,items);
//        return new BotResult(true, "", botOrders);
        return processOrderService.itemsForBot(steamId);
    }

    @RequestMapping(value = "furtherTrade",method = RequestMethod.POST)
    public @ResponseBody BotResult furtherTrade(String tradeOffer) throws IOException, ServiceException {
        FurtherTradeRequest request = gson.fromJson(tradeOffer, FurtherTradeRequest.class);
        List<OrderPush> orderPushes = repository.findAll(request.getContent().getOrderPushes());
        List<Item> items = itemRepository.findAll(request.getContent().getItems());
        processOrderService.waitAccept(request.getTradeId(),items,orderPushes,request.getSteamId());
        return new BotResult(true, "success",null);
    }

    @RequestMapping(value = "tradeReport",method = RequestMethod.POST)
    public @ResponseBody BotResult tradeReport(String tradeOffer,String type) throws IOException, ServiceException {
        if (type.equals("finish")){
            processOrderService.finishBotTrade(tradeOffer);
        }else if(type.equals("cancel")){
            System.out.println("交易已取消，重置订单状态..");
            processOrderService.cancelBotTrade(tradeOffer);
        }
        return new BotResult(true, "success",null);
    }
//    @RequestMapping(value = "userInfo",method = RequestMethod.POST)
//    public @ResponseBody Result userInfo(String user){
//        UserInfo userInfo = userInfoRepository.findOne(user);
//        if (userInfo != null){
//            return new Result(true,"",userInfo.getLink());
//        }else {
//            return new Result(false,"未找到您的信息","");
//        }
//    }

}
