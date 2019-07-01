# StreamingShop

以電視購物為發想，提供 Facebook 直播賣家一個買賣平台。

可以動態且彈性的新增商品、商品限時拍賣、終止商品拍賣。

為好想工作室開發者社群於 2019 年 1 月發起的挑戰賽。

## 登入

透過 Facebook 帳號登入

<img src="https://i.imgur.com/AWurw5z.png" width="200">

第一次登入的使用者需要填寫詳細資料

<img src="https://i.imgur.com/lsH3aKL.png" width="200">

## 賣家功能

### 新增商品

點擊右下角的「+」圖示，新增一項物品。

在新建的表格中填入商品名稱、商品介紹、商品拍賣時間、價格。

<img src="https://i.imgur.com/9tE2YJ5.gif" width="200">

點擊左方照片圖示新增商品照片，照片可來自於相簿或相機。

[點擊連結觀看動畫](https://i.imgur.com/vI9oLC7.gifv)

完成後點擊新增，將商品資訊上傳到後端伺服器，商品照片則會上傳至 imgur 圖床。

<img src="https://i.imgur.com/n3HThGU.gif" width="200">

已新增但尚未開賣過的商品可以隨時修改內容。

修改完成後按下「修改」鍵覆蓋原本的內容。

<img src="https://i.imgur.com/m4xdTpl.gif" width="200">

修改錯誤也可輕鬆地恢復到上一次的狀態。

<img src="https://i.imgur.com/rRB5Ef1.gif" width="200">

### 直播設定

首先，從 Facebook App 直播介面獲取直播連結

<img src="https://i.imgur.com/8uJ55xj.gif" width="200">

點擊右上角的編輯圖示設定直播間的標題以及網址

<img src="https://i.imgur.com/K7oPfg6.gif" width="200">

設定完成後右下角的直播圖示外框會變成紅色，點擊後即開始推播直播，買家將可從直播清單看到這場直播。

開始直播後，直播圖示會變成紅色實心，編輯直播的按鈕消失。

<img src="https://i.imgur.com/35MliGg.gif" width="200">

結束直播只要再點一下紅色實心直播圖示就可以了。

<img src="https://i.imgur.com/YpGPnxc.gif" width="200">

### 開賣/下架商品

開啟直播後，已新增的商品將會出現「開賣」按鈕，按下即可拍賣該商品。

進入該直播間的買家將會看到該商品資訊，並允許下單。

<img src="https://i.imgur.com/npypfgl.gif" width="200">

拍賣時間結束，系統將會自動下架該商品。

途中也可以按下「停賣」按鈕提前下架。

<img src="https://i.imgur.com/kY62l3c.gif" width="200">

直播期間仍然可以自由的新增及上架商品！

## 買家功能

直播列表可以看現在有哪位直播主在此拍賣平台上直播，點擊即進入直播間

<img src="https://i.imgur.com/j6TIFpJ.png" width="200">

進入直播間後，上方為直播畫面，下方為當前拍賣中的商品資訊。

可以在此選擇想購買的商品數量，然後按下右下角的購物車按鈕查看商品明細。

<img src="https://i.imgur.com/xi07rAw.png" width="200">

再次按下購物車按鈕即可下單

<img src="https://i.imgur.com/LGBIIgT.png" width="200">

## 未完成的部份

- 買家/賣家商品資訊即時傳輸 (已賣商品、商品剩餘時間、賣家強制下架)

- 訂單系統

- 直播播放器控制

- 直播直版介面

- UI 美化

- 設定頁面

## Bugs

- 需改善架構設計，目前當 Activity 遭到系統回收時重啟 App 會崩潰
