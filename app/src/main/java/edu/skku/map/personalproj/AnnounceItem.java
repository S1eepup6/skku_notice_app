package edu.skku.map.personalproj;

public class AnnounceItem {
    private String title;
    private String URL;

    public AnnounceItem () { }

    public AnnounceItem(String title, String URL) {
        this.title = title;
        this.URL = URL;
    }

    public String getTitle() {
        return this.title;
    }

    public String getURL() {
        return this.URL;
    }
}
