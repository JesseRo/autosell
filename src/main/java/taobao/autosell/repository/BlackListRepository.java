package taobao.autosell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taobao.autosell.entity.BlackList;

/**
 * Created by asus on 2017/2/5.
 */
@Repository
public interface BlackListRepository extends JpaRepository<BlackList,String>{
}
