package taobao.autosell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taobao.autosell.entity.OrderData;
import taobao.autosell.entity.OrderPush;

import java.util.List;

/**
 * Created by asus on 2016/11/6.
 */
@Repository
public interface OrderDataRepository extends JpaRepository<OrderData,String>{
    List<OrderData> findByTid(OrderPush orderPush);
}
