package taobao.autosell.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

/**
 * Created by asus on 2016/10/30.
 */
@Data
@Entity
@Table(name = "autosell_type")
public class Type {
    @Id
    private String id;
    private String classid;
    private String instanceid;
//    private String icon_url;
//    private String icon_url_large;
    private String market_hash_name;
    private String market_name;
    private String name;
    private int type = 0;
    @Transient
    private List<Tag> tags;
}
