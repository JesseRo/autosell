package taobao.autosell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import taobao.autosell.entity.OrderPush;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by asus on 2016/10/30.
 */
@Repository
public interface OrderPushRepository extends JpaRepository<OrderPush,String> {
    List<OrderPush> findByBuyerNickAndStateAndSteamId(String buyerNick,int state,String steamId);

    List<OrderPush> findByStateAndSteamId(int state,String steamId);

    OrderPush findByBuyerNickAndStateAndTid(String buyerNick,int state,String tid);

    @Modifying
    @Query("update OrderPush o set o.steamId = ?1 where o.tid in ?2")
    @Transactional
    int setOrderSteamId(String steamId, Iterable<String> tids);
}
