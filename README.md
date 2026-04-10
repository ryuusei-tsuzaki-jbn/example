# Servlet Debug Practice (初学者向け / MVC版 + SQL論理バグ)

このプロジェクトは「デバッグ練習専用」です。あえて初学者が起こしやすいミスを複数含めています。

## 構成（MVC）

- Controller: `src/main/java/com/example/debug/controller`
- Service: `src/main/java/com/example/debug/service`
- Model: `src/main/java/com/example/debug/model`
- Repository: `src/main/java/com/example/debug/repository`
- DB Config: `src/main/java/com/example/debug/config`
- View (JSP): `src/main/webapp/WEB-INF/jsp`

> JSP は `WEB-INF` 配下に置いており、URL 直アクセスでは表示できない構成です。

## PostgreSQL 接続情報（今回の指定）

- host: `localhost`
- port: `5432`
- database: `postgres`
- user: `postgres`
- password: `password`

## DB準備

```bash
# PostgreSQL 起動例（ローカルにない場合）
docker run --name debug-postgres -e POSTGRES_PASSWORD=password -e POSTGRES_USER=postgres -e POSTGRES_DB=postgres -p 5432:5432 -d postgres:16

# テーブル作成
psql -h localhost -p 5432 -U postgres -d postgres -f src/main/resources/db/schema.sql

# 初期データ
psql -h localhost -p 5432 -U postgres -d postgres -f src/main/resources/db/seed.sql
```

## 使い方

```bash
mvn clean package
```

生成された `target/servlet-debug-practice.war` を Tomcat 10+ に配置して動作確認してください。

## 想定フロー

1. `/login` にアクセス
2. `username/password/age` を入力して送信
3. `/profile` へ遷移

## 含まれている典型ミス（例）

- 文字列比較に `==` を使うミス（過去版の名残として README で比較観点を維持）
- 数値変換で入力値のバリデーションがない（Service）
- `null` チェック不足（Controller / Service）
- セッション属性 `visits` の初期化漏れ（Controller）
- リスト要素アクセスの範囲外参照（Controller）
- `web.xml` とアノテーション設定の不整合
- **SQLの論理エラー**
  - `username = ? AND active = true` にすべき箇所で `OR` を使っている
  - `active = true AND age >= 18` にすべき箇所で `OR` を使っている

## 目標

- スタックトレースを読み、原因箇所を特定する
- 再現手順を作る
- 修正方針（最小変更/根本修正）を比較する
- SQLの実行結果が「文法上は正しいが要件に反する」ケースを見抜く
