package ua.com.papers.convertors;

/**
 * Created by oleh_kurpiak on 07.09.2016.
 */
public class Fields {

    public static class Publication {
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String ANNOTATION = "annotation";
        public static final String TYPE = "type";
        public static final String LINK = "link";
        public static final String STATUS = "status";
        public static final String PUBLISHER = "publisher";

        public static final String DEFAULT = ID + ',' + TITLE;
    }

    public static class User {
        public static final String ID = "id";
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String PASSWORD = "password";
        public static final String ROLE = "role";
        public static final String ACTIVE = "active";

        public static final String DEFAULT = ID + ',' + EMAIL;
    }

}
