package com.bot.takagi3.util;

import org.iq80.leveldb.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class LevelDBSingleton implements DB {
    public static final LevelDBSingleton INSTANCE = new LevelDBSingleton();
    private static DB db;

    private LevelDBSingleton() {
    }

    public DB getDatabase() {
        return db;
    }

    public static void init(DB db) {
        LevelDBSingleton.db = db;
    }

    public byte[] get(String key) {
        return get(key.getBytes());
    }

    public void set(String key, byte[] value) {
        put(key.getBytes(), value);
    }

    public void set(String key, byte[] value, WriteOptions writeOptions) {
        put(key.getBytes(), value, writeOptions);
    }

    public void delete(String key) {
        delete(key.getBytes());
    }

    public void delete(String key, WriteOptions writeOptions) {
        delete(key.getBytes(), writeOptions);
    }

    public ArrayList<Map.Entry<byte[], byte[]>> filter(Function<Map.Entry<byte[], byte[]>, Boolean> fun) {
        DBIterator iterator = iterator();
        ArrayList<Map.Entry<byte[], byte[]>> list = new ArrayList<>();
        for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
            if (fun.apply(iterator.peekNext())) list.add(iterator.peekNext());
        }
        try {
            iterator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public byte[] get(byte[] key) throws DBException {
        return db.get(key);
    }

    @Override
    public byte[] get(byte[] key, ReadOptions options) throws DBException {
        return db.get(key, options);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public DBIterator iterator() {
        return db.iterator();
    }

    @Override
    public void forEach(Consumer<? super Map.Entry<byte[], byte[]>> action) {
        db.forEach(action);
    }

    @Override
    public Spliterator<Map.Entry<byte[], byte[]>> spliterator() {
        return db.spliterator();
    }

    @Override
    public DBIterator iterator(ReadOptions options) {
        return db.iterator(options);
    }

    @Override
    public void put(byte[] key, byte[] value) throws DBException {
        db.put(key, value);
    }

    @Override
    public void delete(byte[] key) throws DBException {
        db.delete(key);
    }

    @Override
    public void write(WriteBatch updates) throws DBException {
        db.write(updates);
    }

    @Override
    public WriteBatch createWriteBatch() {
        return db.createWriteBatch();
    }

    @Override
    public Snapshot put(byte[] key, byte[] value, WriteOptions options) throws DBException {
        return db.put(key, value, options);
    }

    @Override
    public Snapshot delete(byte[] key, WriteOptions options) throws DBException {
        return db.delete(key, options);
    }

    @Override
    public Snapshot write(WriteBatch updates, WriteOptions options) throws DBException {
        return db.write(updates, options);
    }

    @Override
    public Snapshot getSnapshot() {
        return db.getSnapshot();
    }

    @Override
    public long[] getApproximateSizes(Range... ranges) {
        return db.getApproximateSizes(ranges);
    }

    @Override
    public String getProperty(String name) {
        return db.getProperty(name);
    }

    @Override
    public void suspendCompactions() throws InterruptedException {
        db.suspendCompactions();
    }

    @Override
    public void resumeCompactions() {
        db.resumeCompactions();
    }

    @Override
    public void compactRange(byte[] begin, byte[] end) throws DBException {
        db.compactRange(begin, end);
    }

    @Override
    public void close() throws IOException {
        LevelDBSingleton.closeDatabase();
    }

    public static void closeDatabase() throws IOException {
        if (db == null) return;
        db.close();
        db = null;
    }
}
