package taobao.autosell.entity.rest;

import lombok.Data;

/**
 * Created by asus on 2016/12/18.
 */
@Data
public class AgisoResult {
    private boolean IsSuccess;
    private AgisoData Data;
    private int Error_Code;

}
