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
        public static final String FILE_LINK = "file_link";
        public static final String PUBLISHER = "publisher_id";
        public static final String IN_INDEX = "in_index";
        public static final String STATUS = "status";
        public static final String LITERATURE_PARSED = "literature_parsed";
        public static final String AUTHORS = "authors_id";

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

    public static class Author{
        public static final String ID = "id";
        public static final String LAST_NAME="last_name";
        public static final String INITIALS = "initials";
        public static final String ORIGINAL = "original";
        public static final String MASTER = "master_id";

        public static final String DEFAULT = ID + ',' + LAST_NAME;
    }

    public static class AuthorMaster{
        public static final String ID = "id";
        public static final String LAST_NAME="last_name";
        public static final String INITIALS = "initials";
        public static final String AUTHORS = "authors";
        public static final String PUBLICATIONS_COUNT = "publications_count";
        public static final String PUBLICATIONS_ID_LIST = "publications_id";

        public static final String DEFAULT = ID + ',' + LAST_NAME + ',' + INITIALS;

    }

    public static class Publisher{
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String URL = "url";
        public static final String CONTACTS = "contacts";
        public static final String ADDRESS = "address";

        public static final String DEFAULT = ID + ',' + TITLE;
    }

    public static class Address{
        public static final String ID = "id";
        public static final String COUNTRY = "country";
        public static final String CITY = "city";
        public static final String ADDRESS = "address";
        public static final String DEFAULT = ID + ',' + ADDRESS;
    }

    public static class PublicationOrder{
        public static final String ID = "id";
        public static final String EMAIL = "email";
        public static final String REASON = "reason";
        public static final String ANSWER = "answer";
        public static final String STATUS = "status";
        public static final String PUBLICATION_ID = "publication_id";
        public static final String DATE = "date";
        public static final String DEFAULT = ID + ',' + STATUS;
    }

}
