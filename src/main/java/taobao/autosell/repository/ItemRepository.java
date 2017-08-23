package taobao.autosell.repository;

/**
 * Created by asus on 2016/11/6.
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import taobao.autosell.entity.Item;
import taobao.autosell.entity.TradeOrder;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface ItemRepository extends JpaRepository<Item,String>,JpaSpecificationExecutor<Item>{
    Item findTop1ByClassidInAndPlacedFalse(Collection<String> classid);

    @Query(nativeQuery = true,value = "select * from autosell_item_detail where classid in :classid and placed = false order by id limit 0,:num")
    List<Item> findTopN(@Param("classid")Collection<String> classid, @Param("num")Integer num);

    Integer countByClassidInAndPlacedFalse(Collection<String> classid);

    @Query(nativeQuery = true,value = "select * from autosell_item_detail where CONCAT(classid,instanceid) in :clsIns and placed = false order by id limit 0,:num")
    List<Item> findStoneTopN(@Param("clsIns")List<String> clsIns, @Param("num")Integer num);

    @Query(nativeQuery = true,value = "select count(id) from autosell_item_detail where CONCAT(classid,instanceid) in :clsIns and placed = false ")
    Integer countStone(@Param("clsIns") List<String> clsIns);

    @Query(nativeQuery = true, value = "select id as BuyerNick, classid as BuyerMessage from autosell_item_detail")
    List<TradeOrder> testQuery();
}
