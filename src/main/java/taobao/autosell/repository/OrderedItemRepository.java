package taobao.autosell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taobao.autosell.entity.OrderData;
import taobao.autosell.entity.OrderedItem;

import java.util.List;

/**
 * Created by asus on 2016/11/12.
 */
@Repository
public interface OrderedItemRepository extends JpaRepository<OrderedItem,String > {
    List<OrderedItem> findByOrderData(OrderData orderData);
}
