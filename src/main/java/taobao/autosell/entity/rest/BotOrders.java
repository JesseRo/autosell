package taobao.autosell.entity.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import taobao.autosell.entity.Item;

import java.util.List;

/**
 * Created by Administrator on 2017/3/19.
 */
@Data
@AllArgsConstructor
public class BotOrders {
    private List<String> orderPushes;
    private List<String> items;
}
