package taobao.autosell.entity.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by asus on 2016/11/12.
 */
@Data
@AllArgsConstructor
public class Result {
    private boolean result;
    private String message;
    private String url;
    public Result(){

    }
}
