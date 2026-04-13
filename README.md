# Servlet Debug Practice (初学者向け / MVC版 + PostgreSQL)

このプロジェクトは **Eclipse + Tomcat 10 + Java 17** を前提にした、デバッグ練習用の小規模サンプルです。  
あえて初学者が起こしやすいミス（Java側 / SQL側）を残しています。

## 前提環境

- Eclipse IDE for Enterprise Java and Web Developers (2023-09 以降を推奨)
- JDK 17
- Apache Tomcat 10.1.x
- PostgreSQL 14+（確認例は 16）

## 構成（MVC）

- Controller: `src/main/java/com/example/debug/controller`
- Service: `src/main/java/com/example/debug/service`
- Model: `src/main/java/com/example/debug/model`
- Repository: `src/main/java/com/example/debug/repository`
- DB Config: `src/main/java/com/example/debug/config`
- View (JSP): `src/main/webapp/WEB-INF/jsp`

> JSP は `WEB-INF` 配下に置いているため、URL直アクセスでは表示できません（Servlet から `forward`）。

## PostgreSQL 接続情報（指定値）

`DbConfig` は以下固定値です。

- host: `localhost`
- port: `5432`
- database: `postgres`
- user: `postgres`
- password: `password`

実装箇所: `src/main/java/com/example/debug/config/DbConfig.java`

## DB準備

```bash
# PostgreSQL 起動例（ローカルにない場合）
docker run --name debug-postgres \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=postgres \
  -p 5432:5432 -d postgres:16

# テーブル作成
psql -h localhost -p 5432 -U postgres -d postgres -f src/main/resources/db/schema.sql

# 初期データ投入
psql -h localhost -p 5432 -U postgres -d postgres -f src/main/resources/db/seed.sql
```



### psql に直接打ち込む手順（SQLコピペ用）

1. まず接続

```bash
psql -h localhost -p 5432 -U postgres -d postgres
```

2. 以下をそのまま貼り付け（テーブル作成）

```sql
CREATE TABLE IF NOT EXISTS app_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    age INTEGER NOT NULL,
    role VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true
);
```

3. 続けて初期データ投入

```sql
INSERT INTO app_user (username, password, age, role, active) VALUES
('alice', 'alice123', 20, 'USER', true),
('bob', 'bob123', 17, 'USER', false),
('admin', 'secret', 30, 'ADMIN', true)
ON CONFLICT (username) DO NOTHING;
```

4. 投入確認

```sql
SELECT id, username, age, role, active FROM app_user ORDER BY id;
```

5. 終了

```sql
\q
```

### psql 接続で失敗したとき

- `psql: error: connection to server ... failed`
  - PostgreSQL が起動しているか確認（Dockerなら `docker ps`）
- `FATAL: password authentication failed for user "postgres"`
  - パスワードが `password` か確認
- `FATAL: database "postgres" does not exist`
  - DB名を作成するか、接続先を既存DBに変更

## Eclipse + Tomcat 10 での実行手順

1. **Import**: `File > Import > Existing Maven Projects` でこのプロジェクトを読み込み
2. **JRE確認**: `Project > Properties > Java Build Path` で JDK 17 を選択
3. **Project Facets** (`Properties > Project Facets`)
   - Dynamic Web Module: `5.0`
   - Java: `17`
4. **Tomcat Runtime紐付け** (`Properties > Targeted Runtimes`)
   - `Apache Tomcat v10.1` にチェック
5. **Server追加**: `Servers` ビューで Tomcat 10.1 を作成し、当プロジェクトを Add
6. **起動**: Server を Start して次へアクセス
   - `http://localhost:8080/servlet-debug-practice/login`

## ビルド

```bash
mvn clean package
```

生成物: `target/servlet-debug-practice.war`

## 想定フロー

1. `/login` にアクセス
2. `username/password/age` を入力して送信
3. `/profile` へ遷移

## 練習用に含めている典型ミス

### Java / Servlet 側
- `null` チェック不足（Controller / Service）
- セッション属性 `visits` の初期化漏れ（Controller）
- リスト要素アクセスの範囲外参照（Controller）
- `web.xml` とアノテーション設定の不整合
- 数値変換 (`Integer.parseInt`) の入力バリデーション不足

### SQL 側（論理エラー）
- `username = ? AND active = true` にすべき場面で `OR` を使っている
- `active = true AND age >= 18` にすべき場面で `OR` を使っている

