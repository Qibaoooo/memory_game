package nus.iss.sa57.team11;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.stream.Collectors;

public class ImageDownloader {

    protected boolean downloadAllImages(String imgURL, File dir) {
        try {
            Document doc = Jsoup.connect(imgURL).get();
            List<Element> imgs = doc.getElementsByTag("img");
            List<String> img_urls = imgs.stream()
                    .map(e -> e.absUrl("src"))
                    .filter(s -> s.endsWith("jpg"))
                    .collect(Collectors.toList());

            if (img_urls.size() < 20) {
                Log.e("downloadImage", "The website has < 20 imgs shown. Choose another website.");
            }

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 4; j++) {
                    String url = img_urls.get(4 * i + j);
                    File destFile = new File(dir, url.substring(url.lastIndexOf('/') + 1));
                    downloadImage(url, destFile);
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    protected boolean downloadImage(String imgURL, File destFile) {
        try {

            URL url2 = new URL(imgURL);
            URL url = new URL("https://p4.wallpaperbetter.com/wallpaper/291/663/679/stones-background-stones-spa-wallpaper-preview.jpg");
            URLConnection conn = url.openConnection();

            InputStream in = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(destFile);

            byte[] buf = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }

            out.close();
            in.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
