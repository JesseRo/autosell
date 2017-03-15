package taobao.autosell.entity.rest;

import lombok.Data;

/**
 * Created by Administrator on 2016/12/3.
 */
@Data
public class JsonResult extends Result{
    public JsonResult(boolean result, String message, String url) {
        super(result, message, url);
    }
    private Object content;
    private Integer total;
}
