package taobao.autosell.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import taobao.autosell.entity.*;
import taobao.autosell.entity.domain.Repository;
import taobao.autosell.entity.rest.JsonResult;
import taobao.autosell.entity.rest.Receipt;
import taobao.autosell.repository.*;
import taobao.autosell.service.ManagementService;
import taobao.autosell.service.ProcessOrderService;

import javax.transaction.Transactional;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2016/12/3.
 */
@Service
public class ManagementServiceImpl implements ManagementService {
    @Autowired
    private PairRepository pairRepository;
    @Autowired
    private ProcessOrderService processOrderService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private OrderPushRepository orderPushRepository;
    @Autowired
    private OrderDataRepository orderDataRepository;
    @Value("${storage.number}")
    private String extraNumber;
    @Override
    @Transactional
    public JsonResult getRepository(Integer page) {
        Pageable pageable = new PageRequest(page,100);
        Page<Pair> pairs = pairRepository.findAll(pageable);
        List<Repository> repositories = new ArrayList<>();
        for (Pair pair : pairs){
            Repository repository = new Repository(pair.getTaobaoName(),pair.getMarketHashName(),pair.getPic());
            List<Integer> numbers = new ArrayList<>();
            String hashName = pair.getMarketHashName();
            String[] names = hashName.split(",");
            for (String name : names){
                String[] subNames = name.split("\\|");
                List<Type> types = typeRepository.findByMarkethashnames(Arrays.asList(subNames));
                Integer count = itemRepository.countByClassidInAndPlacedFalse(types.stream().map(Type::getClassid).collect(Collectors.toSet()));
                numbers.add(count);
            }
            Integer min = numbers.stream().min(Integer::compareTo).get();
            repository.setNumber(min);
            repositories.add(repository);
        }
        JsonResult jsonResult = new JsonResult(true,"","");
        jsonResult.setTotal(pairs.getTotalPages());
        repositories = repositories.stream().sorted((one,another)-> {
            if(one.getNumber() < another.getNumber()) {
                return -1;
            }else {
                return 1;
            }
        }).collect(Collectors.toList());
        jsonResult.setContent(repositories);
        return jsonResult;
    }

    @Override
    @Transactional
    public Integer savePair(String title, String pairs){
        List<List<String>> hashNames = new Gson().fromJson(pairs, new TypeToken<List<List<String>>>(){}.getType());
        String hash = "";
        for (Collection<String > part : hashNames){
            String subHash = "";
            for (String replacement : part){
                if (!StringUtils.isEmpty(replacement.trim())){
                    subHash += replacement.trim() + "|";
                }
            }
            if (!StringUtils.isEmpty(subHash)){
                hash += subHash.substring(0, subHash.length() - 1) + ",";
            }
        }
        if (hash.length() > 0){
            hash = hash.substring(0, hash.length() - 1);
            Pair pair = new Pair();
            pair.setMarketHashName(hash);
            pair.setTaobaoName(title);
            pairRepository.save(pair);
            return 0;
        }else {
            return -1;
        }


    }

    @Transactional
    @Override
    public void newStorage(String html){
        Gson gson = new Gson();
        Pattern pattern = Pattern.compile("oItem.+?\\{.+?};");
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()){
            String itemJson = matcher.group();
            itemJson = itemJson.substring(itemJson.indexOf('{'));
            JsonReader jsonReader = new JsonReader(new StringReader(itemJson));
            Receipt receipt = gson.fromJson(jsonReader, Receipt.class);
            Type type = new Type();
            type.setId(receipt.getClassid() + "_" + receipt.getInstanceid());
            type.setClassid(receipt.getClassid());
            type.setInstanceid(receipt.getInstanceid());
            type.setMarket_hash_name(receipt.getMarket_hash_name());
            type.setMarket_name(receipt.getMarket_name());
            type.setName(receipt.getName());

            Item item = new Item();
            item.setId(receipt.getId());
            item.setClassid(receipt.getClassid());
            item.setInstanceid(receipt.getInstanceid());
            item.setAmount(receipt.getAmount());
            item.setPos(receipt.getPos());
            itemRepository.save(item);
            typeRepository.save(type);
        }
    }

    @Override
    @Transactional
    public void updateNumber() {
        List<Pair> pairs = pairRepository.findAll();
        for (Pair pair : pairs){
            List<Integer> numbers = new ArrayList<>();
            String hashName = pair.getMarketHashName();
            String[] names = hashName.split(",");
            for (String name : names){
                String[] subNames = name.split("\\|");
                List<Type> types = typeRepository.findByMarkethashnames(Arrays.asList(subNames));
                Integer count = itemRepository.countByClassidInAndPlacedFalse(types.stream().map(Type::getClassid).collect(Collectors.toSet()));
                numbers.add(count);
            }
            Integer min = numbers.stream().min(Integer::compareTo).get();
            processOrderService.sendAgisoUpdateStorage(pair.getNumIid(),String.valueOf(min + Integer.valueOf(extraNumber)));
            pair.setNum(min);
        }
        pairRepository.save(pairs);
    }
    @Transactional
    @Override
    public Float cardOrder(String oid){
        OrderPush orderPush = orderPushRepository.findOne(oid.trim());
        if (orderPush != null) {
            List<OrderData> orderDatas = orderDataRepository.findByTid(orderPush);
            Float payment = 0f;
            for (OrderData orderData : orderDatas){
                if(orderData.getTitle().equals("卡牌充值专用") && orderData.getState() == 0){
                    payment += Float.valueOf(orderData.getPayment());
                    orderData.setState(2);
                    orderDataRepository.save(orderData);
                }
            }
            return payment;
        }else {
            return null;
        }
    }
}
