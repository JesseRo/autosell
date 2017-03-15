package taobao.autosell.entity;

import lombok.Data;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by asus on 2016/10/30.
 */
@Data
@Entity
@Table(name = "autosell_order")
public class OrderPush {
    public static int NOT_PLACED = 0;
    public static int PLACING = 1;
    public static int PLACED = 2;
    public static int CANNOT_PLACE = 3;

    @Id
    private String tid;
    private String timestamp;
    private String sign;
    private String aopic;
    @Type(type = "text")
    private String json;
    private String buyerNick;
    private String buyerMessage;
    private String steamId;
    private String tradeOfferId;
    private String payment;
    private int state = 0;
}
