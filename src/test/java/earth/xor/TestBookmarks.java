package earth.xor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestBookmarks {

    @Test
    public void testCreatingBookmark() {
        Bookmark b = new Bookmark();

        b.setTitle("foo");
        b.setUrl("http://www.foo.org");

        assertEquals("foo", b.getTitle());
        assertEquals("http://www.foo.org", b.getUrl());
    }
}
