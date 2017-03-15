package taobao.autosell.service;

import org.springframework.stereotype.Service;
import taobao.autosell.entity.Item;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by asus on 2016/10/23.
 */
@Service
public interface StorageService {
    boolean finishLoad();

    List<Item> getAllItems() throws InterruptedException;

    void reload() throws InterruptedException;

    @Transactional
    void getAllItemFromFile();
}
