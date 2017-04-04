package taobao.autosell.service;

import taobao.autosell.entity.rest.JsonResult;
import taobao.autosell.entity.rest.Result;

import javax.transaction.Transactional;

/**
 * Created by Administrator on 2016/12/3.
 */
public interface ManagementService {
    JsonResult getRepository(Integer page);

    @Transactional
    Integer savePair(String title, String pairs);

    @Transactional
    void newStorage(String html);

    void updateNumber();

    @Transactional
    Float cardOrder(String oid);

    @Transactional
    JsonResult orderDetail(String orderId);

    @Transactional
    Result orderSave(String orderId, String steamId, String state);
}
