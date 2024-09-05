# 🗄️ Data Migration

If you want to switch your storage method and don't want to loose your existing data, you need to follow these steps:

1. Export your existing data using `/lsz data export data.csv`
2. Change the storage method in the `config.yml` and optionally change the database credentials
3. Restart or reload your server
4. Import the old data using `/lsz data import data.csv`

