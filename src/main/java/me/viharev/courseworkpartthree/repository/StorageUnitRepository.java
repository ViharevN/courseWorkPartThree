package me.viharev.courseworkpartthree.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.viharev.courseworkpartthree.services.SocksFileService;
import me.viharev.courseworkpartthree.models.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

@Data
@Repository
public class StorageUnitRepository {

    static Long idCounter = 1L;
    private HashMap<Long, StorageUnit> storageUnitList = new HashMap<>();

    private final SocksFileService socksFileService;

    public StorageUnitRepository(SocksFileService socksFileService) {
        this.socksFileService = socksFileService;
    }

    public boolean addInStorageUnitRepository(int cottonPart,
                                              SocksColor socksColor,
                                              SocksSize socksSize,
                                              int quantity) {
        if (checkInputValues(cottonPart, quantity)) {
            StorageUnit bufferStorageUnit = new StorageUnit(cottonPart, socksColor, socksSize, quantity);
            if (storageUnitList.isEmpty()) {
                storageUnitList.put(idCounter, bufferStorageUnit);
                idCounter++;
                socksFileService.cleanSocksListJson();
                socksFileService.saveSocksListToJsonFile(jsonFromList());
                return true;
            }
            if (checkContainsInputValues(cottonPart, socksColor, socksSize)) {
                Long currentKey = findKey(cottonPart, socksColor, socksSize);
                int newQuantity = storageUnitList.get(currentKey).getQuantity() + bufferStorageUnit.getQuantity();
                bufferStorageUnit.setQuantity(newQuantity);
                storageUnitList.remove(currentKey);
                storageUnitList.put(currentKey, bufferStorageUnit);
                socksFileService.cleanSocksListJson();
                socksFileService.saveSocksListToJsonFile(jsonFromList());
                return true;
            }
            if (!checkContainsInputValues(cottonPart, socksColor, socksSize)) {
                idCounter = 1L;
                while (storageUnitList.containsKey(idCounter)) {
                    idCounter++;
                }
                storageUnitList.put(idCounter, bufferStorageUnit);
                idCounter++;
                socksFileService.cleanSocksListJson();
                socksFileService.saveSocksListToJsonFile(jsonFromList());
                return true;
            }
        }
        return false;
    }

    public int findByCottonPartLessThan(SocksColor socksColor, SocksSize socksSize, int cottonMin) {
        int quantity = 0;
        Collection<StorageUnit> units = storageUnitList.values();
        for (StorageUnit su : units) {
            if (su.getSocksColor().equals(socksColor) &
                    su.getSocksSize().equals(socksSize) &
                    su.getCottonPart() < cottonMin) {
                quantity += su.getQuantity();
            }
        }
        return quantity;
    }

    public int findByCottonPartMoreThan(SocksColor socksColor, SocksSize socksSize, int cottonMax) {
        int quantity = 0;
        Collection<StorageUnit> units = storageUnitList.values();
        for (StorageUnit su : units) {
            if (su.getSocksColor().equals(socksColor) &
                    su.getSocksSize().equals(socksSize) &
                    su.getCottonPart() > cottonMax) {
                quantity += su.getQuantity();
            }
        }
        return quantity;
    }

    public boolean delete(int cottonPart, SocksColor socksColor, SocksSize socksSize, int quantity) {
        Long bufferKey = findKey(cottonPart, socksColor, socksSize);
        if (storageUnitList.containsKey(bufferKey)) {
            storageUnitList.remove(bufferKey);
            socksFileService.cleanSocksListJson();
            socksFileService.saveSocksListToJsonFile(jsonFromList());
            return true;
        }
        return false;
    }

    public boolean outFromStorage(int cottonPart, SocksColor socksColor, SocksSize socksSize, int quantity) {

        Long bufferKey = findKey(cottonPart, socksColor, socksSize);
        if (storageUnitList.containsKey(bufferKey)) {
            StorageUnit bufferStorageUnit = storageUnitList.get(bufferKey);
            int bufferQuantity = bufferStorageUnit.getQuantity() - quantity;
            if (bufferQuantity > 0) {
                bufferStorageUnit.setQuantity(bufferQuantity);
                storageUnitList.remove(bufferKey);
                storageUnitList.put(bufferKey, bufferStorageUnit);
                socksFileService.cleanSocksListJson();
                socksFileService.saveSocksListToJsonFile(jsonFromList());
                return true;
            }
            if (bufferQuantity == 0) {
                storageUnitList.remove(bufferKey);
                socksFileService.cleanSocksListJson();
                socksFileService.saveSocksListToJsonFile(jsonFromList());
                return true;
            }
        }
        return false;
    }

    private HashMap<Long, StorageUnit> listFromFile() {
        try {
            String json = socksFileService.readSocksListFromJsonFile();
            if (StringUtils.isNotEmpty(json) || StringUtils.isNotBlank(json)) {
                storageUnitList = new ObjectMapper().readValue(json, new TypeReference<>() {
                });
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return storageUnitList;
    }

    private String jsonFromList() {
        String json;
        try {
            json = new ObjectMapper().writeValueAsString(storageUnitList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    //Здесь ищем key, которому соответствует входящий юнит
    private Long findKey(int cottonPart, SocksColor socksColor, SocksSize socksSize) {
        Long bufferKey = 1L;
        Set<Long> keys = storageUnitList.keySet();
        for (Long key : keys) {
            if (checkContainsInputValues(cottonPart,
                    socksColor,
                    socksSize)) {
                bufferKey = key;
            }
        }
        return bufferKey;
    }

    private boolean checkContainsInputValues(int cottonPart, SocksColor socksColor, SocksSize socksSize) {
        for (StorageUnit storageUnitEntry : storageUnitList.values()) {
            if (storageUnitEntry.getSocksColor().equals(socksColor) &
                    storageUnitEntry.getSocksSize().equals(socksSize) &
                    storageUnitEntry.getCottonPart() == cottonPart) {
                return true;
            }
        }
        return false;
    }

    private boolean checkInputValues(int cottonPart,
                                     int quantity) {
        return cottonPart > 0 & cottonPart <= 100 & quantity > 0;
    }

    @PostConstruct
    private void init() {
        storageUnitList = listFromFile();
    }
}
