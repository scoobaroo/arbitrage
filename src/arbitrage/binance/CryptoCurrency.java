package binance;

import java.util.HashMap;
import java.util.Map;

public enum CryptoCurrency {
	
	ETP("ETP"), SAN("SAN"), QTM("QTM"), EDO("EDO"), RRT("RRT"), XRP("XRP"), DSH("DSH"), BT1("BT1"), 
	BT2("BT2"), BCC("BCC"), EUR("EUR"), BCH("BCH"), USD("USD"), QSH("QSH"), EOS("EOS"), OMG("OMG"),
	IOT("IOT"), BTC("BTC"), BTG("BTG"), ETC("ETC"), BCU("BCU"), DAT("DAT"), YYW("YYW"), ETH("ETH"), 
	ZEC("ZEC"), NEO("NEO"), LTC("LTC"), XMR("XMR"), AVT("AVT"), GNT("GNT"), SNT("SNT"), BAT("BAR"),
	MNA("MNA"), FUN("FUN"), ZRX("ZRX"), TNB("TNB"), SPK("SPK"), TRX("TRX"), RCN("RCN"), RLC("RLC"),
	AID("AID"), SNG("SNG"), REP("REP"), ELF("ELF");
	
    protected final String name;

    private static final Map<String, CryptoCurrency> lookup = new HashMap<String, CryptoCurrency>();

    static {
        for (CryptoCurrency c : CryptoCurrency.values()) {
            lookup.put(c.name, c);
        }
    }

    CryptoCurrency(String name) {
        this.name = name;
    }

    public static CryptoCurrency get(String name) {
        return lookup.get(name);
    }
	
}
