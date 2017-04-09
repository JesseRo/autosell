package taobao.autosell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import taobao.autosell.entity.OrderData;
import taobao.autosell.entity.OrderPush;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2016/11/6.
 */
@Repository
public interface OrderDataRepository extends JpaRepository<OrderData,String>{
    List<OrderData> findByTid(OrderPush orderPush);

    @Query("select o.Title, sum(o.Num) from OrderData as o where o.Title in :title " +
            "and date(o.tid.finishTime) = current_date() " +
            "group by o.Title")
    List<Object> sumOrderGroupByPair(@Param("title") List<String> title);

    @Query("select o.Title, sum(o.Num) from OrderData as o where o.Title in :title " +
            "and date(o.tid.finishTime) = date(:dt) " +
            "group by o.Title")
    List<Object> sumOrderGroupByPair(@Param("title") List<String> title, @Param("dt")String dt);
}
