package taobao.autosell.service;

import taobao.autosell.entity.domain.Repository;
import taobao.autosell.entity.rest.JsonResult;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2016/12/3.
 */
public interface ManagementService {
    JsonResult getRepository(Integer page);

    @Transactional
    Integer savePair(String title, Collection<Collection<String>> hashNames);

    @Transactional
    void newStorage(String html);

    void updateNumber();

    @Transactional
    Float cardOrder(String oid);
}
