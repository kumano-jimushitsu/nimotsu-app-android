# 熊野寮荷物アプリ POKKE - Android

熊野寮事務室に届く荷物の受け取り・引き渡し・管理を行うための、事務当番用の Android アプリです。登録・修正されたデータは[POKKE サーバーサイド](https://github.com/kumano-jimushitsu/luggage-manager-api)に送信され、同期・バックアップされます。

## アーキテクチャ

<p align="center">
  <img src="./images/pokke-architecture.png" width="85%" />
</p>

## 操作概要

<p align="center">
  <img src="./images/%E6%A6%82%E8%A6%81.png" width="85%" />
</p>

メイン画面から各ページに移り、操作を行います。
主な操作は以下の通りです：

- 事務当番交代
- 業者からの荷物受け取り
- 寮生への荷物引き渡し
- 泊まり事務当番時の荷物確認

## 各画面について

### メイン画面

<p align="center">
  <img src="./images/%E3%83%A1%E3%82%A4%E3%83%B3.jpeg" width="85%" />
</p>

各種操作画面へ移るボタンが配置されています。画面右の履歴一覧から、行なった操作の閲覧・削除ができます。

### 事務当番交代画面

<p align="center">
  <img src="./images/%E4%BA%8B%E5%8B%99%E5%BD%93%E4%BA%A4%E4%BB%A3.jpeg" width="85%" />
</p>

事務当番に入ったら、自分の名前を検索して事務当番に設定します。

### 荷物受け渡し画面

<p align="center">
  <img src="./images/%E5%8F%97%E3%81%91%E5%8F%96%E3%82%8A.jpeg" width="85%" />
  <img src="./images/%E3%81%9D%E3%81%AE%E4%BB%96%E5%8F%97%E3%81%91%E5%8F%96%E3%82%8A.jpeg" width="85%" />
</p>

業者から荷物が運ばれてきたら、種別選択ダイアログで荷物の種別（普通・冷蔵・冷凍・大型・不在票・その他）を選んでから、持ち主の名前を登録します。種別が「その他」の場合、200 字以内で説明を書いてください。

### 荷物引き渡し画面

<p align="center">
  <img src="./images/%E5%BC%95%E3%81%8D%E6%B8%A1%E3%81%97.jpeg" width="85%" />
  <img src="./images/%E4%BB%A3%E7%90%86%E5%8F%97%E3%81%91%E5%8F%96%E3%82%8A%E4%BA%BA%E9%81%B8%E6%8A%9E.jpeg" width="85%" />
  <img src="./images/%E4%BB%A3%E7%90%86%E5%BC%95%E3%81%8D%E6%B8%A1%E3%81%97.jpeg" width="85%" />
</p>

寮生が荷物を取りにきたら、寮生の名前を選択して荷物を引き渡します。代理人に対して荷物受け取りを行う場合、画面右下の「代理引き渡し」をオンにして、代理人の名前を選択して引き渡しします。

### 泊まり事務当番画面

<p align="center">
  <img src="./images/%E7%8F%BE%E7%89%A9%E7%A2%BA%E8%AA%8D.jpeg" width="85%" />
  <img src="./images/%E6%9C%AD%E7%A2%BA%E8%AA%8D.jpeg" width="85%" />
</p>

泊まり事務当番に入ったら、① 荷物棚の荷物と POKKE 内のデータが一致しているかどうかをチェックした後、② 荷物札が存在するかどうかチェックします。

## Slack との連携

Slack の POKKE ワークスペースに入ると、荷物が届いた時に Slack で通知を受けることができます。また、通知と一緒に荷物引き渡し用の QR コードも送られてきます。メイン画面の QR 受け取りボタンから読み取ると、自動で引き渡しを行うことができます。

## 関連リンク

- POKKE サーバーサイド（同期用）：https://github.com/kumano-jimushitsu/luggage-manager-api
- POKKE PC アプリ（庶務部管理用）：https://github.com/kumano-jimushitsu/nimotsu-app-pc
