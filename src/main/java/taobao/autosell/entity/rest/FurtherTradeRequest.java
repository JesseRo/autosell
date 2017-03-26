package taobao.autosell.entity.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Administrator on 2017/3/19.
 */
@Data
@AllArgsConstructor
public class FurtherTradeRequest {
    public BotOrders content;
    public String tradeId;
    public String steamId;
}
