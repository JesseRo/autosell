package taobao.autosell.entity.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by asus on 2017/3/5.
 */
@Data
@AllArgsConstructor
public class CardResult {
    private boolean result;
    private String message;
    private Float payment;
}
