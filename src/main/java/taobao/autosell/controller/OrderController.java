package taobao.autosell.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import taobao.autosell.entity.OrderPush;
import taobao.autosell.entity.TradeOrder;
import taobao.autosell.entity.UserInfo;
import taobao.autosell.entity.rest.AnswerResult;
import taobao.autosell.entity.rest.Result;
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

//    @RequestMapping(value = "placeOrder",method = RequestMethod.POST)
//    public @ResponseBody
//    Result placeOrder(String buyer, String tids, String url){
//        Integer integer;
//        Result result;
//        List<String> tradeId = new ArrayList<>();
//        try {
//            integer = processOrderService.process(buyer.trim(), Arrays.asList(tids.trim().split(",")), url.trim(),tradeId);
//        } catch (RemoteException | ServiceException e) {
//            e.printStackTrace();
//            integer = 3;
//        }
//        switch (integer){
//            case 0:
//                result = new Result(true,tradeId.get(0),tradeId.get(1));
//                break;
//            case 1:
//                result = new Result(false,"报价连接错误","");
//                break;
//            case 2:
//                result = new Result(false,"订单号或淘宝用户名错误","");
//                break;
//            case 3:
//                result = new Result(false,"未知错误","");
//                break;
//            case 4:
//                result = new Result(false,"订单已处理或正在处理中","");
//                break;
//            case 5:
//                result = new Result(false,"机器人发生错误","");
//                break;
//            case 6:
//                result = new Result(false,tradeId.get(0),"");
//                break;
//            default:
//                result = new Result(false,"未知错误","");
//                break;
//        }
//        return result;
//    }

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
    public void tradeOffer(String steamId,HttpServletResponse response) throws IOException, ServiceException {
        String steamid = String.valueOf(Long.valueOf(steamId) + 76561197960265728L);
        int r = processOrderService.answer(steamid);
        switch (r){
            case 0:
                response.getWriter().write(0);
            case 1:
                response.getWriter().write(1);
            default:
                response.getWriter().write(-1);
        }
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
