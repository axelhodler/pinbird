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
        col.insert(createBookmarkBasicDBObject(b));
    }

    public Bookmark getBookmark(Bookmark b) {
        DBObject dbo = col.findOne(new BasicDBObject("title", b.getTitle()));
        Bookmark bm = new Bookmark();
        bm.setTitle(dbo.get(DbProperties.TITLE).toString());
        bm.setUrl(dbo.get(DbProperties.URL).toString());
        return bm;
    }

    private BasicDBObject createBookmarkBasicDBObject(Bookmark b) {
        return new BasicDBObject(DbProperties.TITLE, b.getTitle()).append(
                DbProperties.URL, b.getUrl());
    }
}
