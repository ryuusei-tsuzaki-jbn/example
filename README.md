# Servlet Debug Practice (初学者向け / MVC版)

このプロジェクトは「デバッグ練習専用」です。あえて初学者が起こしやすいミスを複数含めています。

## 構成（MVC）

- Controller: `src/main/java/com/example/debug/controller`
- Service: `src/main/java/com/example/debug/service`
- Model: `src/main/java/com/example/debug/model`
- Repository: `src/main/java/com/example/debug/repository`
- View (JSP): `src/main/webapp/WEB-INF/jsp`

> JSP は `WEB-INF` 配下に置いており、URL 直アクセスでは表示できない構成です。

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

- 文字列比較に `==` を使っている（Repository）
- 数値変換で入力値のバリデーションがない（Service）
- `null` チェック不足（Controller / Service）
- セッション属性 `visits` の初期化漏れ（Controller）
- リスト要素アクセスの範囲外参照（Controller）
- `web.xml` とアノテーション設定の不整合

## 目標

- スタックトレースを読み、原因箇所を特定する
- 再現手順を作る
- 修正方針（最小変更/根本修正）を比較する
