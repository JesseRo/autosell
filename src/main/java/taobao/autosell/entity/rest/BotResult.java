package taobao.autosell.entity.rest;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Administrator on 2017/3/18.
 */
@Data
@AllArgsConstructor
public class BotResult {
    private boolean result;
    private String message;
    private Object content;
}
