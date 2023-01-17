package me.viharev.courseworkpartthree.models;

import lombok.Data;

@Data
public class StorageUnit {

    private int cottonPart;
    private SocksColor socksColor;
    private SocksSize socksSize;
    private int quantity;

    public StorageUnit(int cottonPart,
                       SocksColor socksColor,
                       SocksSize socksSize,
                       int quantity) {
        this.cottonPart = cottonPart;
        this.socksColor = socksColor;
        this.socksSize = socksSize;
        this.quantity = quantity;
    }

    public StorageUnit() {
    }
}
