package taobao.autosell.service.impl;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import taobao.autosell.entity.*;
import taobao.autosell.entity.rest.*;
import taobao.autosell.repository.*;
import taobao.autosell.service.ProcessOrderService;
import taobao.autosell.service.Robot;

import javax.transaction.Transactional;
import javax.xml.rpc.ServiceException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by asus on 2016/10/30.
 */
@Service
public class ProcessOrderServiceImpl implements ProcessOrderService {

    @Autowired
    private Robot robot;
    @Autowired
    private OrderPushRepository orderPushRepository;
    @Autowired
    private OrderDataRepository orderDataRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private PairRepository pairRepository;
    @Autowired
    private BlackListRepository blackListRepository;
    @Autowired
    private OrderedItemRepository orderedItemRepository;

    private ConcurrentHashMap<String, Long> buyers = new ConcurrentHashMap<>();
    RestTemplate restTemplate = new RestTemplate();
    private Gson gson = new Gson();

    @Override
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void processUnSuccessOrder() {
        for (Map.Entry<String,Long> entry : buyers.entrySet()){
            if ( System.currentTimeMillis()  - entry.getValue() > 10 * 60 * 1000){
                buyers.remove(entry.getKey());
            }
        }
    }

    @Override
    @Transactional
    public int saveOrderPush(OrderPush orderPush){
        int ret = 0;
        TradeOrder tradeOrder = gson.fromJson(orderPush.getJson(), TradeOrder.class);
        if (orderPushRepository.findOne(tradeOrder.getTid()) != null){
            System.out.println("订单重复推送！");
            return -3;
        }
        if (blackListRepository.findOne(tradeOrder.getBuyerNick()) != null){
            System.out.println("黑名单");
            return -4;
        }
        orderPush.setTid(tradeOrder.getTid());
        orderPush.setBuyerNick(tradeOrder.getBuyerNick());
        orderPush.setBuyerMessage(tradeOrder.getBuyerMessage());
        orderPush.setPayment(tradeOrder.getPayment());
        String ra = tradeOrder.getReceiverAddress();
        Pattern pattern = Pattern.compile("\\d{8,}");
        Matcher matcher = pattern.matcher(ra);
        if (matcher.find()){
            String aa = matcher.group();
            orderPush.setSteamId(String.valueOf(Long.valueOf(aa) + 76561197960265728L));
        }else {
            orderPush.setSteamId(ra);
        }

//        String[] as = ra.split("\r\n");
//        int i = 0;
//        for (String a: as){
//            String aaa = a.substring(a.indexOf(':') + 1);
//            if (aaa.length() <= 5){
//                continue;
//            }
//            //"id","ID","数字ID"，"国服ID"，"Id"，"："，":" replace掉
//            aaa = aaa.replace("id","").replace("ID", "").replace("数字ID","").replace("国服ID","").replace("Id","").replace(":","").replace("：","").trim();
//
//            if (matcher.find()){
//
//            }
//            Long aa;
//            try {
//                aa = Long.valueOf(aaa);
//                if (blackListRepository.findOne(aa.toString()) != null){
//                    System.out.println("黑名单");
//                    return -4;
//                }
//                orderPush.setSteamId(String.valueOf(aa + 76561197960265728L));
//                break;
//            }catch (NumberFormatException e){
//                i++;
//                if (i == 3){
//                    System.out.println("数字id错误！");
//                    orderPush.setSteamId(ra);
//                    ret = -1;
//                }
//            }
//
//        }
        for(OrderData orderData : tradeOrder.getOrders()){
            if (pairRepository.findPairByTaobaoName(orderData.getTitle()) == null){
                orderPush.setState(2);
                ret = -2;
                break;
            }
        }
        orderPushRepository.save(orderPush);
        tradeOrder.getOrders().forEach(p -> {
            p.setTid(orderPush);
            orderDataRepository.save(p);
        });
        return ret;
    }

