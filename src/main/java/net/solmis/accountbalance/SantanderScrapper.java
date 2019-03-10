package net.solmis.accountbalance;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

class SantanderScrapper {
    private String nik;
    private String password;

    SantanderScrapper(String nik, String password) {
        this.nik = nik;
        this.password = password;
    }

    String getLoginFormAddress(String html, String baseUri) {
        Document doc = Jsoup.parse(html, baseUri);
        Element form = doc.selectFirst("form");
        return form.attr("abs:action");
    }

    String getAccountsLink(String html, String baseUri) {
        Document doc = Jsoup.parse(html, baseUri);
        return doc.selectFirst("#menu_portfel_24 a").attr("abs:href");
    }

    List<Account> getAccounts(String html) {
        List<Account> accList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        for (Element tr: doc.select("#avistaAccounts tbody tr, #savings tbody tr"))
            accList.add(accountFromRow(tr));
        return accList;
    }

    String getNikAsParam() {
        return getFieldAsParam("loginInput:nik", nik);
    }

    String getPasswordAsParam() {
        return getFieldAsParam("pinFragment:pin", password);
    }

    private Account accountFromRow(Element tr) {
        Element nameCol = tr.selectFirst("td.name");
        String accName = nameCol.selectFirst("a").text();
        BigInteger accNo = new BigInteger(nameCol.selectFirst("em").text().replaceAll("\\s+", ""));

        Elements moneyCols = tr.select("td.money");
        Element moneyCol = moneyCols.get(moneyCols.size() - 1);
        String strBalance = moneyCol.selectFirst("div").text();
        int CURRENCY_CODE_LEN = 3;
        String currencyCode = strBalance.substring(strBalance.length() - CURRENCY_CODE_LEN);
        String balance = strBalance.substring(0, strBalance.length() - CURRENCY_CODE_LEN)
                .replaceAll("[^0-9,]+", "").replace(",", ".");

        BigDecimal accBalance = new BigDecimal(balance);
        Currency currency = Currency.getInstance(currencyCode);
        return new Account(accName, accNo, accBalance, currency);
    }

    private String getFieldAsParam(String key, String value) {
        try {
            return key + "=" + URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
