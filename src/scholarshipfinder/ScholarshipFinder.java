package scholarshipfinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Saleh
 */
public class ScholarshipFinder {

    /**
     * @param args the command line arguments
     */
    public static FileOutputStream out;
    public static int count;
    public static int found;
    public static int women;
    public static ArrayList<Scholarship> scholars = new ArrayList<>();

    public static void add(String title, String url, String content, String cont_read, String tags, int like, int unlike, int awesome, int restricted) throws IOException {
        Scholarship s = new Scholarship();
        s.title = title;
        s.url = url;
        s.content = content;
        s.cont_read = cont_read;
        s.tags = tags;
        s.like = like;
        s.unlike = unlike;
        s.awesome = awesome;
        s.restricted = restricted;

        scholars.add(s);
    }

    public static void main(String[] args) {
        try {
            out = new FileOutputStream(new File("women_scholarships.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ScholarshipFinder.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        count = 0;
        found = 0;
        for (int i = 1; i <= 55; i++) {
            System.out.println("Looking at " + i + "th page.");

            Document doc;
            try {
                doc = Jsoup.connect("http://scholarship-positions.com/category/canada-graduate-positions/page/" + i + "/").timeout(10000).get();
            } catch (Exception e) {
                i--;
                continue;
            }
            Elements newsHeadlines = doc.select("div.blog-item1.gdl-divider.sixteen.columns.mt0");
            System.out.println("Searching in " + i + "th page.");

            newsHeadlines.forEach(new Consumer<Element>() {

                @Override
                public void accept(Element t) {
                    String title = t.select(".gdl-title a").html().toLowerCase();
                    String url = t.select(".gdl-title a").attr("href").toLowerCase();
                    String content = t.select(".blog-thumbnail-content").html().toLowerCase();
                    String cont_read = t.select(".blog-continue-reading").html().toLowerCase();
                    ArrayList<String> tags = new ArrayList<>();
                    t.select(".custom-scholarships li").forEach(new Consumer<Element>() {
                        @Override
                        public void accept(Element t) {
                            tags.add(t.html().toLowerCase());
                        }
                    });

                    String awesome_words[] = {"engineering", "computer", "artificial intelligence", "machine learning"};
                    String interesting_words[] = {"graduate", "international student", "overseas student", "master", "ms", "university of guelph", "ontario"};
                    String unlike_words[] = {"phd", "postdoc", "doctoral", "health", "canadian", "women", "university of "};
                    String restricted_words[] = {"biophotonics","for women","optical fiber transmissions","forest research", "bionanotechnology", "wind energy", "bio-medical engineering", "professional accounting", "liberal arts", "health", "social science"};

                    int interest = 0;
                    int unlike = 0;
                    int awesome = 0;
                    int restricted = 0;
                    for (int i = 0; i < interesting_words.length; i++) {
                        if (title.contains(interesting_words[i])) {
                            interest++;
                        }
                        if (content.contains(interesting_words[i])) {
                            interest++;
                        }
                        if (tags.contains(interesting_words[i])) {
                            interest++;
                        }
                    }

                    for (int i = 0; i < unlike_words.length; i++) {
                        if (title.contains(unlike_words[i])) {
                            unlike++;
                        }
                        if (content.contains(unlike_words[i])) {
                            unlike++;
                        }
                        if (tags.contains(unlike_words[i])) {
                            unlike++;
                        }
                    }

                    for (int i = 0; i < awesome_words.length; i++) {
                        if (title.contains(awesome_words[i])) {
                            awesome++;
                        }
                        if (content.contains(awesome_words[i])) {
                            awesome++;
                        }
                        if (tags.contains(awesome_words[i])) {
                            awesome++;
                        }
                    }

                    for (int i = 0; i < restricted_words.length; i++) {
                        if (title.contains(restricted_words[i])) {
                            restricted++;
                        }
                        if (content.contains(restricted_words[i])) {
                            restricted++;
                        }
                        if (tags.contains(restricted_words[i])) {
                            restricted++;
                        }
                    }

                    if (title.contains("women")) {
                        women++;
                    }
                    if (content.contains("women")) {
                        women++;
                    }
                    if (tags.contains("women")) {
                        women++;
                    }
                    try {
                        add(title, url, content, cont_read, tags.toString(), interest, unlike, awesome, restricted);
                    } catch (IOException ex) {
                        Logger.getLogger(ScholarshipFinder.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    count++;
                    if (interest > 0) {
                        found++;
                    }
                }
            });
        }

        System.out.println("I've found " + found + " out of " + count);
        System.out.println("There are " + women + " scholarships for women!");
        System.out.println("Sorting...");

        scholars.sort(new Comparator<Scholarship>() {

            @Override
            public int compare(Scholarship o1, Scholarship o2) {
                if (o1.awesome > o2.awesome) {
                    return -1;
                } else if (o1.awesome < o2.awesome) {
                    return 1;
                } else {
                    if (o1.restricted > o2.restricted) {
                        return 1;
                    } else if (o1.restricted < o2.restricted) {
                        return -1;
                    }

                    if (o1.like > o2.like) {
                        return -1;
                    } else if (o1.like < o2.like) {
                        return 1;
                    } else {
                        if (o1.unlike > o2.unlike) {
                            return 1;
                        } else if (o1.unlike < o2.unlike) {
                            return -1;
                        }

                        return 0;
                    }
                }

            }
        });

        System.out.println("Writing on file ...");
        for (int i = 0; i < scholars.size(); i++) {
            try {
                Scholarship a = scholars.get(i);

                out.write(("Title: " + a.title + "\n").getBytes());
                out.write(("Url: " + a.url + "\n").getBytes());
                out.write(("Content: " + a.content + "\n").getBytes());
                out.write(("Continue Reading: " + a.cont_read + "\n").getBytes());
                out.write(("Tags: " + a.tags.toString() + "\n").getBytes());
                out.write(("Like: " + a.like + "\n").getBytes());
                out.write(("Unlike: " + a.unlike + "\n").getBytes());
                out.write(("Awesome: " + a.awesome + "\n").getBytes());
                out.write(("Restricted: " + a.restricted + "\n" + "\n" + "\n").getBytes());
            } catch (IOException ex) {
                Logger.getLogger(ScholarshipFinder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(ScholarshipFinder.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Done.");
    }

}
