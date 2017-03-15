package taobao.autosell.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import taobao.autosell.entity.Item;
import taobao.autosell.entity.Storage;
import taobao.autosell.entity.Type;
import taobao.autosell.repository.ItemRepository;
import taobao.autosell.repository.TypeRepository;
import taobao.autosell.service.StorageService;


import javax.transaction.Transactional;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Created by asus on 2016/10/23.
 */
@Service("storageService")
public class StorageServiceImpl implements StorageService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Value("${storage.reload}")
    private String reload;
    @Value("${storage.reload.path}")
    private String jsonFilePath;
    private static String storageUrl = "https://steamcommunity.com/profiles/76561198177687081/inventory/json/570/2";
    RestTemplate restTemplate = new RestTemplate();

    private AtomicBoolean finishLoad = new AtomicBoolean(false);
//
//    private ConcurrentHashMap<String,Item> items = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<String,Type> types = new ConcurrentHashMap<>();

    @Override
    public boolean finishLoad(){
        return finishLoad.get();
    }

    @Override
    @Transactional
    public List<Item> getAllItems() throws InterruptedException {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().generateNonExecutableJson()
                .create();
        Boolean more = true;
        Integer start = 0;
        while (more){
            try {
                System.out.println("extract json.... from start : " + start);
                byte[] s = restTemplate.getForObject(storageUrl + "?start=" + start, byte[].class);
                JsonReader reader = new JsonReader(new StringReader(new String(s)));
                reader.setLenient(true);

                Storage storage = gson.fromJson(reader, Storage.class);
                if (storage.getMore_start() instanceof Double)
                    start = ((Double) storage.getMore_start()).intValue();
                more = storage.getMore();
                System.out.println(storage.getRgDescriptions().keySet().size());
                System.out.println(storage.getRgInventory().keySet().size());
                addStorage(storage);
            }catch (Exception e){
                e.printStackTrace();
            }
            Thread.sleep(10 * 1000);
        }
        return null;
    }

    @Override
    @Transactional
    public void reload() throws InterruptedException {
//        items.clear();
//        types.clear();
        finishLoad.set(false);
        if (reload != null && reload.equals("web")) {
            typeRepository.deleteAll();
            itemRepository.deleteAll();
            getAllItems();
        }else if (reload != null && reload.equals("file")){
            typeRepository.deleteAll();
            itemRepository.deleteAll();
            getAllItemFromFile();
        }
        finishLoad.set(true);
    }

//    public void remove(String id){
//        items.remove(id);
//    }

    @Transactional
    private void addStorage(Storage storage){
        itemRepository.save(storage.getRgInventory().values());
        Iterable<Type> types = storage.getRgDescriptions().entrySet().stream()
                .map(p ->
                        {
                            p.getValue().setId(p.getKey());
                            return p.getValue();
                        }
                ).collect(Collectors.toSet());
        typeRepository.save(types);
    }
    @Override
    @Transactional
    public void getAllItemFromFile(){
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().generateNonExecutableJson()
                .create();
        File dir = new File(jsonFilePath);
        if (dir.exists() && dir.isDirectory()){
            for (File file : dir.listFiles()){
                System.out.println("extract json.... from : " + file.getName());
                try {
                    JsonReader reader = new JsonReader(new FileReader(file));
                    reader.setLenient(true);
//                    String line = reader.readLine();
//                    while (line != null){
//                        json += line;
//                        line = reader.readLine();
//                    }
                    Storage storage = gson.fromJson(reader, Storage.class);
                    addStorage(storage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
