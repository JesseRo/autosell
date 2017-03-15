package taobao.autosell.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by asus on 2016/10/30.
 */
@Data
//@Entity
//@Table(name = "autosell_order")
public class TradeOrder {
    private String Tid;
    private String BuyerNick;
    private String BuyerMessage;
    private String ReceiverAddress;
    private String Payment;
    private List<OrderData> Orders;
}
