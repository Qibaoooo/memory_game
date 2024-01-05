package nus.iss.sa57.team11;

import android.util.Log;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageDownloader {

    protected List<String> getIndividualImageUrls(String imgURL) {
        try {
            Document doc = Jsoup.connect(imgURL).get();
            List<Element> imageElements;
            imageElements = doc.getElementsByTag("img");

            List<String> img_urls_data_src = getImageUrlsFromAttribute(imageElements, "data-src");
            List<String> img_urls_src = getImageUrlsFromAttribute(imageElements, "src");
            List<String> img_urls = Stream
                    .concat(img_urls_src.stream(), img_urls_data_src.stream())
                    .collect(Collectors.toList());
            if (img_urls.size() < 20) {
                Log.e("downloadImage", "The website has < 20 imageElements shown. Choose another website.");
                throw new Exception();
            }
            return img_urls;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @NonNull
    private static List<String> getImageUrlsFromAttribute(List<Element> imageElements, String attr) {
        return imageElements.stream()
                .map(e -> e.absUrl(attr))
                .filter(s -> s.endsWith("jpg") || s.endsWith("png") || s.endsWith("jpeg"))
                .collect(Collectors.toList());
    }

    protected boolean downloadImage(String imgURL, File destFile) {
        try {

            URL url = new URL(imgURL);
            // for testing
            // URL url2 = new URL("https://p4.wallpaperbetter.com/wallpaper/291/663/679/stones-background-stones-spa-wallpaper-preview.jpg");

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
