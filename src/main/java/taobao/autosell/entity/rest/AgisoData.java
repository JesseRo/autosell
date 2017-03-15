package taobao.autosell.entity.rest;

import lombok.Data;

import java.util.List;

/**
 * Created by asus on 2017/3/5.
 */
@Data
public class AgisoData {
    private List<AgisoInfo> ResultsInfo;

    @Data
    public class AgisoInfo {
        private int Tid;
        private int ResultCode;
        private String ResultMsg;
    }
}
