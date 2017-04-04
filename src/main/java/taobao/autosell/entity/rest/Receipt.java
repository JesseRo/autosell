package taobao.autosell.entity.rest;

import lombok.Data;
import taobao.autosell.entity.Tag;

import java.util.List;

/**
 * Created by asus on 2017/2/20.
 */
@Data
public class Receipt {
    private String id;
    private String classid;
    private String instanceid;
    private String market_hash_name;
    private String market_name;
    private String name;
    private Integer amount;
    private String pos;
    private List<Tag> tags;
}
