package taobao.autosell.service;

import org.springframework.scheduling.annotation.Scheduled;
import taobao.autosell.entity.Item;
import taobao.autosell.entity.OrderPush;
import taobao.autosell.entity.rest.AgisoResult;
import taobao.autosell.entity.rest.BotResult;

import javax.transaction.Transactional;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

/**
 * Created by asus on 2016/10/30.
 */
public interface ProcessOrderService {
    @Scheduled(fixedDelay = 30 * 1000)
    void processUnSuccessOrder();

    @Transactional
    int saveOrderPush(OrderPush orderPush);

    void sendAgisoUpdateStorage(String iId, String number);

    AgisoResult LogisticsDummySend(String tids);

    String query(String tids);

    void process(OrderPush orderPush);

    @Transactional
    Integer answer(String partner)  throws RemoteException, ServiceException;

    @Transactional
    Integer process(String buyer,String partner,List<String> orderPushs) throws RemoteException, ServiceException;

    List<Item> findTopN(Collection<String> classid, int num);

    @Transactional
    int waitFriend(String partner, String buyer,String tids) throws RemoteException, ServiceException;

    @Transactional
    void waitAccept(String tradeId, List<Item> items, List<OrderPush> tids, String buyerId);

    @Transactional
    BotResult itemsForBot(String steamId);

    void cancelBotTrade(String tradeOffer);

    @Transactional
    void finishBotTrade(String tradeOffer);
}
