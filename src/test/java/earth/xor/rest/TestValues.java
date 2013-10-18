package earth.xor.rest;

import com.mongodb.BasicDBObject;

import earth.xor.db.DbProperties;

public class TestValues {

    private TestValues() {
    }

    public static final String POST_BOOKMARK_1 = "{\"bookmark\":{\"title\":\"foo\", \"url\":\"http://www.foo.org\"}}";

    public static final BasicDBObject BOOKMARK_1 = new BasicDBObject(
            DbProperties.TITLE, "foo").append(DbProperties.URL,
            "http://www.foo.org");

    public static final BasicDBObject BOOKMARK_2 = new BasicDBObject(
            DbProperties.TITLE, "bar").append(DbProperties.URL,
            "http://www.bar.org");

    public static final BasicDBObject BOOKMARK_3 = new BasicDBObject(
            DbProperties.TITLE, "baz").append(DbProperties.URL,
            "http://www.baz.org");
}