    @Override
    public void sendAgisoUpdateStorage(String iId, String number){
        for(int i = 0;i < 5;i++) {
            try {
                AgisoResult result = updateStorageInfo(iId, number);
                if (result.isIsSuccess()) {
                    if (result.getData().getResultsInfo().size() > 0){
                        if (result.getData().getResultsInfo().get(0).getResultCode() == 100){
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                System.out.println("Agiso finished rest api error");
            }
        }
    }

    private void sendAgisoFinish(String tids){
        for(int i = 0;i < 5;i++) {
            try {
                AgisoResult result = LogisticsDummySend(tids);
                if (result.isIsSuccess()) {
                    if (result.getData().getResultsInfo().size() > 0){
                        if (result.getData().getResultsInfo().get(0).getResultCode() == 100){
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                System.out.println("Agiso finished rest api error");
            }
        }
    }
    @Override
    public AgisoResult LogisticsDummySend(String tids ){
        String url = "http://gw.api.agiso.com/api/Trade/AldsProcessTrades";
        String appKey = "100899360200";
        String appSecret = "dm5h5yxduy6v5znbumrakmpw3dxgifh";
        String accessToken = "aldsx5rdg6aet4meftxuvgbcz3p9mcnzdkhf4vdagvyu7znn8";

        //设置头部
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+ accessToken);
        headers.add("ApiVersion","1");


        Map<String , String> data = new HashMap<>();
        data.put("tids", tids);
        data.put("ingoreOnOff","true");
        data.put("ingoreRefundCheck","true");
        data.put("ingoreBlackList","true");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long timestamp = System.currentTimeMillis() / 1000;
        data.put("timestamp", timestamp.toString());
        //参数签名
        try {
            data.put("sign", Sign(data, appSecret));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LinkedMultiValueMap<String,String > d = new LinkedMultiValueMap<>();
        for (Map.Entry<String,String > e : data.entrySet()) {
            d.add(e.getKey(),e.getValue());
        }
        HttpEntity<LinkedMultiValueMap> entity = new HttpEntity<>(d,headers);
        AgisoResult r = restTemplate.postForObject(url, entity, AgisoResult.class);
        System.out.println(r);
        return r;
    }

    public AgisoResult updateStorageInfo(String iId,String number){
        String url = "http://gw.api.agiso.com/api/Item/QuantityUpdate";
        String appKey = "100899360200";
        String appSecret = "dm5h5yxduy6v5znbumrakmpw3dxgifh";
        String accessToken = "aldsx5rdg6aet4meftxuvgbcz3p9mcnzdkhf4vdagvyu7znn8";

        //设置头部
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+ accessToken);
        headers.add("ApiVersion","1");


        Map<String , String> data = new HashMap<>();
        data.put("numIid", iId);
        data.put("quantity",number);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long timestamp = System.currentTimeMillis() / 1000;
        data.put("timestamp", timestamp.toString());
        //参数签名
        try {
            data.put("sign", Sign(data, appSecret));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LinkedMultiValueMap<String,String > d = new LinkedMultiValueMap<>();
        for (Map.Entry<String,String > e : data.entrySet()) {
            d.add(e.getKey(),e.getValue());
        }
        HttpEntity<LinkedMultiValueMap> entity = new HttpEntity<>(d,headers);
        AgisoResult r = restTemplate.postForObject(url, entity, AgisoResult.class);
        System.out.println(r);
        return r;
    }


    @Override
    public String query(String tids){
        String url = "http://gw.api.agiso.com/api/Trade/LogisticsDummySend";
        String appKey = "100899360200";
        String appSecret = "dm5h5yxduy6v5znbumrakmpw3dxgifh";
        String accessToken = "aldsx5rdg6aet4meftxuvgbcz3p9mcnzdkhf4vdagvyu7znn8";

        //设置头部
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+ accessToken);
        headers.add("ApiVersion","1");


        Map<String , String> data = new HashMap<>();
        data.put("tids", tids);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long timestamp = System.currentTimeMillis() / 1000;
        data.put("timestamp", timestamp.toString());
        //参数签名
        try {
            data.put("sign", Sign(data, appSecret));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LinkedMultiValueMap<String,String > d = new LinkedMultiValueMap<>();
        for (Map.Entry<String,String > e : data.entrySet()) {
            d.add(e.getKey(),e.getValue());
        }
        HttpEntity<LinkedMultiValueMap> entity = new HttpEntity<>(d,headers);
        String r = restTemplate.postForObject(url, entity, String.class);
        System.out.println(r);
        return null;
    }

    public String Sign(Map<String, String> params,String appSecret) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {

        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        StringBuilder query = new StringBuilder();
        query.append(appSecret);
        for (String key : keys) {
            String value = params.get(key);
            query.append(key).append(value);
        }
        query.append(appSecret);

        byte[] md5byte = encryptMD5(query.toString());

        return  byte2hex(md5byte);

    }
    //byte数组转成16进制字符串
    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toLowerCase());
        }
        return sign.toString();
    }
    //Md5摘要
    public static byte[] encryptMD5(String data) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return md5.digest(data.getBytes("UTF-8"));
    }

    @Override
    @Transactional
    public void process(OrderPush orderPush) {
        String json = orderPush.getJson();
        TradeOrder tradeOrder = new Gson().fromJson(json,TradeOrder.class);
        List<Pair> pairList = tradeOrder.getOrders().stream()
                .map(
                        p -> pairRepository.findPairByTaobaoName(p.getTitle())
                )
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public Integer answer(String partner)  throws RemoteException, ServiceException{
        List<OrderPush> orderPushs = orderPushRepository.findByStateAndSteamId(0, partner);
        if (orderPushs.isEmpty()){
            return 1;
        }
        switch (process("anyone", partner, orderPushs.stream().map(OrderPush::getTid).collect(Collectors.toList()))){
            case 0:
                return 0;
            case 5:
                return 3;
            case 6:
                return 2;
            default:
                return -1;
        }
    }

    @Override
    @Transactional
    public Integer process(String buyer,String partner,List<String> orders) throws RemoteException, ServiceException {
        if(buyer != null && !buyer.isEmpty()){
            List<OrderPush> orderPushs;
            if (orders == null) {
                orderPushs = orderPushRepository.findByBuyerNickAndStateAndSteamId(buyer, 0, partner);
            }else {
                orderPushs = orderPushRepository.findAll(orders);
                orderPushs = orderPushs.stream().filter(o->o.getState() == 0).collect(Collectors.toList());
            }
            List<Item> itemSendList = new ArrayList<>();
            String successMessage = "";
            String failureMessage = "";
            if (orderPushs == null || orderPushs.isEmpty()) {
                failureMessage = "无效订单或正在处理中，请联系客服";
            }else {
                for (OrderPush orderPush : orderPushs) {
                    System.out.println("order:" + orderPush.getTid());
                    if (placeTrade(orderPush, itemSendList)) {
                        successMessage += orderPush.getTid() + ",";
                    } else {
                        failureMessage = orderPush.getTid();
                    }
                }
                if (successMessage.length() > 0) {
                    successMessage = "订单" + successMessage.substring(0, successMessage.length() - 1) + "处理成功";
                }
                if (failureMessage.length() > 0) {
                    failureMessage = "订单" + failureMessage + "存货不足，请联系客服";
                }
            }
            if (itemSendList.size() != 0) {
                String param = partner + "|";
                System.out.println("param:" + param);
                for (Item item : itemSendList) {
                    param += item.getId() + ",";
                    System.out.println("item:" + item.getId());
                }
                String tradeId = sendOrder(param);
                int steamErrorCount = 0;
                while (tradeId.equals("false1")) {
                    try {
                        Thread.sleep(20 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    tradeId = sendOrder(param);
                    steamErrorCount++;
                    if (steamErrorCount >= 5) {
                        return 5;
                    }
                }
                if (!tradeId.equals("false2")) {
                    tradeId = tradeId.split(":")[1];
                } else {
                    return 5;
                }
                System.out.println("tradeId:" + tradeId);
//                trade.add(successMessage + "，" + failureMessage);
//                trade.add(tradeId);
                if (!failureMessage.isEmpty()) {
                    chat(partner + "|" + failureMessage);
                }
                waitAccept(tradeId, itemSendList, orderPushs, partner);
                return 0;
            } else {
//                trade.add(failureMessage);
                if (!failureMessage.isEmpty()) {
                    chat(partner + "|" + failureMessage);
                }
                return 6;
            }

        }
        return 2;
    }

    @Transactional
    private boolean placeOrder(OrderData orderData, List<Item> items){
        Pair pair = pairRepository.findPairByTaobaoName(orderData.getTitle());
        if (pair == null){
            return false;
        }
        String[] marketHashNames = pair.getMarketHashName().split(",");
        List<Item> _items = new ArrayList<>();
        for (String marketHashName : marketHashNames) {
            String[] names = marketHashName.split("\\|");
            List<Type> type3 = typeRepository.findByMarkethashnames(Arrays.asList(names));
            if (type3 != null && !type3.isEmpty()) {
                List<Item> item1 = itemRepository.findTopN(type3.stream().map(Type::getClassid).collect(Collectors.toSet()), orderData.getNum());
                if (item1.size() == orderData.getNum()) {
                    item1.forEach(p -> p.setPlaced(true));
                    itemRepository.save(item1);
                    for (Item i : item1) {
                        OrderedItem orderedItem = new OrderedItem(i);
                        orderedItem.setOrderData(orderData);
                        orderedItemRepository.save(orderedItem);
                    }
                    _items.addAll(item1);
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }
        items.addAll(_items);
        return true;
    }

    @Transactional
    private boolean placeTrade(OrderPush orderPush,List<Item> items){
        List<OrderData> orderDatas = orderDataRepository.findByTid(orderPush);
        orderPush.setState(OrderPush.PLACING);
        List<Item> _items = new ArrayList<>();
        for (OrderData orderData : orderDatas){
            if (!placeOrder(orderData,_items)){
                orderPush.setState(OrderPush.CANNOT_PLACE);
                orderPushRepository.save(orderPush);
                return false;
            }
        }
        orderPushRepository.save(orderPush);
        items.addAll(_items);
        return true;
    }

    @Override
    @Transactional
    public List<Item> findTopN(Collection<String> classid, int num){
        Specifications<Item> specifications = Specifications.where(null);

        Specification<Item> specification = SpecificationFactory.getIn("classid", classid);
        specifications = specifications.and(specification);
        specification = SpecificationFactory.getEqual("placed", false);
        specifications = specifications.and(specification);

        Pageable pageable = new PageRequest(0,num);
        return itemRepository.findAll(specifications,pageable).getContent();
    }

    @Transactional
    @Override
    public int waitFriend(String partner, String buyer,String tids) throws RemoteException, ServiceException {
        if (buyers.putIfAbsent(partner,System.currentTimeMillis()) != null){
            System.out.println("发往该steam用户的订单正在处理中...");
            System.out.println("上一次召唤时间:" + new Date(buyers.get(partner)));
            return 2;
        }else {
            System.out.println("正在生产新的订单...");
        }
        List<OrderPush> orderPushs;
        if (tids == null) {
            orderPushs = orderPushRepository.findByBuyerNickAndStateAndSteamId(buyer, 0, partner);
            if (orderPushs.isEmpty()) {
                return 0;
            }
        }else {
            orderPushs = new ArrayList<>();
            String[] tid = tids.split(",");
            for (String t : Stream.of(tid).map(String::trim).distinct().collect(Collectors.toList())) {
                OrderPush o = orderPushRepository.findByBuyerNickAndStateAndTid(buyer, 0, t);
                if (o != null){
                    orderPushs.add(o);
                }
            }
            if (orderPushs.isEmpty()){
                buyers.remove(partner);
                return 1;
            }
        }
        for (OrderPush orderPush : orderPushs) {
            List<OrderData> orderDatas = orderDataRepository.findByTid(orderPush);
            if (pairRepository.findPairByTaobaoNameIn(orderDatas.stream().map(OrderData::getTitle).collect(Collectors.toList())).size() > 0) {
                break;
            }
            if (orderPushs.indexOf(orderPush) == orderPushs.size() - 1) {
                buyers.remove(partner);
                return 1;
            }
        }

        Thread thread = new Thread(()->{
            Long currentTime = buyers.get(partner);
            try {
                addFriend(partner);
                String url = "http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key=F9E07DB151F050DB43E0FFAAA9C0FBB7&steamid=76561198177687081&relationship=friend";
                do{
                    Friends friends;
                    while(true) {
                        try {
                            friends = restTemplate.getForObject(url, Friends.class);
                            break;
                        }catch (Exception e){
                            System.out.println("get friend list error, retrying...");
                        }
                    }
                    List<Friend> friendList = friends.getFriendslist().getFriends();
                    if (friendList.stream().filter(p -> p.getRelationship().equals("friend")).map(Friend::getSteamid).collect(Collectors.toSet()).contains(partner)) {
                        System.out.println("friend " + partner + " added!");
//                        不主动发送tradeoffer了
//                        if (tids == null) {
//                            process(buyer, partner, null);
//                        }else {
//                            process(buyer,partner,orderPushs.stream().map(OrderPush::getTid).collect(Collectors.toList()));
//                        }
                        chat(partner + "|请回复'交易'或'报价'来领取您的物品。（不会接受报价的请回复'交易'）");
                        buyers.remove(partner);
                        return;
                    }
                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (System.currentTimeMillis() - currentTime < 10 * 60 * 1000);
                buyers.remove(partner);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        return 0;
    }

    @Transactional
    @Override
    public void waitAccept(String tradeId, List<Item> items, List<OrderPush> tids, String buyerId){
        long beginTime = System.currentTimeMillis();
        String url = "http://api.steampowered.com/IEconService/GetTradeOffer/v1/?key=DF768D875E848CE96C05567249F56EEC&tradeofferid=" + tradeId + "&language=en_us";
        Runnable runnable = () -> {
            try {
                loop:do {
                    Thread.sleep(10 * 1000);
                    TradeOfferState tradeOfferState;
                    while(true) {
                        try {
                            tradeOfferState = restTemplate.getForObject(url, TradeOfferState.class);
                            break;
                        }catch (Exception e){
                            System.out.println("get trade state error, retrying...");
                        }
                    }
                    switch (tradeOfferState.getResponse().getOffer().getTrade_offer_state())
                    {
                        case 3:
                            itemRepository.delete(items);
                            tids.stream().filter(p->p.getState() == OrderPush.PLACING).forEach(p -> {
                                sendAgisoFinish(p.getTid());
                                p.setState(OrderPush.PLACED);
                                p.setTradeOfferId(tradeId);
                                orderPushRepository.save(p);
                            });
                            try {
                                List<OrderPush> otherOrder = orderPushRepository.findByStateAndSteamId(0,buyerId);
                                if (otherOrder != null && otherOrder.size() >0){
                                    chat(buyerId + "|你还有订单未完成，请再次发送交易");
                                }else {
                                    chat(buyerId + "|交易已完成！");
                                    remove(buyerId);
                                }
                            } catch (RemoteException | ServiceException e) {
                                e.printStackTrace();
                            }
                            return;
                        case 6:
                        case 7:
                            break loop;
                    }
                }while (System.currentTimeMillis() - beginTime <= 10 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            items.forEach(p -> p.setPlaced(false));
            itemRepository.save(items);
            for (OrderPush tid:tids){
                tid.setState(OrderPush.NOT_PLACED);
                tid.setTradeOfferId(tradeId);
                orderPushRepository.save(tid);
                List<OrderData> orderDatas = orderDataRepository.findByTid(tid);
                for (OrderData orderData : orderDatas){
                    List<OrderedItem> orderedItems = orderedItemRepository.findByOrderData(orderData);
                    if (orderedItems.size() > 0){
                        orderedItemRepository.delete(orderedItems);
                    }
                }
            }
            try {
                cancelOrder(tradeId + "|" + buyerId );
                Thread.sleep(5 * 1000);
//                remove(buyerId);
            } catch (RemoteException | ServiceException | InterruptedException e) {
                e.printStackTrace();
            }
        };
        new Thread(runnable).start();
    }

    @Transactional
    @Override
    public BotResult itemsForBot(String steamId){
        List<OrderPush> orderPushs = orderPushRepository.findByStateAndSteamId(0, steamId);
        if (orderPushs == null || orderPushs.isEmpty()){
            return new BotResult(false, "未找到您的订单...", null );
        }
        List<Item> itemSendList = new ArrayList<>();
        String failureMessage = "";
        String message = "";
        List<String > succeedOrderPushes = new ArrayList<>();
        int errorOrderCount = 0;
        for (OrderPush orderPush : orderPushs) {
            System.out.println("order:" + orderPush.getTid());
            if (!placeTrade(orderPush, itemSendList)) {
                errorOrderCount++;
                failureMessage = orderPush.getTid();
            }else {
                succeedOrderPushes.add(orderPush.getTid());
            }
        }
        if (!failureMessage.isEmpty()){
            message = "您的订单: " + failureMessage + "暂无库存，请联系客服...";
        }else {
            message = "订单完成...";
        }
        if (errorOrderCount < orderPushs.size()){
            if(!itemSendList.isEmpty()){
                BotOrders botOrders = new BotOrders(succeedOrderPushes, itemSendList.stream().map(Item::getId).collect(Collectors.toList()));
                return new BotResult(true,message,botOrders);
            }
        }
        return new BotResult(false,message,null);
    }

    @Override
    @Transactional
    public void cancelBotTrade(String tradeOffer) {
        FurtherTradeRequest request = gson.fromJson(tradeOffer, FurtherTradeRequest.class);
        List<Item> items = itemRepository.findAll(request.getContent().getItems());
        items.forEach(p -> p.setPlaced(false));
        itemRepository.save(items);
        List<OrderPush> orderPushes = orderPushRepository.findAll(request.getContent().getOrderPushes());
        for (OrderPush tid:orderPushes){
            tid.setState(OrderPush.NOT_PLACED);
            tid.setTradeOfferId(request.getTradeId());
            orderPushRepository.save(tid);
            List<OrderData> orderDatas = orderDataRepository.findByTid(tid);
            for (OrderData orderData : orderDatas){
                List<OrderedItem> orderedItems = orderedItemRepository.findByOrderData(orderData);
                if (orderedItems.size() > 0){
                    orderedItemRepository.delete(orderedItems);
                }
            }
        }
    }

    @Override
    @Transactional
    public void finishBotTrade(String tradeOffer){
        FurtherTradeRequest request = gson.fromJson(tradeOffer, FurtherTradeRequest.class);
        List<Item> items = itemRepository.findAll(request.getContent().getItems());
        itemRepository.delete(items);
        List<OrderPush> orderPushes = orderPushRepository.findAll(request.getContent().getOrderPushes());
        orderPushes.stream().filter(p->p.getState() == OrderPush.PLACING).forEach(p -> {
            sendAgisoFinish(p.getTid());
            p.setState(OrderPush.PLACED);
            p.setTradeOfferId(request.getTradeId());
            orderPushRepository.save(p);
        });
        try {
            remove(request.getSteamId());
        } catch (RemoteException | ServiceException e) {
            e.printStackTrace();
        }
    }

    private String sendOrder(String param) throws RemoteException, ServiceException {
        for(int i = 0;i < 5;i++){
            try {
                return robot.send("send", param);
            }catch (RemoteException | ServiceException e){
                if (i == 4){
                    throw e;
                }
            }
        }
        return null;
    }
    private String cancelOrder(String param) throws RemoteException, ServiceException {
        for(int i = 0;i < 5;i++){
            try {
                return robot.send("cancel", param);
            }catch (RemoteException | ServiceException e){
                if (i == 4){
                    throw e;
                }
            }
        }
        return null;
    }
    private String addFriend(String param) throws RemoteException, ServiceException {
        for(int i = 0;i < 5;i++){
            try {
                return robot.send("add", param);
            }catch (RemoteException | ServiceException e){
                if (i == 4){
                    throw e;
                }
            }
        }
        return null;
    }
    private String remove(String param) throws RemoteException, ServiceException {
        for(int i = 0;i < 5;i++){
            try {
                return robot.send("remove", param);
            }catch (RemoteException | ServiceException e){
                if (i == 4){
                    throw e;
                }
            }
        }
        return null;
    }
    private String chat(String param) throws RemoteException, ServiceException {
        for(int i = 0;i < 5;i++){
            try {
                return robot.send("chat", param);
            }catch (RemoteException | ServiceException e){
                if (i == 4){
                    throw e;
                }
            }
        }
        return null;
    }
}
