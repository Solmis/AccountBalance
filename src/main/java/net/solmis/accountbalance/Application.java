package net.solmis.accountbalance;

public class Application {

    // Santander Login URL
    private static final String LOGIN_URL = "https://www.centrum24.pl/centrum24-web/login";

    // Santander Login Failed URL
    private static final String LOGIN_FAILED_URL = "centrum24.pl/centrum24-web/uep";

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("USAGE: ./app <NIK> <password>");
            return;
        }
        SantanderScrapper scrapper = new SantanderScrapper(args[0], args[1]);
        HttpsRequestService reqService = new HttpsRequestService();

        String formAddress = scrapper.getLoginFormAddress(reqService.sendGetRequest(LOGIN_URL), LOGIN_URL);
        String html = reqService.sendPostRequest(formAddress, scrapper.getNikAsParam());
        formAddress = scrapper.getLoginFormAddress(html, reqService.getCurrentUrl());
        html = reqService.sendPostRequest(formAddress, scrapper.getPasswordAsParam());

        if (reqService.getCurrentUrl().contains(LOGIN_FAILED_URL)) {
            System.err.println("Incorrect credentials. Login failed.");
            return;
        }

        html = reqService.sendGetRequest(scrapper.getAccountsLink(html, reqService.getCurrentUrl()));
        for (Account acc: scrapper.getAccounts(html))
            System.out.println(acc.toString());
    }
}
