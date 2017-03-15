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
@Table(name = "autosell_item_name_pair")
public class Pair {
    @Id
    @GeneratedValue(generator = "uuidGenerator")
    @GenericGenerator(name = "uuidGenerator", strategy = "uuid")
    private String id;
    private String taobaoName;
    @Type(type = "text")
    private String marketHashName;
    private String pic;
    //淘宝宝贝ID
    private String numIid;
    //淘宝宝贝存量
    private Integer num;
    private int type = 0;
}
