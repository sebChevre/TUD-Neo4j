package jura.network.bus.config;

/**
 * Created by sce on 19.08.14.
 */
public class Proxy {

    public static void enable(){
        System.setProperty("http.proxyHost", "proxy.ju.globaz.ch");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost", "proxy.ju.globaz.ch");
        System.setProperty("https.proxyPort", "8080");
    }
}
