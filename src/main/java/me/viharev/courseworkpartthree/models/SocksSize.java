package me.viharev.courseworkpartthree.models;

public enum SocksSize {
    S (36, 37),
    M (38, 40),
    L (41, 43),
    XL (44, 46),
    XXL(47, 47);

    private final int shoe_size_min = 36;
    private final int shoe_size_max = 47;

    SocksSize(int shoe_size_min, int shoe_size_max) {
    }

    public int getShoe_size_min() {
        return shoe_size_min;
    }

    public int getShoe_size_max() {
        return shoe_size_max;
    }
}
