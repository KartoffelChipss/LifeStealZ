package org.strassburger.lifestealz.util.storage;

import org.strassburger.lifestealz.LifeStealZ;
import org.strassburger.lifestealz.util.storage.connectionPool.ConnectionPool;
import org.strassburger.lifestealz.util.storage.connectionPool.SQLiteConnectionPool;

public final class SQLiteStorage extends SQLStorage {
    private final SQLiteConnectionPool connectionPool;

    public SQLiteStorage(LifeStealZ plugin) {
        super(plugin);
        connectionPool = new SQLiteConnectionPool(getPlugin().getDataFolder().getPath() + "/userData.db");
    }

    @Override
    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    @Override
    protected String getInserOrReplaceStatement() {
        return "INSERT OR REPLACE INTO hearts (uuid, name, maxhp, hasbeenRevived, craftedHearts, craftedRevives, killedOtherPlayers, firstJoin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }
}
