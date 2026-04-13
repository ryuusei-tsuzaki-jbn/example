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
