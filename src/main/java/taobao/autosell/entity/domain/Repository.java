package taobao.autosell.entity.domain;

import lombok.Data;

/**
 * Created by Administrator on 2016/12/3.
 */
@Data
public class Repository {
    private String title;
    private String marketHashName;
    private Integer number;
    private String pic;
    private Integer sellsToday;
    private Integer sellsYesterday;
    public Repository(String taobaoName, String marketHashName,String pic) {
        this.title = taobaoName;
        this.marketHashName = marketHashName;
        this.pic = pic;
    }
    public Repository(){

    }
}
