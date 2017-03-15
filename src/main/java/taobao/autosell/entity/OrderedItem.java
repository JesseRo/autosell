package taobao.autosell.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by asus on 2016/11/12.
 */
@Data
@Entity
@Table(name = "autosell_ordered_item")
public class OrderedItem {
    @Id
    private String id;
    private String classid;
    private String instanceid;
    @ManyToOne
    @JoinColumn
    private OrderData orderData;

    public OrderedItem(Item type) {
        classid = type.getClassid();
        instanceid = type.getInstanceid();
        id = type.getId();
    }
    public OrderedItem(){

    }
}
