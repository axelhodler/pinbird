package earth.xor.db;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import earth.xor.Bookmark;
import earth.xor.DbProperties;

public class BookmarkDatastore {

    private MongoClient client;
    private DBCollection col;

    public BookmarkDatastore(MongoClient c) {
        this.client = c;
        this.col = c.getDB(DbProperties.DB_NAME).getCollection("bookmarks");
    }

    public void saveBookmark(Bookmark b) {
        col.insert(new BasicDBObject("title", b.getTitle()).append("url",
                b.getUrl()));
    }

    public Bookmark getBookmark(Bookmark b) {
        DBObject dbo = col.findOne(new BasicDBObject("title", b.getTitle()));
        Bookmark bm = new Bookmark();
        bm.setTitle(dbo.get("title").toString());
        bm.setUrl(dbo.get("url").toString());
        return bm;
    }

}
