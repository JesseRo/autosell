package taobao.autosell.entity.rest;

import lombok.Data;

/**
 * Created by asus on 2017/2/9.
 */
@Data
public class AnswerResult {
    private String message;
    private int code;
    public AnswerResult(String message,int code){
        this.code = code;
        this.message = message;
    }
}