> いずれも **文法的には正しいためコンパイル/実行できるが、結果が要件とズレる** というデバッグ教材です。

## 学習目標

- スタックトレースを読み、原因箇所を特定する
- 再現手順を作る
- 修正方針（最小変更 / 根本修正）を比較する
- SQLの実行結果が要件と一致しているか検証する


## Tomcat起動エラー（子コンテナーを開始できません）対策

以下ログは**原因そのものではなく結果メッセージ**です。  
```
重大: 子コンテナーを開始できません。
重大: 必要なサーバーコンポーネントを開始できないため、Tomcat を開始できませんでした。
```

まずは Eclipse の **Servers ビュー > Tomcat > Open Launch Configuration** と、
`<workspace>/.metadata/.log` / `catalina.*.log` の **Caused by** を確認してください。

### この構成で特に多い原因と対策

1. **Tomcat 実行JREが Java 17 になっていない**
   - 症状: `UnsupportedClassVersionError` が出る
   - 対策:
     - `Window > Preferences > Server > Runtime Environments > Apache Tomcat v10.1` の JRE を 17 に変更
     - Serversビューの Tomcat を一度削除→再作成

2. **Tomcat 10 と `javax.servlet` 系ライブラリの混在**
   - 症状: `ClassNotFoundException` / `NoClassDefFoundError`（`javax.servlet.*`）
   - 対策:
     - このプロジェクトは `jakarta.servlet` 前提。独自に追加した `javax.servlet-api` を除去
     - `WEB-INF/lib` に古い servlet 関連JARを入れない

3. **8080ポート競合**
   - 症状: `Address already in use: bind`
   - 対策:
     - Eclipseの Servers 設定で HTTP ポートを 8081 などに変更
     - 既存Tomcat/別プロセス停止

4. **Servers キャッシュ破損（Eclipse WTP）**
   - 症状: 起動時に child container エラーのみ出て詳細が見えにくい
   - 対策:
     - Serversビューで対象Tomcatを `Clean...`
     - `Project > Clean`
     - 改善しない場合、Serversプロジェクトを再生成（Tomcat再追加）

5. **DB起動前提の誤解**
   - このサンプルは起動時ではなくリクエスト時にDB接続するため、通常はDB停止でもTomcat自体は起動可能
   - ただし他コード追加で `init()` 中にDB接続した場合は起動失敗要因になる

### 最短の復旧手順（おすすめ）

1. Tomcat Runtime の JRE を **Java 17** に設定
2. Servers の Tomcat を削除して再作成
3. `Project Facets` を `Dynamic Web Module 5.0` / `Java 17` に再設定
4. `Project > Maven > Update Project...` 実行
5. `Project > Clean` と Servers の `Clean...` 実行
6. 再起動し、まだ失敗する場合は **最初の Caused by 1行** を確認

### 追加で貼ってほしいログ

切り分けを正確にするため、次のいずれかを共有してください。

- Eclipse Console の `Caused by:` から始まる3〜10行
- `<workspace>/.metadata/.log` の該当スタックトレース
- `<TOMCAT_HOME>/logs/catalina*.log` の該当箇所


## 共有ログ（2026-04-13）からの一次診断

あなたが共有してくれたログから、次は**問題なし**と判断できます。

- Tomcat: `10.1.34`（OK）
- Java: `17.0.13`（OK）
- OS/アーキ: Windows 11 / amd64（OK）
- `APR native library` のメッセージは **性能向上用ライブラリ未導入の通知**で、起動失敗の原因ではありません。

つまり、今回の停止原因は高確率で **Webアプリのデプロイ/初期化時の例外** です。  
`子コンテナーを開始できません` は要約メッセージなので、原因特定には次のログが必要です。

- `C:\pleiades\2024-12\workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp2\logs\localhost*.log`
- `C:\pleiades\2024-12\workspace\.metadata\.log`

### まず試す設定リセット（3分）

1. Eclipse `Servers` ビューで Tomcat を **Delete**（`Also remove server configuration` にチェック）
2. `Project > Clean` を実行
3. `Project > Maven > Update Project...`（`Force Update` ON）
4. Tomcat 10.1 を再登録し、プロジェクトを Add
5. 再起動

### それでも落ちる場合（最有力）

最初に出る `SEVERE` / `Caused by` の1ブロックを貼ってください。  
この1ブロックがあれば、ほぼ1回で修正箇所を特定できます。
