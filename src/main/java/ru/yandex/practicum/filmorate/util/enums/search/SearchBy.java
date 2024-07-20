package ru.yandex.practicum.filmorate.util.enums.search;

public enum SearchBy implements SearchParameter {
    DIRECTOR {
        @Override
        public String getQueryByParameter() {
            return """
                    SELECT F.*,
                    FG.GENRE_ID,
                    FUL.USER_ID as liked_user_id,
                    FD.DIRECTOR_ID
                    FROM FILM F
                    LEFT JOIN PUBLIC.FILM_GENRE FG ON F.FILM_ID = FG.FILM_ID
                    LEFT JOIN PUBLIC.FILM_USER_LIKES FUL ON F.FILM_ID = FUL.FILM_ID
                    LEFT JOIN PUBLIC.FILM_DIRECTOR FD ON F.FILM_ID = FD.FILM_ID
                    LEFT JOIN PUBLIC.DIRECTOR D ON FD.DIRECTOR_ID = D.DIRECTOR_ID
                    WHERE D.NAME LIKE ?
                    """;
        }
    },
    TITLE {
        @Override
        public String getQueryByParameter() {
            return """
                    SELECT F.*,
                    FG.GENRE_ID,
                    FUL.USER_ID as liked_user_id,
                    FD.DIRECTOR_ID
                    FROM FILM F
                    LEFT JOIN PUBLIC.FILM_GENRE FG ON F.FILM_ID = FG.FILM_ID
                    LEFT JOIN PUBLIC.FILM_USER_LIKES FUL ON F.FILM_ID = FUL.FILM_ID
                    LEFT JOIN PUBLIC.FILM_DIRECTOR FD ON F.FILM_ID = FD.FILM_ID
                    WHERE F.NAME LIKE ?
                    """;
        }
    };
}
