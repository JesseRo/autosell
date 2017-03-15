package taobao.autosell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taobao.autosell.entity.OrderPush;

import java.util.List;

/**
 * Created by asus on 2016/10/30.
 */
@Repository
public interface OrderPushRepository extends JpaRepository<OrderPush,String> {
    List<OrderPush> findByBuyerNickAndStateAndSteamId(String buyerNick,int state,String steamId);

    List<OrderPush> findByStateAndSteamId(int state,String steamId);

    OrderPush findByBuyerNickAndStateAndTid(String buyerNick,int state,String tid);
}
