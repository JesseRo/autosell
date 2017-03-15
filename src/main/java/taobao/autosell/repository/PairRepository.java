package taobao.autosell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taobao.autosell.entity.Pair;

import java.util.Collection;
import java.util.List;

/**
 * Created by asus on 2016/10/30.
 */
@Repository
public interface PairRepository extends JpaRepository<Pair,String> {
    Pair findPairByTaobaoName(String taobaoName);

    List<Pair> findPairByTaobaoNameIn(Collection<String> taobaoNames);
}
