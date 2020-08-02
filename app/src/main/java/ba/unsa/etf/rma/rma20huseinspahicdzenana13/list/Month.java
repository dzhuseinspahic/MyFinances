package ba.unsa.etf.rma.rma20huseinspahicdzenana13.list;

public enum Month {
    Jan(1), Feb(2), Mar(3), Apr(4), Maj(5), May(5),
    Jun(6), Jul(7), Avg(8), Aug(8), Sep(9), Okt(10),
    Oct(10), Nov(11), Dec(12);

    private final int value;

    Month (int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
