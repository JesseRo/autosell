package taobao.autosell.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by asus on 2016/10/30.
 */
@Data
@Table(name = "autosell_order_object")
@Entity
public class OrderData {
    private String Title;
    @Id
    private String Oid;
    @JoinColumn
    @ManyToOne
    private OrderPush tid;

    private String Payment;

    private int Num;

    private int state = 0;
}
