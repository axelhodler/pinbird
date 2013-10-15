package earth.xor.db;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import earth.xor.Bookmark;
import earth.xor.DbProperties;

public class BookmarkDatastore {

    private MongoClient client;
    private DBCollection col;

    public BookmarkDatastore(MongoClient c) {
        this.client = c;
        this.col = c.getDB(DbProperties.DB_NAME).getCollection(
                DbProperties.COL_NAME);
    }

    public void saveBookmark(Bookmark b) {
        col.insert(createBookmarkBasicDBObject(b));
    }

    private BasicDBObject createBookmarkBasicDBObject(Bookmark b) {
        return new BasicDBObject(DbProperties.TITLE, b.getTitle()).append(
                DbProperties.URL, b.getUrl());
    }

    public Bookmark getBookmark(String id) {
        DBObject dbo = col.findOne(new BasicDBObject(DbProperties.ID,
                new ObjectId(id)));
        Bookmark bm = createBookmarkFromDBObject(dbo);
        return bm;
    }

    private Bookmark createBookmarkFromDBObject(DBObject dbo) {
        Bookmark bm = new Bookmark();
        bm.setId(dbo.get(DbProperties.ID).toString());
        bm.setTitle(dbo.get(DbProperties.TITLE).toString());
        bm.setUrl(dbo.get(DbProperties.URL).toString());
        return bm;
    }

    public List<Bookmark> getAllBookmarks() {
        List<Bookmark> allBookmarks = new ArrayList<Bookmark>();
        DBCursor curs = col.find();
        iterateThroughAllBookmarks(allBookmarks, curs);

        return allBookmarks;
    }

    private void iterateThroughAllBookmarks(List<Bookmark> allBookmarks,
            DBCursor curs) {
        while (curs.hasNext()) {
            allBookmarks.add(createBookmarkFromDBObject(curs.next()));
        }
    }
}
