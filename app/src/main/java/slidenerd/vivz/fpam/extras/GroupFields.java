package slidenerd.vivz.fpam.extras;

/**
     * {
     * "data": [
     * {
     * "name": "The Fantastic Four",
     * "id": "368380276678573",
     * "icon": "https://fbstatic-a.akamaihd.net/rsrc.php/v2/yQ/r/QSvrimiDFPQ.png",
     * "unread": 1
     * },
     * {
     * "name": "C/C++ and Native Development",
     * "id": "1704641256427590",
     * "icon": "https://fbstatic-a.akamaihd.net/rsrc.php/v2/yB/r/odyq1oFT40B.png",
     * "unread": 0
     * }
     * ],
     * "paging": {
     * "cursors": {
     * "before": "MzY4MzgwMjc2Njc4NTcz",
     * "after": "MTcwNDY0MTI1NjQyNzU5MAZDZD"
     * },
     * "next": "https://graph.facebook.com/v2.4/867531740000500/admined_groups?access_token=CAAXv1PzFiNABACJiweVkycJ1ANIZCIZBZA3qhuLqViq4YxdeQDuQQeZA0pBfbbg3FxE46nsZA9ZCFkAw3JqzhhZBNpSApozbynBg26SAjpWZB3pazCyRkJoz4YX2qZBYAthBt8SrDh9BVcfrdowdhgPHcKxjBrlKagYas6rEgmuoOn2JBspJuFY5FZB4Te5jJd8ZBZCKPxYQHB8dtv2DuFZAZCfZATx&pretty=0&fields=name%2Cid%2Cicon%2Cunread&limit=2&after=MTcwNDY0MTI1NjQyNzU5MAZDZD",
     * "previous": "https://graph.facebook.com/v2.4/867531740000500/admined_groups?access_token=CAAXv1PzFiNABACJiweVkycJ1ANIZCIZBZA3qhuLqViq4YxdeQDuQQeZA0pBfbbg3FxE46nsZA9ZCFkAw3JqzhhZBNpSApozbynBg26SAjpWZB3pazCyRkJoz4YX2qZBYAthBt8SrDh9BVcfrdowdhgPHcKxjBrlKagYas6rEgmuoOn2JBspJuFY5FZB4Te5jJd8ZBZCKPxYQHB8dtv2DuFZAZCfZATx&pretty=0&fields=name%2Cid%2Cicon%2Cunread&limit=2&before=MzY4MzgwMjc2Njc4NTcz"
     * }
     * }
     */
    public interface GroupFields {
        String DATA = "data";
        String ID = "id";
        String NAME = "name";
        String ICON = "icon";
        String UNREAD = "unread";
        String PAGING = "paging";
        String CURSORS = "cursors";
        String BEFORE = "before";
        String AFTER = "after";
        String NEXT = "next";
        String PREVIOUS = "previous";
    }