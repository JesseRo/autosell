package taobao.autosell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import taobao.autosell.entity.Type;

import java.util.Collection;
import java.util.List;

/**
 * Created by asus on 2016/11/6.
 */
@Repository
public interface TypeRepository extends JpaRepository<Type,String> {
    @Query(nativeQuery = true,value = "select * from autosell_type where market_hash_name = :name")
    List<Type> findByMarkethashname(@Param("name")String market_hash_name);

    @Query(nativeQuery = true,value = "select * from autosell_type where market_hash_name in :names")
    List<Type> findByMarkethashnames(@Param("names")Collection<String> market_hash_names);

    Type findByInstanceidAndClassid(String instanceid,String classid);
}
